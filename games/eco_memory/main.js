const TEXT = {
  title: { ru: 'Эко-мемори', be: 'Эка-мэмары', en: 'Eco memory' },
  hint: {
    ru: 'Открывай по две карточки и находи пары. На каждый уровень — 60 секунд',
    be: 'Адкрывай па дзве карткі і знаходзь пары. На кожны ўзровень — 60 секунд',
    en: 'Flip two cards at a time and find the pairs. Each level has 60 seconds',
  },
  level: { ru: 'Уровень {0}', be: 'Узровень {0}', en: 'Level {0}' },
  finished: { ru: 'Время вышло! Пар найдено: {0}', be: 'Час скончыўся! Знойдзена пар: {0}', en: 'Time is up! Pairs found: {0}' },
};

const LEVELS = [
  { columns: 3, rows: 4 },
  { columns: 4, rows: 4 },
  { columns: 4, rows: 5 },
];
const FACES = ['🌳', '♻️', '🐝', '💧', '☀️', '🚲', '🍃', '🌻', '🐟', '🔋', '🌍', '🦔'];
const LEVEL_SECONDS = 60;
const REPEAT_LEVEL_PENALTY_SECONDS = 10;
const MIN_LEVEL_SECONDS = 30;
const FLIP_SECONDS = 0.22;
const MISMATCH_DELAY = 0.7;
const MATCH_DELAY = 0.25;
const TIMER_BAR_HEIGHT = 8;
const LEVEL_BANNER_SECONDS = 1.2;
const GAME_OVER_DELAY_MS = 700;

const stage = Engine.createStage(document.getElementById('stage'));
const particles = Engine.createParticles();
const scoreLabel = Engine.scoreLabel(document.getElementById('score'));
const timerLabel = document.getElementById('lives');
document.getElementById('hint').textContent = GP.t(TEXT.hint);

let colors = Engine.palette();
let state = 'ready';
let score = 0;
let levelIndex = 0;
let round = 0;
let timeLeft = LEVEL_SECONDS;
let levelTime = LEVEL_SECONDS;
let cards = [];
let open = [];
let resolveIn = 0;
let banner = 0;
let grid = { x: 0, y: 0, cell: { width: 0, height: 0 }, gap: 0 };

function level() {
  return LEVELS[levelIndex];
}

function layout() {
  const { columns, rows } = level();
  const top = TIMER_BAR_HEIGHT * 3;
  const gap = Math.min(stage.width, stage.height) * 0.025;
  const width = (stage.width - gap * (columns + 1)) / columns;
  const height = Math.min(width * 1.25, (stage.height - top - gap * (rows + 1)) / rows);
  const cellWidth = Math.min(width, height / 1.25);
  const totalWidth = cellWidth * columns + gap * (columns - 1);
  const totalHeight = height * rows + gap * (rows - 1);
  grid = {
    gap,
    cell: { width: cellWidth, height },
    x: (stage.width - totalWidth) / 2,
    y: top + (stage.height - top - totalHeight) / 2,
  };
}

stage.onResize = layout;

function deal() {
  const { columns, rows } = level();
  const pairs = (columns * rows) / 2;
  const faces = GP.shuffle(FACES).slice(0, pairs);
  cards = GP.shuffle([...faces, ...faces]).map((face, index) => ({ index, face, flip: 0, target: 0, matched: false, pop: 0, deal: index * 0.03 }));
  open = [];
  resolveIn = 0;
  layout();
}

function reset() {
  colors = Engine.palette();
  score = 0;
  scoreLabel.set(0);
  levelIndex = 0;
  round = 0;
  levelTime = LEVEL_SECONDS;
  timeLeft = levelTime;
  particles.clear();
  deal();
}

function start() {
  reset();
  state = 'playing';
  banner = LEVEL_BANNER_SECONDS;
}

function cardRect(card) {
  const { columns } = level();
  const column = card.index % columns;
  const row = Math.floor(card.index / columns);
  return {
    x: grid.x + column * (grid.cell.width + grid.gap),
    y: grid.y + row * (grid.cell.height + grid.gap),
    width: grid.cell.width,
    height: grid.cell.height,
  };
}

function nextLevel() {
  if (levelIndex < LEVELS.length - 1) {
    levelIndex += 1;
  } else {
    round += 1;
  }
  levelTime = Math.max(MIN_LEVEL_SECONDS, LEVEL_SECONDS - round * REPEAT_LEVEL_PENALTY_SECONDS);
  timeLeft = levelTime;
  banner = LEVEL_BANNER_SECONDS;
  Engine.sound.over();
  particles.burst(stage.width / 2, stage.height / 2, { count: 40, colors: ['#34C77B', colors.lime, '#FFD54F', '#FF8FB1', '#ffffff'], speed: 420, life: 1.1 });
  deal();
}

function resolve() {
  const [first, second] = open;
  if (first.face === second.face) {
    first.matched = true;
    second.matched = true;
    first.pop = 0.35;
    second.pop = 0.35;
    score += 1;
    scoreLabel.set(score);
    Engine.sound.good();
    for (const card of [first, second]) {
      const rect = cardRect(card);
      particles.burst(rect.x + rect.width / 2, rect.y + rect.height / 2, { count: 12, colors: ['#34C77B', colors.lime, '#ffffff'], speed: 200, life: 0.6 });
    }
    if (cards.every((card) => card.matched)) setTimeout(nextLevel, 500);
  } else {
    first.target = 0;
    second.target = 0;
    Engine.sound.bad();
  }
  open = [];
}

