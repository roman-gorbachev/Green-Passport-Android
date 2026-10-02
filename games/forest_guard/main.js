const TEXT = {
  title: { ru: 'Лесной патруль', be: 'Лясны патруль', en: 'Forest guard' },
  hint: {
    ru: 'Саженцы растут сами. Туши искры и прогоняй жуков касанием, пока они не навредили лесу',
    be: 'Саджанцы растуць самі. Тушы іскры і праганяй жукоў дотыкам, пакуль яны не нашкодзілі лесу',
    en: 'Saplings grow on their own. Tap sparks and beetles before they harm the forest',
  },
  finished: { ru: 'Патруль окончен! Деревьев: {0}', be: 'Патруль скончаны! Дрэў: {0}', en: 'Patrol over! Trees grown: {0}' },
};

const GRID = 4;
const STAGES = ['🌰', '🌱', '🌿', '🌲', '🌳'];
const GROWN_STAGE = STAGES.length - 1;
const STAGE_SECONDS = 2.4;
const CELEBRATE_SECONDS = 1;
const ASH_SECONDS = 2;
const SPARK_FUSE_START = 2.8;
const SPARK_FUSE_MIN = 1.5;
const BUG_FUSE = 2.8;
const BUG_DAMAGE_STAGES = 2;
const THREAT_GAP_START = [1.6, 2.4];
const THREAT_GAP_MIN = [0.45, 0.8];
const DIFFICULTY_SECONDS = 100;
const POP_SECONDS = 0.35;
const GAME_OVER_DELAY_MS = 700;

const stage = Engine.createStage(document.getElementById('stage'));
const particles = Engine.createParticles();
const scoreLabel = Engine.scoreLabel(document.getElementById('score'));
document.getElementById('hint').textContent = GP.t(TEXT.hint);

let colors = Engine.palette();
let state = 'ready';
let score = 0;
let elapsed = 0;
let nextThreat = 0;
let plots = [];
let board = { x: 0, y: 0, cell: 0, gap: 0 };
let lives = null;

stage.onResize = () => {
  const size = Math.min(stage.width, stage.height) * 0.94;
  const gap = size * 0.035;
  board = {
    cell: (size - gap * (GRID + 1)) / GRID,
    gap,
    x: (stage.width - size) / 2 + gap,
    y: (stage.height - size) / 2 + gap,
  };
};
stage.onResize();

function newPlot(index) {
  return { index, stage: 0, growth: Math.random() * 0.6, threat: null, ash: 0, celebrate: 0, pop: 0, phase: Math.random() * Math.PI * 2 };
}

function reset() {
  colors = Engine.palette();
  score = 0;
  scoreLabel.set(0);
  elapsed = 0;
  nextThreat = 1.5;
  particles.clear();
  plots = Array.from({ length: GRID * GRID }, (_, index) => newPlot(index));
  lives = GP.lives(document.getElementById('lives'), gameOver);
}

function start() {
  reset();
  state = 'playing';
}

function center(plot) {
  const column = plot.index % GRID;
  const row = Math.floor(plot.index / GRID);
  return {
    x: board.x + column * (board.cell + board.gap) + board.cell / 2,
    y: board.y + row * (board.cell + board.gap) + board.cell / 2,
  };
}

function difficulty() {
  return Math.min(1, elapsed / DIFFICULTY_SECONDS);
}

function spawnThreat() {
  const candidates = plots.filter((plot) => !plot.threat && plot.ash === 0 && plot.celebrate === 0);
  if (candidates.length === 0) return;
  const plot = GP.randomItem(candidates);
  const isSpark = Math.random() < 0.55;
  const fuse = isSpark ? Engine.lerp(SPARK_FUSE_START, SPARK_FUSE_MIN, difficulty()) : BUG_FUSE;
  plot.threat = { kind: isSpark ? 'spark' : 'bug', left: fuse, total: fuse, appear: 0 };
}

