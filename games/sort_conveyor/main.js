const TEXT = {
  title: { ru: 'Сортировочный конвейер', be: 'Сартавальны канвеер', en: 'Sorting line' },
  hint: {
    ru: 'Отходы едут по ленте. Нажимай на нужный бак, пока подсвеченный предмет не уехал. Серия верных ответов даёт ×2 и ×3',
    be: 'Адходы едуць па стужцы. Націскай на патрэбны бак, пакуль падсвечаны прадмет не з\'ехаў. Серыя правільных адказаў дае ×2 і ×3',
    en: 'Waste rides the belt. Tap the right bin before the highlighted item falls off. A streak gives ×2 and ×3',
  },
  finished: { ru: 'Смена окончена! Очки: {0}', be: 'Змена скончана! Ачкі: {0}', en: 'Shift over! Score: {0}' },
};

const TYPES = [
  { id: 'glass', color: '#34C77B', icon: '🍾', title: { ru: 'Стекло', be: 'Шкло', en: 'Glass' }, items: ['🍾', '🫙', '🍷', '🥛'] },
  { id: 'metal', color: '#8E7CF0', icon: '🥫', title: { ru: 'Металл', be: 'Метал', en: 'Metal' }, items: ['🥫', '🔩', '🗝️', '🥄'] },
  { id: 'paper', color: '#4DA3FF', icon: '📰', title: { ru: 'Бумага', be: 'Папера', en: 'Paper' }, items: ['📰', '📦', '📄', '📚'] },
  { id: 'plastic', color: '#FF9F43', icon: '🧴', title: { ru: 'Пластик', be: 'Пластык', en: 'Plastic' }, items: ['🧴', '🛍️', '🥤', '🪥'] },
];

const BASE_HEIGHT = 640;
const BELT_Y_RATIO = 0.3;
const BELT_HEIGHT = 58;
const ITEM_SIZE = 44;
const START_SPEED = 70;
const MAX_SPEED = 210;
const SPEED_GAIN_PER_SECOND = 2.6;
const SPAWN_GAP = [120, 200];
const FLIGHT_SECONDS = 0.32;
const BIN_HEIGHT = 128;
const BIN_GAP = 10;
const BIN_BOTTOM_MARGIN = 14;
const COMBO_DOUBLE = 5;
const COMBO_TRIPLE = 10;
const GAME_OVER_DELAY_MS = 700;

const stage = Engine.createStage(document.getElementById('stage'));
const particles = Engine.createParticles();
const scoreLabel = Engine.scoreLabel(document.getElementById('score'));
document.getElementById('hint').textContent = GP.t(TEXT.hint);

let colors = Engine.palette();
let state = 'ready';
let unit = 1;
let score = 0;
let combo = 0;
let speed = START_SPEED;
let elapsed = 0;
let beltOffset = 0;
let sinceSpawn = 0;
let nextGap = 0;
let items = [];
let flights = [];
let bins = [];
let lives = null;

function multiplier() {
  return combo >= COMBO_TRIPLE ? 3 : combo >= COMBO_DOUBLE ? 2 : 1;
}

function layout() {
  unit = stage.height / BASE_HEIGHT;
  const width = (stage.width - BIN_GAP * unit * (TYPES.length + 1)) / TYPES.length;
  const height = BIN_HEIGHT * unit;
  const top = stage.height - height - BIN_BOTTOM_MARGIN * unit;
  bins = TYPES.map((type, index) => ({
    type,
    x: BIN_GAP * unit + index * (width + BIN_GAP * unit),
    y: top,
    width,
    height,
    lid: bins[index]?.lid ?? 0,
    shake: bins[index]?.shake ?? 0,
  }));
}

stage.onResize = layout;
layout();

function beltY() {
  return stage.height * BELT_Y_RATIO;
}

function reset() {
  colors = Engine.palette();
  score = 0;
  combo = 0;
  scoreLabel.set(0);
  speed = START_SPEED;
  elapsed = 0;
  items = [];
  flights = [];
  particles.clear();
  sinceSpawn = 0;
  nextGap = 0;
  lives = GP.lives(document.getElementById('lives'), gameOver);
}

function start() {
  reset();
  state = 'playing';
}

function spawn() {
  const type = GP.randomItem(TYPES);
  items.push({ type, glyph: GP.randomItem(type.items), x: -ITEM_SIZE * unit, wobble: Math.random() * Math.PI * 2, appear: 0 });
}

function activeItem() {
  return items.reduce((best, item) => (!best || item.x > best.x ? item : best), null);
}

