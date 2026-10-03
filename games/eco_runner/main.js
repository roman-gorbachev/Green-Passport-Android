const TEXT = {
  title: { ru: 'Эко-забег', be: 'Эка-забег', en: 'Eco run' },
  hint: {
    ru: 'Собирай листья и перепрыгивай мусор. Тап — прыжок, тап в воздухе — двойной прыжок',
    be: 'Збірай лісце і пераскоквай смецце. Тап — скачок, тап у паветры — двайны скачок',
    en: 'Collect leaves and jump over trash. Tap to jump, tap again mid-air for a double jump',
  },
  finished: { ru: 'Забег окончен! Листьев: {0}', be: 'Забег скончаны! Лісця: {0}', en: 'Run over! Leaves: {0}' },
};

const BASE_HEIGHT = 640;
const GROUND_RATIO = 0.8;
const PLAYER_X_RATIO = 0.22;
const PLAYER_SIZE = 70;
const GRAVITY = 2600;
const JUMP_VELOCITY = 880;
const DOUBLE_JUMP_VELOCITY = 760;
const START_SPEED = 300;
const MAX_SPEED = 660;
const SPEED_GAIN_PER_SECOND = 9;
const OBSTACLE_GAP_SECONDS = [1.0, 1.9];
const LEAF_GAP_SECONDS = [0.7, 1.4];
const OBSTACLE_SIZE = 46;
const LEAF_SIZE = 30;
const LEAF_HIGH_OFFSET = 150;
const LEAF_LOW_OFFSET = 34;
const INVULNERABLE_SECONDS = 1.2;
const GAME_OVER_DELAY_MS = 700;
const OBSTACLES = ['🗑️', '🛢️', '🛞', '📦'];
const BARREL_GLYPH = '🛢️';
const BARREL_LEAF_CHANCE = 0.5;
const TRASH_COLORS = ['#8E7CF0', '#FF9F43', '#4DA3FF', '#9aa5a0'];
const LEAF_COLORS = ['#34C77B', '#7ED957', '#C3EE5A'];

const stage = Engine.createStage(document.getElementById('stage'));
const particles = Engine.createParticles();
const scoreLabel = Engine.scoreLabel(document.getElementById('score'));
const mascot = new Image();
mascot.src = '../common/mascot.png';
document.getElementById('hint').textContent = GP.t(TEXT.hint);

let colors = Engine.palette();
let state = 'ready';
let unit = 1;
let ground = 0;
let score = 0;
let speed = START_SPEED;
let travelled = 0;
let elapsed = 0;
let player = null;
let obstacles = [];
let leaves = [];
let nextObstacle = 0;
let nextLeaf = 0;
let invulnerable = 0;
let lives = null;
let clouds = [];

stage.onResize = () => {
  unit = stage.height / BASE_HEIGHT;
  ground = stage.height * GROUND_RATIO;
  if (player && player.onGround) player.y = ground;
};
stage.onResize();

function reset() {
  colors = Engine.palette();
  score = 0;
  scoreLabel.set(0);
  speed = START_SPEED;
  travelled = 0;
  elapsed = 0;
  obstacles = [];
  leaves = [];
  particles.clear();
  nextObstacle = 1.4;
  nextLeaf = 0.6;
  invulnerable = 0;
  player = { y: ground, vy: 0, onGround: true, jumpsLeft: 2, squash: 0, runPhase: 0 };
  clouds = Array.from({ length: 4 }, (_, index) => ({
    x: (index / 4) * stage.width + Engine.random(0, 80),
    y: Engine.random(0.08, 0.32) * stage.height,
    scale: Engine.random(0.7, 1.3),
  }));
  lives = GP.lives(document.getElementById('lives'), gameOver);
}

function start() {
  reset();
  state = 'playing';
}

function jump() {
  if (state !== 'playing' || player.jumpsLeft === 0) return;
  player.vy = -(player.onGround ? JUMP_VELOCITY : DOUBLE_JUMP_VELOCITY) * unit;
  player.onGround = false;
  player.jumpsLeft -= 1;
  player.squash = -0.25;
  Engine.sound.jump();
  if (player.jumpsLeft === 0) {
    particles.burst(stage.width * PLAYER_X_RATIO, player.y, { count: 10, colors: [colors.mintHigh], speed: 140, gravity: 200, life: 0.4 });
  }
}

function spawnObstacle() {
  const x = stage.width + OBSTACLE_SIZE * unit;
  const glyph = GP.randomItem(OBSTACLES);
  obstacles.push({ x, glyph, wobble: Math.random() * Math.PI });
  if (glyph === BARREL_GLYPH && Math.random() < BARREL_LEAF_CHANCE) {
    spawnLeaves(x + OBSTACLE_SIZE * unit);
  }
}