function tap(x, y) {
  if (state !== 'playing' || open.length === 2 || banner > 0) return;
  const card = cards.find((candidate) => {
    const rect = cardRect(candidate);
    return x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height;
  });
  if (!card || card.matched || card.target === 1) return;
  card.target = 1;
  open.push(card);
  Engine.sound.pop(1);
  if (open.length === 2) {
    resolveIn = open[0].face === open[1].face ? MATCH_DELAY : MISMATCH_DELAY;
  }
}

function update(dt) {
  particles.update(dt);
  for (const card of cards) {
    card.flip += Math.sign(card.target - card.flip) * Math.min(Math.abs(card.target - card.flip), dt / FLIP_SECONDS);
    card.pop = Math.max(0, card.pop - dt);
    card.deal = Math.max(0, card.deal - dt);
  }
  banner = Math.max(0, banner - dt);
  if (state !== 'playing') return;
  if (open.length === 2) {
    resolveIn -= dt;
    if (resolveIn <= 0) resolve();
  }
  if (banner === 0) timeLeft = Math.max(0, timeLeft - dt);
  timerLabel.textContent = '⏱ ' + Math.ceil(timeLeft);
  if (timeLeft === 0) gameOver();
}

function drawCard(ctx, card) {
  const rect = cardRect(card);
  const showingFace = card.flip > 0.5;
  const squash = Math.abs(Math.cos(card.flip * Math.PI));
  const popScale = card.pop > 0 ? 1 + Math.sin((1 - card.pop / 0.35) * Math.PI) * 0.12 : 1;
  const dealScale = card.deal > 0 ? 0 : 1;
  ctx.save();
  ctx.translate(rect.x + rect.width / 2, rect.y + rect.height / 2);
  ctx.scale(squash * popScale * dealScale, popScale * dealScale);
  const radius = rect.width * 0.16;
  if (showingFace) {
    ctx.fillStyle = card.matched ? colors.mint : colors.card;
    Engine.roundRect(ctx, -rect.width / 2, -rect.height / 2, rect.width, rect.height, radius);
    ctx.fill();
    ctx.strokeStyle = card.matched ? colors.forest : colors.mintHigh;
    ctx.lineWidth = 3;
    ctx.stroke();
    ctx.restore();
    ctx.save();
    ctx.translate(rect.x + rect.width / 2, rect.y + rect.height / 2);
    ctx.scale(squash * popScale, popScale);
    Engine.drawGlyph(ctx, card.face, 0, 0, rect.width * 0.55);
  } else {
    const gradient = ctx.createLinearGradient(-rect.width / 2, -rect.height / 2, rect.width / 2, rect.height / 2);
    gradient.addColorStop(0, '#34C77B');
    gradient.addColorStop(1, colors.forest);
    ctx.fillStyle = gradient;
    Engine.roundRect(ctx, -rect.width / 2, -rect.height / 2, rect.width, rect.height, radius);
    ctx.fill();
    ctx.strokeStyle = 'rgba(255,255,255,0.25)';
    ctx.lineWidth = 2;
    Engine.roundRect(ctx, -rect.width / 2 + 6, -rect.height / 2 + 6, rect.width - 12, rect.height - 12, radius * 0.7);
    ctx.stroke();
    ctx.globalAlpha = 0.9;
    Engine.drawGlyph(ctx, '🍃', 0, 0, rect.width * 0.38);
    ctx.globalAlpha = 1;
  }
  ctx.restore();
}

function render(dt) {
  const ctx = stage.ctx;
  stage.begin(dt);
  ctx.fillStyle = colors.card;
  ctx.fillRect(0, 0, stage.width, stage.height);
  const progress = timeLeft / levelTime;
  ctx.fillStyle = colors.mintHigh;
  Engine.roundRect(ctx, 12, TIMER_BAR_HEIGHT, stage.width - 24, TIMER_BAR_HEIGHT, TIMER_BAR_HEIGHT / 2);
  ctx.fill();
  ctx.fillStyle = progress < 0.2 ? colors.error : colors.forest;
  Engine.roundRect(ctx, 12, TIMER_BAR_HEIGHT, Math.max(TIMER_BAR_HEIGHT, (stage.width - 24) * progress), TIMER_BAR_HEIGHT, TIMER_BAR_HEIGHT / 2);
  ctx.fill();
  for (const card of cards) drawCard(ctx, card);
  if (banner > 0 && state === 'playing') {
    const alpha = Math.min(1, banner / 0.3);
    ctx.globalAlpha = alpha;
    ctx.fillStyle = colors.forest;
    ctx.font = `800 ${Math.round(stage.width * 0.1)}px -apple-system, system-ui, Roboto, sans-serif`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(GP.t(TEXT.level, levelIndex + 1 + round), stage.width / 2, stage.height / 2);
    ctx.globalAlpha = 1;
  }
  particles.render(ctx);
}

function gameOver() {
  if (state !== 'playing') return;
  state = 'over';
  Engine.sound.over();
  GP.finish(score);
  setTimeout(() => GP.showResult(GP.t(TEXT.finished, score), start), GAME_OVER_DELAY_MS);
}

Engine.input(stage.canvas, { down: tap });

reset();
Engine.loop(update, render);
Engine.startScreen(GP.t(TEXT.title), GP.t(TEXT.hint), start);
