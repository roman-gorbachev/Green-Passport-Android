const TEXT = {
  title: { ru: 'Опылитель', be: 'Апыляльнік', en: 'Pollinator' },
  hint: {
    ru: 'Веди пчелу пальцем к вянущим цветам, чтобы они расцвели. Облетай осу и облака пестицидов',
    be: 'Вядзі пчалу пальцам да кветак, што вянуць, каб яны расквітнелі. Абляцай восаў і хмары пестыцыдаў',
    en: 'Guide the bee to wilting flowers so they bloom again. Avoid wasps and pesticide clouds',
  },
  finished: { ru: 'Сезон окончен! Опылено цветов: {0}', be: 'Сезон скончаны! Апылена кветак: {0}', en: 'Season over! Flowers pollinated: {0}' },
};

const BASE_HEIGHT = 640;
const FLOWER_COUNT = 6;
const FLOWERS = ['🌼', '🌸', '🌻', '🌷', '🌺'];
const FLOWER_SIZE = 46;
const BEE_SIZE = 40;
const BEE_FOLLOW = 9;
const WILT_START = 5;
const WILT_MIN = 3;
const WILT_GAP_START = [1.6, 2.4];
const WILT_GAP_MIN = [0.6, 1.1];
const WITHERED_SECONDS = 2;
const HAZARD_GAP_START = [4, 6];
const HAZARD_GAP_MIN = [1.6, 2.6];
const WASP_SPEED = [90, 150];
const CLOUD_SPEED = [35, 60];
const CLOUD_RADIUS = 46;
const WASP_RADIUS = 16;
const INVULNERABLE_SECONDS = 1.2;
const DIFFICULTY_SECONDS = 90;
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
let bee = { x: 0, y: 0, targetX: 0, targetY: 0, facing: 1 };
let flowers = [];
let hazards = [];
let nextWilt = 0;
let nextHazard = 0;
let invulnerable = 0;
let lives = null;

function fieldTop() {
  return stage.height * 0.22;
}

function placeFlowers() {
  const spots = [];
  const columns = 3;
  const rows = 2;
  const cellWidth = stage.width / columns;
  const cellHeight = (stage.height - fieldTop()) / rows;
  for (let row = 0; row < rows; row += 1) {
    for (let column = 0; column < columns; column += 1) {
      spots.push({
        x: cellWidth * (column + 0.5) + Engine.random(-0.2, 0.2) * cellWidth,
        y: fieldTop() + cellHeight * (row + 0.55) + Engine.random(-0.15, 0.15) * cellHeight,
      });
    }
  }
  return spots.slice(0, FLOWER_COUNT).map((spot) => ({
    ...spot,
    glyph: GP.randomItem(FLOWERS),
    state: 'bloom',
    left: 0,
    total: 0,
    pop: 0,
    phase: Math.random() * Math.PI * 2,
  }));
}

stage.onResize = () => {
  unit = stage.height / BASE_HEIGHT;
  flowers = placeFlowers();
  bee = { x: stage.width / 2, y: stage.height * 0.5, targetX: stage.width / 2, targetY: stage.height * 0.5, facing: 1 };
};
stage.onResize();

function reset() {
  colors = Engine.palette();
  score = 0;
  scoreLabel.set(0);
  elapsed = 0;
  hazards = [];
  particles.clear();
  flowers = placeFlowers();
  nextWilt = 0.8;
  nextHazard = 4;
  invulnerable = 0;
  lives = GP.lives(document.getElementById('lives'), gameOver);
}

function start() {
  reset();
  state = 'playing';
}

function difficulty() {
  return Math.min(1, elapsed / DIFFICULTY_SECONDS);
}

function gap(start, min) {
  return Engine.random(Engine.lerp(start[0], min[0], difficulty()), Engine.lerp(start[1], min[1], difficulty()));
}

function startWilting() {
  const healthy = flowers.filter((flower) => flower.state === 'bloom');
  if (healthy.length === 0) return;
  const flower = GP.randomItem(healthy);
  flower.state = 'wilting';
  flower.total = Engine.lerp(WILT_START, WILT_MIN, difficulty());
  flower.left = flower.total;
}