function spawnLeaves(originX = stage.width + LEAF_SIZE * unit) {
  const isHigh = Math.random() < 0.45;
  const count = 3 + Math.floor(Math.random() * 3);
  const baseY = ground - (isHigh ? LEAF_HIGH_OFFSET : LEAF_LOW_OFFSET) * unit;
  for (let index = 0; index < count; index += 1) {
    const arc = isHigh ? Math.sin((index / (count - 1)) * Math.PI) * 30 * unit : 0;
    leaves.push({ x: originX + index * 40 * unit, y: baseY - arc, phase: Math.random() * Math.PI * 2 });
  }
}

function update(dt) {
  particles.update(dt);
  for (const cloud of clouds) {
    cloud.x -= speed * 0.08 * unit * dt;
    if (cloud.x < -120 * unit) {
      cloud.x = stage.width + Engine.random(20, 120) * unit;
      cloud.y = Engine.random(0.08, 0.32) * stage.height;
    }
  }
  if (state !== 'playing') return;

  elapsed += dt;
  speed = Math.min(MAX_SPEED, START_SPEED + elapsed * SPEED_GAIN_PER_SECOND);
  const step = speed * unit * dt;
  travelled += step;
  invulnerable = Math.max(0, invulnerable - dt);

  player.vy += GRAVITY * unit * dt;
  player.y += player.vy * dt;
  if (player.y >= ground) {
    if (!player.onGround) {
      player.squash = 0.3;
      particles.burst(stage.width * PLAYER_X_RATIO, ground, { count: 6, colors: [colors.secondary], speed: 90, gravity: 300, life: 0.35, size: 4, angle: -Math.PI / 2, spread: Math.PI });
    }
    player.y = ground;
    player.vy = 0;
    player.onGround = true;
    player.jumpsLeft = 2;
  }
  player.squash *= Math.pow(0.0001, dt);
  player.runPhase += dt * speed / 40;

  nextObstacle -= dt;
  if (nextObstacle <= 0) {
    spawnObstacle();
    nextObstacle = Engine.random(...OBSTACLE_GAP_SECONDS) * (START_SPEED / speed + 0.35);
  }
  nextLeaf -= dt;
  if (nextLeaf <= 0) {
    spawnLeaves();
    nextLeaf = Engine.random(...LEAF_GAP_SECONDS) + 0.6;
  }

  const playerX = stage.width * PLAYER_X_RATIO;
  const half = PLAYER_SIZE * unit * 0.3;
  const top = player.y - PLAYER_SIZE * unit * 0.8;

  for (const obstacle of obstacles) obstacle.x -= step;
  obstacles = obstacles.filter((obstacle) => {
    const size = OBSTACLE_SIZE * unit;
    const hit = invulnerable === 0
      && Math.abs(obstacle.x - playerX) < half + size * 0.32
      && player.y > ground - size * 0.7;
    if (hit) {
      invulnerable = INVULNERABLE_SECONDS;
      stage.shake(10 * unit);
      Engine.sound.bad();
      particles.burst(obstacle.x, ground - size / 2, { count: 18, colors: TRASH_COLORS, speed: 320, life: 0.8 });
      lives.lose();
      return false;
    }
    return obstacle.x > -size;
  });

  for (const leaf of leaves) leaf.x -= step;
  leaves = leaves.filter((leaf) => {
    const radius = LEAF_SIZE * unit * 0.6;
    if (leaf.x > playerX - half - radius && leaf.x < playerX + half + radius && leaf.y > top - radius && leaf.y < player.y + radius) {
      score += 1;
      scoreLabel.set(score);
      Engine.sound.pick();
      particles.burst(leaf.x, leaf.y, { count: 8, colors: LEAF_COLORS, speed: 160, gravity: 120, life: 0.5, size: 5 });
      particles.text(leaf.x, leaf.y - 20 * unit, '+1', colors.forest, 20 * unit);
      return false;
    }
    return leaf.x > -LEAF_SIZE * unit;
  });
}

function drawHills(ctx, offset, height, color, wavelength) {
  ctx.fillStyle = color;
  ctx.beginPath();
  ctx.moveTo(0, ground);
  for (let x = 0; x <= stage.width + 10; x += 10) {
    const phase = (x + offset) / wavelength;
    const y = ground - height * (0.6 + 0.4 * Math.sin(phase) * Math.cos(phase * 0.37));
    ctx.lineTo(x, y);
  }
  ctx.lineTo(stage.width, ground);
  ctx.closePath();
  ctx.fill();
}

function drawTrees(ctx, offset) {
  const spacing = 90 * unit;
  const start = -(offset % spacing);
  ctx.fillStyle = colors.forest;
  ctx.globalAlpha = colors.isDark ? 0.55 : 0.35;
  for (let x = start; x < stage.width + spacing; x += spacing) {
    const index = Math.round((x + offset) / spacing);
    const height = (50 + (index * 37) % 40) * unit;
    ctx.beginPath();
    ctx.moveTo(x, ground - height);
    ctx.lineTo(x - height * 0.3, ground);
    ctx.lineTo(x + height * 0.3, ground);
    ctx.closePath();
    ctx.fill();
  }
  ctx.globalAlpha = 1;
}

