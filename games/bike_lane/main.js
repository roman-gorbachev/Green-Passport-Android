const TEXT = {
  title: { ru: 'Велодорожка', be: 'Веласцежка', en: 'Bike lane' },
  hint: {
    ru: 'Свайп или тап влево и вправо — смена полосы. Объезжай машины и лужи, собирай листья',
    be: 'Свайп або тап улева і ўправа — змена паласы. Аб’язджай машыны і лужыны, збірай лісце',
    en: 'Swipe or tap left and right to change lanes. Dodge cars and puddles, collect leaves',
  },
  finished: { ru: 'Поездка окончена! Листьев: {0}', be: 'Паездка скончана! Лісця: {0}', en: 'Ride over! Leaves: {0}' },
};

const BASE_HEIGHT = 640;
const LANES = 3;
const ROAD_WIDTH_RATIO = 0.78;
const PLAYER_Y_RATIO = 0.82;
const PLAYER_SIZE = 50;
const LANE_SWITCH_SPEED = 14;
const START_SPEED = 260;
const MAX_SPEED = 620;
const SPEED_GAIN_PER_SECOND = 7;
const ROW_GAP = [170, 260];
const CAR_LENGTH = 96;
const PUDDLE_LENGTH = 54;
const LEAF_SIZE = 30;
const INVULNERABLE_SECONDS = 1.2;
const CAR_COLORS = ['#E53935', '#1E88E5', '#FDD835', '#8E24AA', '#FB8C00', '#90A4AE'];
const GAME_OVER_DELAY_MS = 700;

const stage = Engine.createStage(document.getElementById('stage'));
const particles = Engine.createParticles();
const scoreLabel = Engine.scoreLabel(document.getElementById('score'));
document.getElementById('hint').textContent = GP.t(TEXT.hint);

let colors = Engine.palette();
let state = 'ready';
let unit = 1;
let score = 0;
let elapsed = 0;
let speed = START_SPEED;
let travelled = 0;
let sinceRow = 0;
let nextRow = 0;
let lane = 1;
let laneX = 1;
let things = [];
let invulnerable = 0;
let lives = null;

stage.onResize = () => {
  unit = stage.height / BASE_HEIGHT;
};
stage.onResize();

function road() {
  const width = stage.width * ROAD_WIDTH_RATIO;
  return { left: (stage.width - width) / 2, width, laneWidth: width / LANES };
}

function laneCenter(index) {
  const { left, laneWidth } = road();
  return left + laneWidth * (index + 0.5);
}

function reset() {
  colors = Engine.palette();
  score = 0;
  scoreLabel.set(0);
  elapsed = 0;
  speed = START_SPEED;
  travelled = 0;
  sinceRow = 0;
  nextRow = 200 * unit;
  lane = 1;
  laneX = 1;
  things = [];
  invulnerable = 0;
  particles.clear();
  lives = GP.lives(document.getElementById('lives'), gameOver);
}

function start() {
  reset();
  state = 'playing';
}

function steer(direction) {
  if (state !== 'playing') return;
  const next = Engine.clamp(lane + direction, 0, LANES - 1);
  if (next !== lane) {
    lane = next;
    Engine.sound.jump();
  }
}

function spawnRow() {
  const lanes = GP.shuffle([...Array(LANES).keys()]);
  const blocked = Math.random() < 0.35 ? 2 : 1;
  lanes.slice(0, blocked).forEach((index) => {
    const isCar = Math.random() < 0.7;
    things.push({
      kind: isCar ? 'car' : 'puddle',
      lane: index,
      y: -CAR_LENGTH * unit,
      color: GP.randomItem(CAR_COLORS),
      drift: isCar ? Engine.random(0.15, 0.35) : 0,
    });
  });
  const free = lanes.slice(blocked);
  if (free.length > 0 && Math.random() < 0.8) {
    const index = GP.randomItem(free);
    for (let step = 0; step < 3; step += 1) {
      things.push({ kind: 'leaf', lane: index, y: -CAR_LENGTH * unit - step * 42 * unit, phase: Math.random() * Math.PI * 2 });
    }
  }
}

