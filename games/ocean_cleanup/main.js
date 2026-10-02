const TEXT = {
  title: { ru: 'Чистый океан', be: 'Чысты акіян', en: 'Ocean cleanup' },
  hint: {
    ru: 'Веди сеть пальцем и вылавливай пластик. Рыб и черепах не трогай',
    be: 'Вядзі сетку пальцам і вылоўлівай пластык. Рыб і чарапах не чапай',
    en: 'Drag the net to scoop up plastic. Leave the fish and turtles alone',
  },
  finished: { ru: 'Улов окончен! Пластика: {0}', be: 'Улоў скончаны! Пластыку: {0}', en: 'Done! Plastic collected: {0}' },
};

const BASE_HEIGHT = 640;
const SURFACE_RATIO = 0.18;
const NET_RADIUS = 34;
const NET_FOLLOW = 12;
const BOAT_FOLLOW = 3;
const ITEM_SIZE = 40;
const PLASTIC = ['🧴', '🥤', '🛍️', '🧃', '🥡'];
const CREATURES = ['🐟', '🐠', '🐡', '🐢'];
const PLASTIC_SPEED = [28, 60];
const CREATURE_SPEED = [80, 150];
const CREATURE_SPEED_GAIN = 2;
const PLASTIC_GAP_SECONDS = [0.55, 1.05];
const CREATURE_GAP_START = [1.8, 2.8];
const CREATURE_GAP_MIN = [0.6, 1.1];
const CREATURE_GAP_SHRINK_SECONDS = 90;
const SCARED_SPEED_FACTOR = 3.2;
const INVULNERABLE_SECONDS = 1.2;
const AMBIENT_BUBBLES = 18;
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
let net = { x: 0, y: 0, targetX: 0, targetY: 0 };
let boatX = 0;
let items = [];
let bubbles = [];
let nextPlastic = 0;
let nextCreature = 0;
let invulnerable = 0;
let lives = null;

function surface() {
  return stage.height * SURFACE_RATIO;
}

function clampNet(x, y) {
  const radius = NET_RADIUS * unit;
  return {
    x: Engine.clamp(x, radius, stage.width - radius),
    y: Engine.clamp(y, surface() + radius + 10 * unit, stage.height - radius),
  };
}

stage.onResize = () => {
  unit = stage.height / BASE_HEIGHT;
  const position = clampNet(net.targetX || stage.width / 2, net.targetY || stage.height * 0.55);
  net = { x: position.x, y: position.y, targetX: position.x, targetY: position.y };
  boatX = position.x;
  bubbles = Array.from({ length: AMBIENT_BUBBLES }, () => newBubble(Engine.random(surface(), stage.height)));
};

function newBubble(y) {
  return { x: Engine.random(0, stage.width), y, radius: Engine.random(2, 6) * unit, speed: Engine.random(20, 50) * unit, phase: Math.random() * Math.PI * 2 };
}

stage.onResize();

function reset() {
  colors = Engine.palette();
  score = 0;
  scoreLabel.set(0);
  elapsed = 0;
  items = [];
  particles.clear();
  nextPlastic = 0.3;
  nextCreature = 2;
  invulnerable = 0;
  lives = GP.lives(document.getElementById('lives'), gameOver);
}

function start() {
  reset();
  state = 'playing';
}

function spawn(kind) {
  const fromLeft = Math.random() < 0.5;
  const isPlastic = kind === 'plastic';
  const range = isPlastic ? PLASTIC_SPEED : CREATURE_SPEED;
  const gain = isPlastic ? 0 : elapsed * CREATURE_SPEED_GAIN;
  const speed = (Engine.random(...range) + gain) * unit;
  items.push({
    kind,
    glyph: GP.randomItem(isPlastic ? PLASTIC : CREATURES),
    x: fromLeft ? -ITEM_SIZE * unit : stage.width + ITEM_SIZE * unit,
    baseY: Engine.random(surface() + 40 * unit, stage.height - 30 * unit),
    y: 0,
    vx: fromLeft ? speed : -speed,
    phase: Math.random() * Math.PI * 2,
    spin: isPlastic ? (Math.random() - 0.5) * 1.2 : 0,
    rotation: 0,
    scared: false,
  });
}

function creatureGap() {
  const progress = Math.min(1, elapsed / CREATURE_GAP_SHRINK_SECONDS);
  return Engine.random(
    Engine.lerp(CREATURE_GAP_START[0], CREATURE_GAP_MIN[0], progress),
    Engine.lerp(CREATURE_GAP_START[1], CREATURE_GAP_MIN[1], progress),
  );
}