function spawnHazard() {
  const fromLeft = Math.random() < 0.5;
  const isCloud = Math.random() < 0.45;
  const range = isCloud ? CLOUD_SPEED : WASP_SPEED;
  const speed = Engine.random(...range) * unit * (1 + difficulty() * 0.6);
  hazards.push({
    kind: isCloud ? 'cloud' : 'wasp',
    x: fromLeft ? -CLOUD_RADIUS * unit : stage.width + CLOUD_RADIUS * unit,
    baseY: Engine.random(fieldTop() * 0.6, stage.height - 40 * unit),
    y: 0,
    vx: fromLeft ? speed : -speed,
    phase: Math.random() * Math.PI * 2,
  });
}

function pollinate(flower) {
  flower.state = 'bloom';
  flower.pop = 0.35;
  score += 1;
  scoreLabel.set(score);
  Engine.sound.good();
  particles.burst(flower.x, flower.y, { count: 18, colors: ['#FF8FB1', '#FFD54F', '#ffffff', colors.lime], speed: 240, life: 0.8 });
  particles.text(flower.x, flower.y - FLOWER_SIZE * unit, '+1', colors.forest, 22 * unit);
}

function hurt() {
  invulnerable = INVULNERABLE_SECONDS;
  stage.shake(8 * unit);
  Engine.sound.bad();
  particles.burst(bee.x, bee.y, { count: 12, colors: ['#FFB703', '#333333'], speed: 200, life: 0.5 });
  lives.lose();
}

function update(dt) {
  const time = performance.now() / 1000;
  particles.update(dt);
  const previousX = bee.x;
  bee.x = Engine.lerp(bee.x, bee.targetX, Math.min(1, BEE_FOLLOW * dt));
  bee.y = Engine.lerp(bee.y, bee.targetY, Math.min(1, BEE_FOLLOW * dt));
  if (Math.abs(bee.x - previousX) > 0.3) bee.facing = bee.x > previousX ? 1 : -1;
  for (const flower of flowers) flower.pop = Math.max(0, flower.pop - dt);
  for (const hazard of hazards) {
    hazard.x += hazard.vx * dt;
    hazard.y = hazard.baseY + Math.sin(time * (hazard.kind === 'wasp' ? 6 : 1.5) + hazard.phase) * (hazard.kind === 'wasp' ? 18 : 10) * unit;
  }
  hazards = hazards.filter((hazard) => hazard.x > -CLOUD_RADIUS * 2 * unit && hazard.x < stage.width + CLOUD_RADIUS * 2 * unit);
  if (state !== 'playing') return;

  elapsed += dt;
  invulnerable = Math.max(0, invulnerable - dt);
  nextWilt -= dt;
  if (nextWilt <= 0) {
    startWilting();
    nextWilt = gap(WILT_GAP_START, WILT_GAP_MIN);
  }
  nextHazard -= dt;
  if (nextHazard <= 0) {
    spawnHazard();
    nextHazard = gap(HAZARD_GAP_START, HAZARD_GAP_MIN);
  }

  const reach = (FLOWER_SIZE * 0.5 + BEE_SIZE * 0.3) * unit;
  for (const flower of flowers) {
    if (flower.state === 'wilting') {
      if (Math.hypot(flower.x - bee.x, flower.y - bee.y) < reach) {
        pollinate(flower);
        continue;
      }
      flower.left -= dt;
      if (flower.left <= 0) {
        flower.state = 'withered';
        flower.left = WITHERED_SECONDS;
        Engine.sound.bad();
        particles.burst(flower.x, flower.y, { count: 10, colors: ['#8d6e63', '#a1887f'], speed: 120, life: 0.6 });
        lives.lose();
      }
    } else if (flower.state === 'withered') {
      flower.left -= dt;
      if (flower.left <= 0) {
        flower.state = 'bloom';
        flower.glyph = GP.randomItem(FLOWERS);
        flower.pop = 0.35;
      }
    }
  }

  if (invulnerable === 0) {
    for (const hazard of hazards) {
      const radius = (hazard.kind === 'cloud' ? CLOUD_RADIUS * 0.75 : WASP_RADIUS) * unit + BEE_SIZE * 0.25 * unit;
      if (Math.hypot(hazard.x - bee.x, hazard.y - bee.y) < radius) {
        hurt();
        break;
      }
    }
  }
}