function drawGround(ctx) {
  ctx.fillStyle = colors.mintHigh;
  ctx.fillRect(0, ground, stage.width, stage.height - ground);
  ctx.fillStyle = colors.forest;
  ctx.fillRect(0, ground, stage.width, 4 * unit);
  const dash = 36 * unit;
  ctx.globalAlpha = 0.25;
  for (let x = -(travelled % (dash * 2)); x < stage.width; x += dash * 2) {
    ctx.fillRect(x, ground + 18 * unit, dash, 4 * unit);
  }
  ctx.globalAlpha = 1;
}

function drawPlayer(ctx) {
  const x = stage.width * PLAYER_X_RATIO;
  const size = PLAYER_SIZE * unit;
  const bob = player.onGround && state === 'playing' ? Math.abs(Math.sin(player.runPhase)) * 4 * unit : 0;
  const stretchX = 1 + player.squash;
  const stretchY = 1 - player.squash;
  if (invulnerable > 0 && Math.floor(invulnerable * 12) % 2 === 0) return;
  ctx.save();
  ctx.fillStyle = 'rgba(0,0,0,0.12)';
  const shadowScale = Engine.clamp(1 - (ground - player.y) / (300 * unit), 0.3, 1);
  ctx.beginPath();
  ctx.ellipse(x, ground + 2 * unit, size * 0.32 * shadowScale, 5 * unit * shadowScale, 0, 0, Math.PI * 2);
  ctx.fill();
  ctx.translate(x, player.y - bob);
  ctx.rotate(player.onGround ? Math.sin(player.runPhase) * 0.05 : Engine.clamp(player.vy / (4000 * unit), -0.2, 0.2));
  ctx.scale(stretchX, stretchY);
  if (mascot.complete && mascot.naturalWidth > 0) {
    ctx.drawImage(mascot, -size / 2, -size, size, size);
  } else {
    Engine.drawGlyph(ctx, '🌱', 0, -size / 2, size * 0.8);
  }
  ctx.restore();
}

function render(dt) {
  const ctx = stage.ctx;
  stage.begin(dt);
  const sky = ctx.createLinearGradient(0, 0, 0, ground);
  sky.addColorStop(0, colors.mint);
  sky.addColorStop(1, colors.card);
  ctx.fillStyle = sky;
  ctx.fillRect(0, 0, stage.width, stage.height);

  ctx.fillStyle = colors.isDark ? 'rgba(255,255,255,0.08)' : 'rgba(255,255,255,0.9)';
  for (const cloud of clouds) {
    const width = 70 * unit * cloud.scale;
    ctx.beginPath();
    ctx.ellipse(cloud.x, cloud.y, width * 0.5, width * 0.2, 0, 0, Math.PI * 2);
    ctx.ellipse(cloud.x - width * 0.2, cloud.y + 4 * unit, width * 0.3, width * 0.16, 0, 0, Math.PI * 2);
    ctx.ellipse(cloud.x + width * 0.15, cloud.y - width * 0.1, width * 0.25, width * 0.18, 0, 0, Math.PI * 2);
    ctx.fill();
  }

  drawHills(ctx, travelled * 0.2, 110 * unit, colors.mintHigh, 160 * unit);
  drawTrees(ctx, travelled * 0.5);
  drawGround(ctx);

  for (const leaf of leaves) {
    const float = Math.sin(performance.now() / 250 + leaf.phase) * 4 * unit;
    Engine.drawGlyph(ctx, '🍃', leaf.x, leaf.y + float, LEAF_SIZE * unit, Math.sin(performance.now() / 300 + leaf.phase) * 0.3);
  }
  for (const obstacle of obstacles) {
    const size = OBSTACLE_SIZE * unit;
    Engine.drawGlyph(ctx, obstacle.glyph, obstacle.x, ground - size * 0.5, size, Math.sin(performance.now() / 200 + obstacle.wobble) * 0.06);
  }
  if (player) drawPlayer(ctx);
  particles.render(ctx);
}

function gameOver() {
  if (state !== 'playing') return;
  state = 'over';
  Engine.sound.over();
  GP.finish(score);
  setTimeout(() => GP.showResult(GP.t(TEXT.finished, score), start), GAME_OVER_DELAY_MS);
}

Engine.input(stage.canvas, { down: jump });
document.addEventListener('keydown', (event) => {
  if (event.code === 'Space' || event.code === 'ArrowUp') jump();
});

reset();
Engine.loop(update, render);
Engine.startScreen(GP.t(TEXT.title), GP.t(TEXT.hint), start);
