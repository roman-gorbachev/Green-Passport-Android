const TEXT = {
  title: { ru: 'Выключи свет', be: 'Выключы святло', en: 'Lights out' },
  hint: {
    ru: 'В доме включаются приборы. Нажимай на комнату, чтобы выключить их, пока счётчик не сгорел',
    be: 'У доме ўключаюцца прыборы. Націскай на пакой, каб выключыць іх, пакуль лічыльнік не згарэў',
    en: 'Appliances keep switching on. Tap a room to turn them off before the timer runs out',
  },
  finished: { ru: 'Свет погашен! Выключено приборов: {0}', be: 'Святло пагашана! Выключана прыбораў: {0}', en: 'Lights out! Appliances switched off: {0}' },
};

const COLUMNS = 2;
const ROWS = 3;
const DEVICES = ['💡', '📺', '💻', '🚿', '🔌', '🎮'];
const ON_LIMIT_START = 4;
const ON_LIMIT_MIN = 2.4;
const SPAWN_GAP_START = [1.1, 1.7];
const SPAWN_GAP_MIN = [0.35, 0.7];
const DIFFICULTY_SECONDS = 90;
const SWITCH_ANIMATION_SECONDS = 0.25;
const GAME_OVER_DELAY_MS = 700;

const stage = Engine.createStage(document.getElementById('stage'));
const particles = Engine.createParticles();
const scoreLabel = Engine.scoreLabel(document.getElementById('score'));
document.getElementById('hint').textContent = GP.t(TEXT.hint);

let colors = Engine.palette();
let state = 'ready';
let score = 0;
let elapsed = 0;
let nextSpawn = 0;
let rooms = [];
let house = { x: 0, y: 0, width: 0, height: 0, roof: 0, room: { width: 0, height: 0 }, gap: 0 };
let lives = null;
let stars = [];

stage.onResize = () => {
  const width = Math.min(stage.width * 0.92, stage.height * 0.72);
  const roof = width * 0.32;
  const height = Math.min(stage.height * 0.88 - roof, width * 1.25);
  const gap = width * 0.035;
  house = {
    width,
    height,
    roof,
    gap,
    x: (stage.width - width) / 2,
    y: (stage.height - height - roof) / 2 + roof,
    room: { width: (width - gap * (COLUMNS + 1)) / COLUMNS, height: (height - gap * (ROWS + 1)) / ROWS },
  };
  stars = Array.from({ length: 24 }, () => ({ x: Math.random() * stage.width, y: Math.random() * house.y * 0.9, phase: Math.random() * Math.PI * 2 }));
};
stage.onResize();

function newRoom(index) {
  return { index, device: DEVICES[index % DEVICES.length], isOn: false, left: 0, total: 0, glow: 0 };
}

function reset() {
  colors = Engine.palette();
  score = 0;
  scoreLabel.set(0);
  elapsed = 0;
  nextSpawn = 1;
  particles.clear();
  rooms = Array.from({ length: COLUMNS * ROWS }, (_, index) => newRoom(index));
  lives = GP.lives(document.getElementById('lives'), gameOver);
}

function start() {
  reset();
  state = 'playing';
}

function difficulty() {
  return Math.min(1, elapsed / DIFFICULTY_SECONDS);
}

function roomRect(room) {
  const column = room.index % COLUMNS;
  const row = Math.floor(room.index / COLUMNS);
  return {
    x: house.x + house.gap + column * (house.room.width + house.gap),
    y: house.y + house.gap + row * (house.room.height + house.gap),
    width: house.room.width,
    height: house.room.height,
  };
}

function switchOn() {
  const dark = rooms.filter((room) => !room.isOn);
  if (dark.length === 0) return;
  const room = GP.randomItem(dark);
  room.device = GP.randomItem(DEVICES);
  room.isOn = true;
  room.total = Engine.lerp(ON_LIMIT_START, ON_LIMIT_MIN, difficulty());
  room.left = room.total;
  Engine.sound.pop(1);
}

function switchOff(room, isPlayer) {
  room.isOn = false;
  const rect = roomRect(room);
  const centerX = rect.x + rect.width / 2;
  const centerY = rect.y + rect.height / 2;
  if (isPlayer) {
    score += 1;
    scoreLabel.set(score);
    Engine.sound.good();
    particles.burst(centerX, centerY, { count: 14, colors: ['#FFD54F', colors.lime, '#ffffff'], speed: 220, life: 0.6 });
    particles.text(centerX, rect.y + rect.height * 0.2, '+1', colors.forest, rect.height * 0.22);
  } else {
    Engine.sound.bad();
    stage.shake(8);
    particles.burst(centerX, centerY, { count: 20, colors: ['#FF7043', '#FFB300', '#555555'], speed: 260, gravity: -60, life: 0.8 });
    particles.text(centerX, rect.y + rect.height * 0.2, '⚡', colors.error, rect.height * 0.3);
    lives.lose();
  }
}