function lengthOf(thing) {
  if (thing.kind === 'car') return CAR_LENGTH * unit;
  if (thing.kind === 'puddle') return PUDDLE_LENGTH * unit;
  return LEAF_SIZE * unit;
}

function update(dt) {
  particles.update(dt);
  laneX = Engine.lerp(laneX, lane, Math.min(1, LANE_SWITCH_SPEED * dt));
  if (state !== 'playing') return;
  elapsed += dt;
  invulnerable = Math.max(0, invulnerable - dt);
  speed = Math.min(MAX_SPEED, START_SPEED + elapsed * SPEED_GAIN_PER_SECOND);
  const step = speed * unit * dt;
  travelled += step;
  sinceRow += step;
  if (sinceRow >= nextRow) {
    spawnRow();
    sinceRow = 0;
    nextRow = Engine.random(...ROW_GAP) * unit * (0.7 + 0.3 * START_SPEED / speed);
  }

  const playerY = stage.height * PLAYER_Y_RATIO;
  const playerLane = Math.round(laneX);
  for (const thing of things) {
    thing.y += step * (thing.kind === 'car' ? 1 - thing.drift : 1);
  }
  things = things.filter((thing) => {
    const half = lengthOf(thing) / 2;
    const overlaps = thing.lane === playerLane && Math.abs(thing.y - playerY) < half + PLAYER_SIZE * unit * 0.35;
    if (overlaps && thing.kind === 'leaf') {
      score += 1;
      scoreLabel.set(score);
      Engine.sound.pick();
      particles.burst(laneCenter(thing.lane), thing.y, { count: 8, colors: ['#34C77B', '#7ED957', colors.lime], speed: 150, life: 0.5, size: 5 });
      particles.text(laneCenter(thing.lane), thing.y - 24 * unit, '+1', colors.forest, 20 * unit);
      return false;
    }
    if (overlaps && invulnerable === 0) {
      invulnerable = INVULNERABLE_SECONDS;
      stage.shake(10 * unit);
      Engine.sound.bad();
      const palette = thing.kind === 'car' ? [thing.color, '#ffffff', '#333333'] : ['#4DA3FF', '#9fd3ff', '#ffffff'];
      particles.burst(laneCenter(thing.lane), playerY - 10 * unit, { count: 18, colors: palette, speed: 260, life: 0.6 });
      lives.lose();
      return thing.kind === 'puddle';
    }
    return thing.y < stage.height + CAR_LENGTH * unit;
  });
}

function drawScenery(ctx) {
  const { left, width, laneWidth } = road();
  ctx.fillStyle = colors.isDark ? '#1f3a2a' : '#8fd18a';
  ctx.fillRect(0, 0, stage.width, stage.height);
  const treeSpacing = 120 * unit;
  for (let y = (travelled % treeSpacing) - treeSpacing; y < stage.height + treeSpacing; y += treeSpacing) {
    const index = Math.round((y - travelled) / treeSpacing);
    Engine.drawGlyph(ctx, index % 2 === 0 ? '🌳' : '🌲', left * 0.5, y, Math.min(left * 0.8, 40 * unit));
    Engine.drawGlyph(ctx, index % 3 === 0 ? '🌲' : '🌳', left + width + left * 0.5, y + treeSpacing / 2, Math.min(left * 0.8, 40 * unit));
  }
  ctx.fillStyle = colors.isDark ? '#30363a' : '#59616a';
  ctx.fillRect(left, 0, width, stage.height);
  ctx.fillStyle = colors.forest;
  ctx.fillRect(left - 5 * unit, 0, 5 * unit, stage.height);
  ctx.fillRect(left + width, 0, 5 * unit, stage.height);
  ctx.fillStyle = 'rgba(255,255,255,0.65)';
  const dash = 34 * unit;
  for (let laneIndex = 1; laneIndex < LANES; laneIndex += 1) {
    const x = left + laneWidth * laneIndex - 2 * unit;
    for (let y = (travelled % (dash * 2)) - dash * 2; y < stage.height; y += dash * 2) {
      ctx.fillRect(x, y, 4 * unit, dash);
    }
  }
}