function grow(plot, dt) {
  if (plot.threat || plot.ash > 0 || plot.celebrate > 0) return;
  plot.growth += dt / STAGE_SECONDS;
  if (plot.growth < 1) return;
  plot.growth = 0;
  plot.stage += 1;
  plot.pop = POP_SECONDS;
  if (plot.stage === GROWN_STAGE) {
    plot.celebrate = CELEBRATE_SECONDS;
    score += 1;
    scoreLabel.set(score);
    Engine.sound.good();
    const point = center(plot);
    particles.burst(point.x, point.y, { count: 22, colors: ['#34C77B', colors.lime, '#FFD54F', '#ffffff'], speed: 300, life: 0.9 });
    particles.text(point.x, point.y - board.cell * 0.45, '+1', colors.forest, board.cell * 0.28);
  }
}

function strike(plot) {
  const point = center(plot);
  const { kind } = plot.threat;
  plot.threat = null;
  if (kind === 'spark') {
    plot.ash = ASH_SECONDS;
    plot.stage = 0;
    plot.growth = 0;
    stage.shake(10);
    Engine.sound.bad();
    particles.burst(point.x, point.y, { count: 24, colors: ['#FF7043', '#FFB300', '#5d5d5d'], speed: 260, gravity: -120, life: 0.9 });
    lives.lose();
  } else {
    plot.stage = Math.max(0, plot.stage - BUG_DAMAGE_STAGES);
    plot.growth = 0;
    plot.pop = POP_SECONDS;
    Engine.sound.bad();
    particles.burst(point.x, point.y, { count: 10, colors: ['#8d6e63', '#6d4c41'], speed: 160, life: 0.5 });
    particles.burst(point.x, point.y, { count: 1, glyph: '🐛', speed: 260, gravity: -200, life: 0.8, size: 10, angle: -Math.PI / 2, spread: 0.6 });
  }
}

function update(dt) {
  particles.update(dt);
  for (const plot of plots) {
    plot.pop = Math.max(0, plot.pop - dt);
    if (plot.threat) plot.threat.appear = Math.min(1, plot.threat.appear + dt * 5);
  }
  if (state !== 'playing') return;

  elapsed += dt;
  nextThreat -= dt;
  if (nextThreat <= 0) {
    spawnThreat();
    nextThreat = Engine.random(
      Engine.lerp(THREAT_GAP_START[0], THREAT_GAP_MIN[0], difficulty()),
      Engine.lerp(THREAT_GAP_START[1], THREAT_GAP_MIN[1], difficulty()),
    );
  }

  for (const plot of plots) {
    if (plot.ash > 0) {
      plot.ash = Math.max(0, plot.ash - dt);
      if (Math.random() < dt * 6) {
        const point = center(plot);
        particles.burst(point.x + Engine.random(-10, 10), point.y, { count: 1, colors: ['rgba(120,120,120,0.6)'], speed: 30, gravity: -80, life: 1, size: 10 });
      }
      if (plot.ash === 0) plot.pop = POP_SECONDS;
      continue;
    }
    if (plot.celebrate > 0) {
      plot.celebrate = Math.max(0, plot.celebrate - dt);
      if (plot.celebrate === 0) {
        Object.assign(plot, newPlot(plot.index), { pop: POP_SECONDS, growth: 0 });
      }
      continue;
    }
    if (plot.threat) {
      plot.threat.left -= dt;
      if (plot.threat.left <= 0) strike(plot);
      continue;
    }
    grow(plot, dt);
  }
}

function tap(x, y) {
  if (state !== 'playing') return;
  const plot = plots.find((candidate) => {
    const point = center(candidate);
    return Math.abs(point.x - x) <= board.cell / 2 + board.gap / 2 && Math.abs(point.y - y) <= board.cell / 2 + board.gap / 2;
  });
  if (!plot || !plot.threat) return;
  const point = center(plot);
  if (plot.threat.kind === 'spark') {
    Engine.sound.splash();
    particles.burst(point.x, point.y, { count: 18, colors: ['#4DA3FF', '#9fd3ff', '#ffffff'], speed: 220, life: 0.6 });
    particles.burst(point.x, point.y - board.cell * 0.2, { count: 1, glyph: '💧', speed: 60, gravity: 300, life: 0.5, size: 9, angle: -Math.PI / 2, spread: 0.2 });
  } else {
    Engine.sound.pop(2);
    particles.burst(point.x, point.y, { count: 1, glyph: '🐞', speed: 320, gravity: -150, life: 0.7, size: 10, angle: -Math.PI / 2, spread: 1.2 });
  }
  plot.threat = null;
  plot.pop = POP_SECONDS;
}