function sortInto(bin) {
  const item = activeItem();
  if (!item || state !== 'playing') return;
  items = items.filter((other) => other !== item);
  flights.push({ item, bin, fromX: item.x, fromY: beltY() - ITEM_SIZE * unit * 0.45, t: 0 });
  Engine.sound.jump();
}

function land(flight) {
  if (state !== 'playing') return;
  const { item, bin } = flight;
  const centerX = bin.x + bin.width / 2;
  if (item.type === bin.type) {
    combo += 1;
    const points = multiplier();
    score += points;
    scoreLabel.set(score);
    bin.lid = 1;
    Engine.sound.good();
    particles.burst(centerX, bin.y + 10 * unit, { count: 16, colors: [bin.type.color, colors.lime, '#ffffff'], speed: 260, angle: -Math.PI / 2, spread: Math.PI * 0.9 });
    particles.text(centerX, bin.y - 18 * unit, '+' + points, colors.forest, 24 * unit);
    if (combo === COMBO_DOUBLE || combo === COMBO_TRIPLE) {
      particles.text(stage.width / 2, beltY() + 70 * unit, '×' + multiplier(), colors.forest, 40 * unit);
    }
  } else {
    combo = 0;
    bin.shake = 1;
    Engine.sound.bad();
    stage.shake(8 * unit);
    particles.text(centerX, bin.y - 18 * unit, '✕', colors.error, 30 * unit);
    lives.lose();
  }
}

function update(dt) {
  particles.update(dt);
  for (const bin of bins) {
    bin.lid = Math.max(0, bin.lid - dt * 3);
    bin.shake = Math.max(0, bin.shake - dt * 3);
  }
  flights = flights.filter((flight) => {
    flight.t += dt / FLIGHT_SECONDS;
    if (flight.t >= 1) {
      land(flight);
      return false;
    }
    return true;
  });
  if (state !== 'playing') return;

  elapsed += dt;
  speed = Math.min(MAX_SPEED, START_SPEED + elapsed * SPEED_GAIN_PER_SECOND);
  const step = speed * unit * dt;
  beltOffset += step;
  sinceSpawn += step;
  if (sinceSpawn >= nextGap) {
    spawn();
    sinceSpawn = 0;
    nextGap = Engine.random(...SPAWN_GAP) * unit;
  }
  for (const item of items) {
    item.x += step;
    item.appear = Math.min(1, item.appear + dt * 4);
  }
  items = items.filter((item) => {
    if (item.x < stage.width + ITEM_SIZE * unit * 0.2) return true;
    combo = 0;
    Engine.sound.bad();
    stage.shake(6 * unit);
    particles.burst(stage.width - 10 * unit, beltY(), { count: 12, colors: [item.type.color], speed: 200, life: 0.6 });
    lives.lose();
    return false;
  });
}

function drawBelt(ctx) {
  const y = beltY();
  const height = BELT_HEIGHT * unit;
  const radius = height / 2;
  ctx.fillStyle = colors.isDark ? '#2c3430' : '#56625c';
  Engine.roundRect(ctx, 4 * unit, y - height / 2, stage.width - 8 * unit, height, radius);
  ctx.fill();
  ctx.save();
  Engine.roundRect(ctx, 4 * unit, y - height / 2 + 6 * unit, stage.width - 8 * unit, height - 12 * unit, radius);
  ctx.clip();
  ctx.fillStyle = colors.isDark ? '#3b4540' : '#6f7c75';
  const stripe = 28 * unit;
  for (let x = (beltOffset % (stripe * 2)) - stripe * 2; x < stage.width; x += stripe * 2) {
    ctx.beginPath();
    ctx.moveTo(x, y + height / 2);
    ctx.lineTo(x + stripe, y + height / 2);
    ctx.lineTo(x + stripe * 1.6, y - height / 2);
    ctx.lineTo(x + stripe * 0.6, y - height / 2);
    ctx.closePath();
    ctx.fill();
  }
  ctx.restore();
  for (const rollerX of [radius + 4 * unit, stage.width - radius - 4 * unit]) {
    ctx.save();
    ctx.translate(rollerX, y);
    ctx.rotate(beltOffset / radius);
    ctx.fillStyle = colors.isDark ? '#1c2220' : '#3f4944';
    ctx.beginPath();
    ctx.arc(0, 0, radius * 0.7, 0, Math.PI * 2);
    ctx.fill();
    ctx.strokeStyle = '#9aa5a0';
    ctx.lineWidth = 3 * unit;
    ctx.beginPath();
    ctx.moveTo(-radius * 0.5, 0);
    ctx.lineTo(radius * 0.5, 0);
    ctx.moveTo(0, -radius * 0.5);
    ctx.lineTo(0, radius * 0.5);
    ctx.stroke();
    ctx.restore();
  }
}