function update(dt) {
  particles.update(dt);
  for (const room of rooms) {
    const target = room.isOn ? 1 : 0;
    room.glow += Math.sign(target - room.glow) * Math.min(Math.abs(target - room.glow), dt / SWITCH_ANIMATION_SECONDS);
  }
  if (state !== 'playing') return;
  elapsed += dt;
  nextSpawn -= dt;
  if (nextSpawn <= 0) {
    switchOn();
    nextSpawn = Engine.random(
      Engine.lerp(SPAWN_GAP_START[0], SPAWN_GAP_MIN[0], difficulty()),
      Engine.lerp(SPAWN_GAP_START[1], SPAWN_GAP_MIN[1], difficulty()),
    );
  }
  for (const room of rooms) {
    if (!room.isOn) continue;
    room.left -= dt;
    if (room.left <= 0) switchOff(room, false);
  }
}

function tap(x, y) {
  if (state !== 'playing') return;
  const room = rooms.find((candidate) => {
    const rect = roomRect(candidate);
    return x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height;
  });
  if (room?.isOn) switchOff(room, true);
}

function drawSky(ctx, time) {
  const sky = ctx.createLinearGradient(0, 0, 0, stage.height);
  sky.addColorStop(0, colors.isDark ? '#0b1630' : '#1d2b53');
  sky.addColorStop(1, colors.isDark ? '#15233f' : '#3a4f86');
  ctx.fillStyle = sky;
  ctx.fillRect(0, 0, stage.width, stage.height);
  ctx.fillStyle = '#ffffff';
  for (const star of stars) {
    ctx.globalAlpha = 0.4 + 0.4 * Math.sin(time * 2 + star.phase);
    ctx.fillRect(star.x, star.y, 2, 2);
  }
  ctx.globalAlpha = 1;
  ctx.fillStyle = '#FFF4C2';
  ctx.beginPath();
  ctx.arc(stage.width * 0.85, house.y * 0.35, house.width * 0.07, 0, Math.PI * 2);
  ctx.fill();
  ctx.fillStyle = colors.isDark ? '#15233f' : '#2a3c6c';
  ctx.beginPath();
  ctx.arc(stage.width * 0.85 + house.width * 0.03, house.y * 0.35 - house.width * 0.02, house.width * 0.06, 0, Math.PI * 2);
  ctx.fill();
}

function drawHouse(ctx) {
  ctx.fillStyle = '#B5523B';
  ctx.beginPath();
  ctx.moveTo(house.x - house.gap * 2, house.y);
  ctx.lineTo(house.x + house.width / 2, house.y - house.roof);
  ctx.lineTo(house.x + house.width + house.gap * 2, house.y);
  ctx.closePath();
  ctx.fill();
  ctx.fillStyle = colors.isDark ? '#3a3f4a' : '#E9E2D4';
  Engine.roundRect(ctx, house.x, house.y, house.width, house.height, house.gap);
  ctx.fill();
  ctx.fillStyle = '#5b6660';
  ctx.fillRect(house.x - house.gap, house.y + house.height - house.gap * 0.5, house.width + house.gap * 2, house.gap);
}

function drawRoom(ctx, room, time) {
  const rect = roomRect(room);
  const dark = colors.isDark ? '#1b2230' : '#2f3a4f';
  const lit = '#FFE8A3';
  ctx.save();
  Engine.roundRect(ctx, rect.x, rect.y, rect.width, rect.height, house.gap * 0.8);
  ctx.clip();
  ctx.fillStyle = dark;
  ctx.fillRect(rect.x, rect.y, rect.width, rect.height);
  if (room.glow > 0) {
    const light = ctx.createRadialGradient(rect.x + rect.width / 2, rect.y + rect.height * 0.15, 0, rect.x + rect.width / 2, rect.y + rect.height * 0.5, rect.width * 0.8);
    light.addColorStop(0, lit);
    light.addColorStop(1, '#F5B94A');
    ctx.globalAlpha = room.glow;
    ctx.fillStyle = light;
    ctx.fillRect(rect.x, rect.y, rect.width, rect.height);
    ctx.globalAlpha = 1;
  }
  ctx.fillStyle = 'rgba(0,0,0,0.18)';
  ctx.fillRect(rect.x, rect.y + rect.height * 0.82, rect.width, rect.height * 0.18);
  ctx.restore();

  const centerX = rect.x + rect.width / 2;
  const centerY = rect.y + rect.height * 0.5;
  const size = Math.min(rect.width, rect.height) * 0.42;
  ctx.globalAlpha = 0.35 + room.glow * 0.65;
  const wobble = room.isOn ? Math.sin(time * 18) * 0.05 * (1 - room.left / room.total) : 0;
  Engine.drawGlyph(ctx, room.device, centerX, centerY, size, wobble, 1 + room.glow * 0.08);
  ctx.globalAlpha = 1;

  if (room.isOn) {
    const progress = room.left / room.total;
    ctx.strokeStyle = progress < 0.35 ? colors.error : colors.lime;
    ctx.lineWidth = Math.max(3, size * 0.08);
    ctx.lineCap = 'round';
    ctx.beginPath();
    ctx.arc(centerX, centerY, size * 0.75, -Math.PI / 2, -Math.PI / 2 + Math.PI * 2 * progress);
    ctx.stroke();
  }
}

function render(dt) {
  const ctx = stage.ctx;
  const time = performance.now() / 1000;
  stage.begin(dt);
  drawSky(ctx, time);
  drawHouse(ctx);
  for (const room of rooms) drawRoom(ctx, room, time);
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