function drawPlot(ctx, plot, time) {
  const point = center(plot);
  const cell = board.cell;
  const left = point.x - cell / 2;
  const top = point.y - cell / 2;
  ctx.fillStyle = plot.ash > 0 ? (colors.isDark ? '#2a2a2a' : '#6b6b6b') : colors.isDark ? '#4a3a2c' : '#9b7653';
  Engine.roundRect(ctx, left, top, cell, cell, cell * 0.22);
  ctx.fill();
  ctx.fillStyle = 'rgba(0,0,0,0.12)';
  Engine.roundRect(ctx, left + cell * 0.12, top + cell * 0.62, cell * 0.76, cell * 0.2, cell * 0.1);
  ctx.fill();

  if (plot.ash === 0 && !plot.threat && plot.celebrate === 0) {
    ctx.strokeStyle = colors.lime;
    ctx.lineWidth = Math.max(3, cell * 0.04);
    ctx.lineCap = 'round';
    ctx.beginPath();
    ctx.arc(point.x, point.y, cell * 0.44, -Math.PI / 2, -Math.PI / 2 + Math.PI * 2 * plot.growth);
    ctx.stroke();
  }

  if (plot.ash === 0) {
    const popScale = plot.pop > 0 ? Engine.ease.outBack(1 - plot.pop / POP_SECONDS) : 1;
    const celebrateBounce = plot.celebrate > 0 ? 1 + Math.sin((1 - plot.celebrate / CELEBRATE_SECONDS) * Math.PI) * 0.25 : 1;
    const size = cell * (0.42 + plot.stage * 0.08);
    const sway = Math.sin(time * 2 + plot.phase) * 0.06 * (plot.stage + 1) / STAGES.length;
    Engine.drawGlyph(ctx, STAGES[plot.stage], point.x, point.y + cell * 0.02, size, sway, popScale * celebrateBounce);
  }

  if (plot.threat) {
    const threat = plot.threat;
    const urgency = 1 - threat.left / threat.total;
    ctx.strokeStyle = colors.error;
    ctx.lineWidth = Math.max(3, cell * 0.05);
    ctx.lineCap = 'round';
    ctx.beginPath();
    ctx.arc(point.x, point.y, cell * 0.44, -Math.PI / 2, -Math.PI / 2 + Math.PI * 2 * (1 - urgency));
    ctx.stroke();
    const appear = Engine.ease.outBack(threat.appear);
    if (threat.kind === 'spark') {
      const flicker = 1 + Math.sin(time * 30) * 0.08;
      Engine.drawGlyph(ctx, '🔥', point.x + cell * 0.18, point.y + cell * 0.12, cell * (0.28 + urgency * 0.3), 0, appear * flicker);
    } else {
      const crawl = Math.sin(time * 6 + plot.phase) * cell * 0.12;
      Engine.drawGlyph(ctx, '🐛', point.x + crawl, point.y + cell * 0.22, cell * 0.34, Math.cos(time * 6 + plot.phase) * 0.4, appear);
    }
  }
}

function render(dt) {
  const ctx = stage.ctx;
  const time = performance.now() / 1000;
  stage.begin(dt);
  ctx.fillStyle = colors.mintHigh;
  ctx.fillRect(0, 0, stage.width, stage.height);
  ctx.fillStyle = colors.isDark ? 'rgba(255,255,255,0.03)' : 'rgba(255,255,255,0.25)';
  for (let index = 0; index < 40; index += 1) {
    const x = ((index * 97) % 100) / 100 * stage.width;
    const y = ((index * 53) % 100) / 100 * stage.height;
    ctx.beginPath();
    ctx.ellipse(x, y, 10, 4, Math.sin(time + index) * 0.2, 0, Math.PI * 2);
    ctx.fill();
  }
  for (const plot of plots) drawPlot(ctx, plot, time);
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