function update(dt) {
  const time = performance.now() / 1000;
  particles.update(dt);
  for (const bubble of bubbles) {
    bubble.y -= bubble.speed * dt;
    if (bubble.y < surface()) Object.assign(bubble, newBubble(stage.height + 10 * unit));
  }
  net.x = Engine.lerp(net.x, net.targetX, Math.min(1, NET_FOLLOW * dt));
  net.y = Engine.lerp(net.y, net.targetY, Math.min(1, NET_FOLLOW * dt));
  boatX = Engine.lerp(boatX, net.x, Math.min(1, BOAT_FOLLOW * dt));
  for (const item of items) {
    item.x += item.vx * dt;
    item.y = item.baseY + Math.sin(time * 2 + item.phase) * 8 * unit;
    item.rotation += item.spin * dt;
  }
  items = items.filter((item) => item.x > -ITEM_SIZE * 2 * unit && item.x < stage.width + ITEM_SIZE * 2 * unit);
  if (state !== 'playing') return;

  elapsed += dt;
  invulnerable = Math.max(0, invulnerable - dt);
  nextPlastic -= dt;
  if (nextPlastic <= 0) {
    spawn('plastic');
    nextPlastic = Engine.random(...PLASTIC_GAP_SECONDS);
  }
  nextCreature -= dt;
  if (nextCreature <= 0) {
    spawn('creature');
    nextCreature = creatureGap();
  }

  const reach = NET_RADIUS * unit + ITEM_SIZE * unit * 0.3;
  items = items.filter((item) => {
    if (Math.hypot(item.x - net.x, item.y - net.y) > reach) return true;
    if (item.kind === 'plastic') {
      score += 1;
      scoreLabel.set(score);
      Engine.sound.pick();
      particles.burst(item.x, item.y, { count: 10, colors: ['#ffffff', '#bfe6ff'], speed: 120, gravity: -160, life: 0.6, size: 5 });
      particles.text(item.x, item.y - 24 * unit, '+1', '#ffffff', 22 * unit);
      return false;
    }
    if (!item.scared && invulnerable === 0) {
      item.scared = true;
      item.vx = Math.sign(item.x - net.x || 1) * Math.abs(item.vx) * SCARED_SPEED_FACTOR;
      invulnerable = INVULNERABLE_SECONDS;
      stage.shake(8 * unit);
      Engine.sound.bad();
      particles.burst(item.x, item.y, { count: 14, colors: ['#ffffff', '#ff8a8a'], speed: 200, gravity: 0, life: 0.5 });
      lives.lose();
    }
    return true;
  });
}

function drawSea(ctx, time) {
  const top = surface();
  const sky = ctx.createLinearGradient(0, 0, 0, top);
  sky.addColorStop(0, colors.isDark ? '#0d1a24' : '#cfeeff');
  sky.addColorStop(1, colors.isDark ? '#18303f' : '#eaf8ff');
  ctx.fillStyle = sky;
  ctx.fillRect(0, 0, stage.width, top + 20 * unit);

  const water = ctx.createLinearGradient(0, top, 0, stage.height);
  water.addColorStop(0, colors.isDark ? '#155a80' : '#4db6ec');
  water.addColorStop(1, colors.isDark ? '#061923' : '#145f8f');
  ctx.fillStyle = water;
  ctx.beginPath();
  ctx.moveTo(0, stage.height);
  for (let x = 0; x <= stage.width + 8; x += 8) {
    ctx.lineTo(x, top + Math.sin(x / (40 * unit) + time * 2) * 5 * unit);
  }
  ctx.lineTo(stage.width, stage.height);
  ctx.closePath();
  ctx.fill();

  ctx.strokeStyle = 'rgba(255,255,255,0.35)';
  ctx.lineWidth = 2 * unit;
  ctx.beginPath();
  for (let x = 0; x <= stage.width + 8; x += 8) {
    const y = top + 10 * unit + Math.sin(x / (28 * unit) - time * 2.6) * 3 * unit;
    if (x === 0) ctx.moveTo(x, y);
    else ctx.lineTo(x, y);
  }
  ctx.stroke();

  ctx.globalAlpha = 0.12;
  ctx.fillStyle = '#ffffff';
  for (let index = 0; index < 4; index += 1) {
    const x = ((index * stage.width) / 3 + time * 18 * unit) % (stage.width + 80 * unit) - 40 * unit;
    ctx.beginPath();
    ctx.moveTo(x, top);
    ctx.lineTo(x + 40 * unit, top);
    ctx.lineTo(x - 30 * unit, stage.height);
    ctx.lineTo(x - 70 * unit, stage.height);
    ctx.closePath();
    ctx.fill();
  }
  ctx.globalAlpha = 1;

  ctx.strokeStyle = colors.forest;
  ctx.lineWidth = 5 * unit;
  ctx.lineCap = 'round';
  for (let index = 0; index < 7; index += 1) {
    const x = (index + 0.5) * (stage.width / 7);
    const height = (40 + (index * 23) % 50) * unit;
    ctx.beginPath();
    ctx.moveTo(x, stage.height);
    ctx.quadraticCurveTo(x + Math.sin(time * 1.5 + index) * 14 * unit, stage.height - height / 2, x + Math.sin(time * 1.5 + index + 1) * 10 * unit, stage.height - height);
    ctx.stroke();
  }

  ctx.strokeStyle = 'rgba(255,255,255,0.5)';
  ctx.lineWidth = 1.5 * unit;
  for (const bubble of bubbles) {
    ctx.beginPath();
    ctx.arc(bubble.x + Math.sin(time * 3 + bubble.phase) * 3 * unit, bubble.y, bubble.radius, 0, Math.PI * 2);
    ctx.stroke();
  }
}