function drawCar(ctx, thing) {
  const { laneWidth } = road();
  const width = laneWidth * 0.62;
  const length = CAR_LENGTH * unit;
  const x = laneCenter(thing.lane);
  ctx.save();
  ctx.translate(x, thing.y);
  ctx.fillStyle = 'rgba(0,0,0,0.25)';
  Engine.roundRect(ctx, -width / 2 + 3 * unit, -length / 2 + 5 * unit, width, length, width * 0.25);
  ctx.fill();
  ctx.fillStyle = thing.color;
  Engine.roundRect(ctx, -width / 2, -length / 2, width, length, width * 0.25);
  ctx.fill();
  ctx.fillStyle = 'rgba(20,30,40,0.75)';
  Engine.roundRect(ctx, -width * 0.38, length * 0.08, width * 0.76, length * 0.2, width * 0.1);
  ctx.fill();
  Engine.roundRect(ctx, -width * 0.38, -length * 0.3, width * 0.76, length * 0.16, width * 0.1);
  ctx.fill();
  ctx.fillStyle = '#FFF59D';
  ctx.fillRect(-width * 0.4, length / 2 - 6 * unit, width * 0.2, 4 * unit);
  ctx.fillRect(width * 0.2, length / 2 - 6 * unit, width * 0.2, 4 * unit);
  ctx.restore();
}

function drawPuddle(ctx, thing, time) {
  const { laneWidth } = road();
  const x = laneCenter(thing.lane);
  ctx.fillStyle = colors.isDark ? 'rgba(80,150,220,0.55)' : 'rgba(77,163,255,0.6)';
  ctx.beginPath();
  ctx.ellipse(x, thing.y, laneWidth * 0.36, PUDDLE_LENGTH * unit * 0.5, 0, 0, Math.PI * 2);
  ctx.fill();
  ctx.strokeStyle = 'rgba(255,255,255,0.5)';
  ctx.lineWidth = 2 * unit;
  ctx.beginPath();
  ctx.ellipse(x, thing.y, laneWidth * 0.2 * (1 + Math.sin(time * 3) * 0.1), PUDDLE_LENGTH * unit * 0.25, 0, 0, Math.PI * 2);
  ctx.stroke();
}

function drawPlayer(ctx, time) {
  if (invulnerable > 0 && Math.floor(invulnerable * 12) % 2 === 0) return;
  const { left, laneWidth } = road();
  const x = left + laneWidth * (laneX + 0.5);
  const y = stage.height * PLAYER_Y_RATIO;
  const tilt = (lane - laneX) * 0.5;
  ctx.fillStyle = 'rgba(0,0,0,0.2)';
  ctx.beginPath();
  ctx.ellipse(x, y + PLAYER_SIZE * unit * 0.45, PLAYER_SIZE * unit * 0.35, 6 * unit, 0, 0, Math.PI * 2);
  ctx.fill();
  Engine.drawGlyph(ctx, '🚴', x, y + Math.sin(time * 14) * 1.5 * unit, PLAYER_SIZE * unit, tilt);
}

function render(dt) {
  const ctx = stage.ctx;
  const time = performance.now() / 1000;
  stage.begin(dt);
  drawScenery(ctx);
  for (const thing of things) {
    if (thing.kind === 'car') drawCar(ctx, thing);
    else if (thing.kind === 'puddle') drawPuddle(ctx, thing, time);
    else Engine.drawGlyph(ctx, '🍃', laneCenter(thing.lane), thing.y, LEAF_SIZE * unit, Math.sin(time * 4 + thing.phase) * 0.3);
  }
  drawPlayer(ctx, time);
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
  tap(x) {
    steer(x < stage.width / 2 ? -1 : 1);
  },
  swipe(direction) {
    if (direction === 'left') steer(-1);
    if (direction === 'right') steer(1);
  },
});
document.addEventListener('keydown', (event) => {
  if (event.code === 'ArrowLeft') steer(-1);
  if (event.code === 'ArrowRight') steer(1);
});

reset();
Engine.loop(update, render);
Engine.startScreen(GP.t(TEXT.title), GP.t(TEXT.hint), start);