function drawMeadow(ctx, time) {
  const top = fieldTop();
  const sky = ctx.createLinearGradient(0, 0, 0, top);
  sky.addColorStop(0, colors.isDark ? '#14263a' : '#bfe6ff');
  sky.addColorStop(1, colors.isDark ? '#1f3a3a' : '#e6f7ff');
  ctx.fillStyle = sky;
  ctx.fillRect(0, 0, stage.width, top + 20 * unit);
  ctx.fillStyle = colors.isDark ? '#FFE9A8' : '#FFD54F';
  ctx.beginPath();
  ctx.arc(stage.width * 0.82, top * 0.45, 22 * unit, 0, Math.PI * 2);
  ctx.fill();
  const field = ctx.createLinearGradient(0, top, 0, stage.height);
  field.addColorStop(0, colors.isDark ? '#2c5a3c' : '#9fdc8a');
  field.addColorStop(1, colors.isDark ? '#1a3a26' : '#5fb85a');
  ctx.fillStyle = field;
  ctx.beginPath();
  ctx.moveTo(0, stage.height);
  for (let x = 0; x <= stage.width + 10; x += 10) {
    ctx.lineTo(x, top + Math.sin(x / (60 * unit)) * 8 * unit);
  }
  ctx.lineTo(stage.width, stage.height);
  ctx.closePath();
  ctx.fill();
  ctx.strokeStyle = colors.isDark ? 'rgba(160,220,160,0.25)' : 'rgba(40,110,50,0.35)';
  ctx.lineWidth = 2 * unit;
  for (let index = 0; index < 40; index += 1) {
    const x = ((index * 61) % 100) / 100 * stage.width;
    const y = top + 30 * unit + ((index * 37) % 100) / 100 * (stage.height - top - 30 * unit);
    const sway = Math.sin(time * 2 + index) * 3 * unit;
    ctx.beginPath();
    ctx.moveTo(x, y);
    ctx.quadraticCurveTo(x + sway, y - 8 * unit, x + sway * 1.5, y - 14 * unit);
    ctx.stroke();
  }
}

function drawFlower(ctx, flower, time) {
  const size = FLOWER_SIZE * unit;
  ctx.strokeStyle = colors.forest;
  ctx.lineWidth = 3 * unit;
  ctx.beginPath();
  ctx.moveTo(flower.x, flower.y + size * 0.9);
  ctx.lineTo(flower.x, flower.y + size * 0.2);
  ctx.stroke();
  if (flower.state === 'withered') {
    Engine.drawGlyph(ctx, '🥀', flower.x, flower.y, size, 0.4);
    return;
  }
  const isWilting = flower.state === 'wilting';
  const droop = isWilting ? (1 - flower.left / flower.total) * 0.7 : Math.sin(time * 1.5 + flower.phase) * 0.06;
  const scale = flower.pop > 0 ? 1 + Math.sin((1 - flower.pop / 0.35) * Math.PI) * 0.35 : 1;
  ctx.globalAlpha = isWilting ? 0.55 + 0.45 * (flower.left / flower.total) : 1;
  Engine.drawGlyph(ctx, flower.glyph, flower.x, flower.y, size, droop, scale);
  ctx.globalAlpha = 1;
  if (isWilting) {
    const progress = flower.left / flower.total;
    ctx.strokeStyle = progress < 0.35 ? colors.error : colors.lime;
    ctx.lineWidth = 4 * unit;
    ctx.lineCap = 'round';
    ctx.beginPath();
    ctx.arc(flower.x, flower.y, size * 0.72, -Math.PI / 2, -Math.PI / 2 + Math.PI * 2 * progress);
    ctx.stroke();
    Engine.drawGlyph(ctx, '💧', flower.x + size * 0.55, flower.y - size * 0.55, size * 0.35, 0, 0.9 + Math.sin(time * 6) * 0.1);
  }
}