function drawBin(ctx, bin) {
  const shakeX = Math.sin(bin.shake * 30) * bin.shake * 8 * unit;
  const x = bin.x + shakeX;
  const lidLift = Engine.ease.outBack(bin.lid) * 14 * unit;
  ctx.save();
  ctx.fillStyle = bin.shake > 0 ? colors.error : bin.type.color;
  Engine.roundRect(ctx, x + 4 * unit, bin.y + 16 * unit, bin.width - 8 * unit, bin.height - 16 * unit, 16 * unit);
  ctx.fill();
  ctx.fillStyle = 'rgba(255,255,255,0.18)';
  for (let index = 1; index <= 2; index += 1) {
    ctx.fillRect(x + (bin.width * index) / 3 - 2 * unit, bin.y + 34 * unit, 4 * unit, bin.height - 60 * unit);
  }
  ctx.fillStyle = bin.shake > 0 ? colors.error : bin.type.color;
  Engine.roundRect(ctx, x, bin.y + 4 * unit - lidLift, bin.width, 16 * unit, 8 * unit);
  ctx.fill();
  ctx.fillStyle = 'rgba(0,0,0,0.15)';
  ctx.fill();
  Engine.drawGlyph(ctx, bin.type.icon, x + bin.width / 2, bin.y + bin.height * 0.48, 32 * unit);
  ctx.fillStyle = '#ffffff';
  ctx.font = `700 ${Math.max(11, 13 * unit)}px -apple-system, system-ui, Roboto, sans-serif`;
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText(GP.t(bin.type.title), x + bin.width / 2, bin.y + bin.height - 20 * unit);
  ctx.restore();
}

function render(dt) {
  const ctx = stage.ctx;
  stage.begin(dt);
  ctx.fillStyle = colors.card;
  ctx.fillRect(0, 0, stage.width, stage.height);
  drawBelt(ctx);

  const active = activeItem();
  const time = performance.now() / 1000;
  for (const item of items) {
    const y = beltY() - ITEM_SIZE * unit * 0.45 + Math.sin(time * 8 + item.wobble) * 1.5 * unit;
    if (item === active && state === 'playing') {
      const pulse = 1 + Math.sin(time * 8) * 0.08;
      ctx.strokeStyle = colors.lime;
      ctx.lineWidth = 4 * unit;
      ctx.beginPath();
      ctx.arc(item.x, y, ITEM_SIZE * unit * 0.62 * pulse, 0, Math.PI * 2);
      ctx.stroke();
    }
    Engine.drawGlyph(ctx, item.glyph, item.x, y, ITEM_SIZE * unit, 0, Engine.ease.outBack(item.appear));
  }

  for (const flight of flights) {
    const t = Engine.ease.outCubic(flight.t);
    const toX = flight.bin.x + flight.bin.width / 2;
    const toY = flight.bin.y + 10 * unit;
    const peakY = Math.min(flight.fromY, toY) - 80 * unit;
    const x = Engine.lerp(flight.fromX, toX, t);
    const y = (1 - t) * (1 - t) * flight.fromY + 2 * (1 - t) * t * peakY + t * t * toY;
    Engine.drawGlyph(ctx, flight.item.glyph, x, y, ITEM_SIZE * unit, t * Math.PI * 2, 1 - t * 0.35);
  }

  for (const bin of bins) drawBin(ctx, bin);

  if (multiplier() > 1 && state === 'playing') {
    ctx.fillStyle = colors.forest;
    ctx.font = `800 ${22 * unit}px -apple-system, system-ui, Roboto, sans-serif`;
    ctx.textAlign = 'center';
    ctx.fillText('×' + multiplier(), stage.width / 2, beltY() - BELT_HEIGHT * unit - 6 * unit);
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

Engine.input(stage.canvas, {
  down(x, y) {
    const bin = bins.find((candidate) => x >= candidate.x && x <= candidate.x + candidate.width && y >= candidate.y - 20 * unit);
    if (bin) sortInto(bin);
  },
});
document.addEventListener('keydown', (event) => {
  const index = Number(event.key) - 1;
  if (bins[index]) sortInto(bins[index]);
});

reset();
Engine.loop(update, render);
Engine.startScreen(GP.t(TEXT.title), GP.t(TEXT.hint), start);