function drawBoat(ctx, time) {
  const y = surface() + Math.sin(boatX / (40 * unit) + time * 2) * 5 * unit;
  const tilt = Math.cos(boatX / (40 * unit) + time * 2) * 0.08;
  ctx.save();
  ctx.translate(boatX, y);
  ctx.rotate(tilt);
  ctx.fillStyle = colors.forest;
  ctx.beginPath();
  ctx.moveTo(-46 * unit, -10 * unit);
  ctx.lineTo(46 * unit, -10 * unit);
  ctx.lineTo(32 * unit, 10 * unit);
  ctx.lineTo(-32 * unit, 10 * unit);
  ctx.closePath();
  ctx.fill();
  ctx.fillStyle = '#ffffff';
  Engine.roundRect(ctx, -16 * unit, -30 * unit, 30 * unit, 20 * unit, 5 * unit);
  ctx.fill();
  ctx.fillStyle = colors.lime;
  ctx.fillRect(-46 * unit, -12 * unit, 92 * unit, 4 * unit);
  ctx.restore();
  return { x: boatX, y };
}

function drawNet(ctx, boat) {
  const radius = NET_RADIUS * unit;
  ctx.strokeStyle = colors.isDark ? '#cfd8d3' : '#2d3833';
  ctx.lineWidth = 2 * unit;
  ctx.beginPath();
  ctx.moveTo(boat.x, boat.y);
  ctx.quadraticCurveTo((boat.x + net.x) / 2, (boat.y + net.y) / 2 + 30 * unit, net.x, net.y - radius);
  ctx.stroke();
  if (invulnerable > 0 && Math.floor(invulnerable * 12) % 2 === 0) return;
  ctx.save();
  ctx.beginPath();
  ctx.arc(net.x, net.y, radius, 0, Math.PI * 2);
  ctx.fillStyle = 'rgba(255,255,255,0.12)';
  ctx.fill();
  ctx.clip();
  ctx.strokeStyle = 'rgba(255,255,255,0.55)';
  ctx.lineWidth = 1.2 * unit;
  const step = 9 * unit;
  for (let offset = -radius * 2; offset <= radius * 2; offset += step) {
    ctx.beginPath();
    ctx.moveTo(net.x + offset - radius, net.y - radius);
    ctx.lineTo(net.x + offset + radius, net.y + radius);
    ctx.moveTo(net.x + offset + radius, net.y - radius);
    ctx.lineTo(net.x + offset - radius, net.y + radius);
    ctx.stroke();
  }
  ctx.restore();
  ctx.strokeStyle = colors.lime;
  ctx.lineWidth = 4 * unit;
  ctx.beginPath();
  ctx.arc(net.x, net.y, radius, 0, Math.PI * 2);
  ctx.stroke();
}

function drawItem(ctx, item) {
  const size = ITEM_SIZE * unit;
  if (item.kind === 'plastic') {
    Engine.drawGlyph(ctx, item.glyph, item.x, item.y, size, item.rotation);
    return;
  }
  ctx.save();
  ctx.translate(item.x, item.y);
  if (item.vx > 0) ctx.scale(-1, 1);
  Engine.drawGlyph(ctx, item.glyph, 0, 0, size, Math.sin(performance.now() / 150 + item.phase) * 0.08);
  ctx.restore();
}

function render(dt) {
  const ctx = stage.ctx;
  const time = performance.now() / 1000;
  stage.begin(dt);
  drawSea(ctx, time);
  for (const item of items) drawItem(ctx, item);
  const boat = drawBoat(ctx, time);
  drawNet(ctx, boat);
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
  const position = clampNet(x, y);
  net.targetX = position.x;
  net.targetY = position.y;
}

Engine.input(stage.canvas, { down: aim, move: aim });
document.addEventListener('keydown', (event) => {
  const step = 30 * unit;
  const moves = { ArrowLeft: [-step, 0], ArrowRight: [step, 0], ArrowUp: [0, -step], ArrowDown: [0, step] };
  const move = moves[event.code];
  if (move) aim(net.targetX + move[0], net.targetY + move[1]);
});

reset();
Engine.loop(update, render);
Engine.startScreen(GP.t(TEXT.title), GP.t(TEXT.hint), start);