function drawCloud(ctx, hazard, time) {
  const radius = CLOUD_RADIUS * unit;
  ctx.fillStyle = 'rgba(150, 120, 170, 0.55)';
  for (let index = 0; index < 5; index += 1) {
    const angle = (index / 5) * Math.PI * 2 + time * 0.6;
    ctx.beginPath();
    ctx.arc(hazard.x + Math.cos(angle) * radius * 0.35, hazard.y + Math.sin(angle) * radius * 0.25, radius * 0.5, 0, Math.PI * 2);
    ctx.fill();
  }
  Engine.drawGlyph(ctx, '☠️', hazard.x, hazard.y, radius * 0.55);
}

function drawWasp(ctx, hazard, time) {
  const radius = WASP_RADIUS * unit;
  ctx.save();
  ctx.translate(hazard.x, hazard.y);
  if (hazard.vx > 0) ctx.scale(-1, 1);
  const flap = Math.sin(time * 40) * 0.5;
  ctx.fillStyle = 'rgba(255,255,255,0.75)';
  ctx.beginPath();
  ctx.ellipse(radius * 0.1, -radius * 0.9, radius * 0.5, radius * 0.9, flap, 0, Math.PI * 2);
  ctx.fill();
  ctx.fillStyle = '#FFC107';
  ctx.beginPath();
  ctx.ellipse(0, 0, radius * 1.3, radius * 0.75, 0, 0, Math.PI * 2);
  ctx.fill();
  ctx.fillStyle = '#212121';
  for (const offset of [-0.3, 0.2, 0.7]) {
    ctx.fillRect(radius * offset - radius * 0.12, -radius * 0.7, radius * 0.24, radius * 1.4);
  }
  ctx.beginPath();
  ctx.arc(-radius * 1.3, 0, radius * 0.45, 0, Math.PI * 2);
  ctx.fill();
  ctx.beginPath();
  ctx.moveTo(radius * 1.25, 0);
  ctx.lineTo(radius * 1.75, radius * 0.1);
  ctx.lineTo(radius * 1.25, radius * 0.25);
  ctx.fill();
  ctx.restore();
}

function drawBee(ctx, time) {
  if (invulnerable > 0 && Math.floor(invulnerable * 12) % 2 === 0) return;
  const bob = Math.sin(time * 10) * 3 * unit;
  ctx.save();
  ctx.translate(bee.x, bee.y + bob);
  if (bee.facing > 0) ctx.scale(-1, 1);
  Engine.drawGlyph(ctx, '🐝', 0, 0, BEE_SIZE * unit, Math.sin(time * 4) * 0.1);
  ctx.restore();
}

function render(dt) {
  const ctx = stage.ctx;
  const time = performance.now() / 1000;
  stage.begin(dt);
  drawMeadow(ctx, time);
  for (const flower of flowers) drawFlower(ctx, flower, time);
  for (const hazard of hazards) {
    if (hazard.kind === 'cloud') drawCloud(ctx, hazard, time);
    else drawWasp(ctx, hazard, time);
  }
  drawBee(ctx, time);
  particles.render(ctx);
}

function gameOver() {
  if (state !== 'playing') return;
  state = 'over';
  Engine.sound.over();
  GP.finish(score);
  setTimeout(() => GP.showResult(GP.t(TEXT.finished, score), start), GAME_OVER_DELAY_MS);
}

function aim(x, y) {
  bee.targetX = Engine.clamp(x, 0, stage.width);
  bee.targetY = Engine.clamp(y, 0, stage.height);
}

Engine.input(stage.canvas, { down: aim, move: aim });

reset();
Engine.loop(update, render);
Engine.startScreen(GP.t(TEXT.title), GP.t(TEXT.hint), start);
