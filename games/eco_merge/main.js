const TEXT = {
  title: { ru: 'Эко-2048', be: 'Эка-2048', en: 'Eco merge' },
  hint: {
    ru: 'Свайпай, чтобы сдвигать плитки. Две одинаковые сливаются: семя → росток → куст → дерево → лес',
    be: 'Свайпай, каб ссоўваць пліткі. Дзве аднолькавыя зліваюцца: насенне → парастак → куст → дрэва → лес',
    en: 'Swipe to slide tiles. Two equal tiles merge: seed → sprout → bush → tree → forest',
  },
  finished: { ru: 'Ходов больше нет! Очки: {0}', be: 'Хадоў больш няма! Ачкі: {0}', en: 'No moves left! Score: {0}' },
};

const SIZE = 4;
const LEVELS = ['🌰', '🌱', '🌿', '☘️', '🪴', '🌳', '🌲', '🏞️', '🌍'];
const SCORING_LEVEL = 2;
const SECOND_LEVEL_CHANCE = 0.1;
const START_TILES = 2;
const SLIDE_SPEED = 22;
const POP_SECONDS = 0.25;
const BORN_SECONDS = 0.2;
const DYING_SECONDS = 0.12;
const LEGEND_HEIGHT_RATIO = 0.12;
const MAX_TILE_TINT = 0.85;
const GAME_OVER_DELAY_MS = 900;
const DIRECTIONS = {
  left: { row: 0, column: -1 },
  right: { row: 0, column: 1 },
  up: { row: -1, column: 0 },
  down: { row: 1, column: 0 },
};

const stage = Engine.createStage(document.getElementById('stage'));
const particles = Engine.createParticles();
const scoreLabel = Engine.scoreLabel(document.getElementById('score'));
const bestLabel = document.getElementById('lives');
document.getElementById('hint').textContent = GP.t(TEXT.hint);

let colors = Engine.palette();
let state = 'ready';
let score = 0;
let best = 0;
let tiles = [];
let dying = [];
let nextId = 0;
let board = { x: 0, y: 0, size: 0, cell: 0, gap: 0 };

stage.onResize = () => {
  const legend = stage.height * LEGEND_HEIGHT_RATIO;
  const size = Math.min(stage.width, stage.height - legend) * 0.94;
  const gap = size * 0.03;
  board = {
    size,
    gap,
    cell: (size - gap * (SIZE + 1)) / SIZE,
    x: (stage.width - size) / 2,
    y: (stage.height - legend - size) / 2,
  };
  for (const tile of tiles) snap(tile);
};

function cellPosition(row, column) {
  return {
    x: board.x + board.gap + column * (board.cell + board.gap),
    y: board.y + board.gap + row * (board.cell + board.gap),
  };
}

function snap(tile) {
  const position = cellPosition(tile.row, tile.column);
  tile.x = position.x;
  tile.y = position.y;
}

stage.onResize();

function tileAt(row, column) {
  return tiles.find((tile) => tile.row === row && tile.column === column);
}

function addRandomTile() {
  const empty = [];
  for (let row = 0; row < SIZE; row += 1) {
    for (let column = 0; column < SIZE; column += 1) {
      if (!tileAt(row, column)) empty.push({ row, column });
    }
  }
  if (empty.length === 0) return;
  const spot = GP.randomItem(empty);
  const tile = { id: nextId++, row: spot.row, column: spot.column, level: Math.random() < SECOND_LEVEL_CHANCE ? 1 : 0, born: BORN_SECONDS, pop: 0 };
  snap(tile);
  tiles.push(tile);
}

function reset() {
  colors = Engine.palette();
  score = 0;
  best = 0;
  scoreLabel.set(0);
  tiles = [];
  dying = [];
  particles.clear();
  for (let index = 0; index < START_TILES; index += 1) addRandomTile();
  updateBest();
}

function start() {
  reset();
  state = 'playing';
}

function updateBest() {
  best = Math.max(best, ...tiles.map((tile) => tile.level));
  bestLabel.textContent = LEVELS[best];
}

function move(name) {
  if (state !== 'playing') return;
  const direction = DIRECTIONS[name];
  const order = [...Array(SIZE).keys()];
  const rows = direction.row === 1 ? [...order].reverse() : order;
  const columns = direction.column === 1 ? [...order].reverse() : order;
  const merged = new Set();
  let moved = false;

  for (const row of rows) {
    for (const column of columns) {
      const tile = tileAt(row, column);
      if (!tile) continue;
      let targetRow = row;
      let targetColumn = column;
      while (true) {
        const nextRow = targetRow + direction.row;
        const nextColumn = targetColumn + direction.column;
        if (nextRow < 0 || nextRow >= SIZE || nextColumn < 0 || nextColumn >= SIZE) break;
        const blocker = tileAt(nextRow, nextColumn);
        if (!blocker) {
          targetRow = nextRow;
          targetColumn = nextColumn;
          continue;
        }
        if (blocker.level === tile.level && !merged.has(blocker.id) && tile.level < LEVELS.length - 1) {
          tiles = tiles.filter((other) => other !== tile);
          tile.row = nextRow;
          tile.column = nextColumn;
          tile.dying = DYING_SECONDS;
          dying.push(tile);
          blocker.level += 1;
          blocker.pop = POP_SECONDS;
          merged.add(blocker.id);
          onMerge(blocker);
          moved = true;
        }
        break;
      }
      if (tile.dying) continue;
      if (targetRow !== row || targetColumn !== column) {
        tile.row = targetRow;
        tile.column = targetColumn;
        moved = true;
      }
    }
  }

  if (!moved) return;
  Engine.sound.jump();
  addRandomTile();
  updateBest();
  if (!hasMoves()) {
    state = 'over';
    Engine.sound.over();
    GP.finish(score);
    setTimeout(() => GP.showResult(GP.t(TEXT.finished, score), start), GAME_OVER_DELAY_MS);
  }
}

function onMerge(tile) {
  const position = cellPosition(tile.row, tile.column);
  const centerX = position.x + board.cell / 2;
  const centerY = position.y + board.cell / 2;
  Engine.sound.pop(tile.level);
  particles.burst(centerX, centerY, { count: 8 + tile.level * 3, colors: ['#34C77B', colors.lime, '#ffffff'], speed: 160 + tile.level * 30, life: 0.6, size: 5 });
  if (tile.level >= SCORING_LEVEL) {
    score += 1;
    scoreLabel.set(score);
    particles.text(centerX, centerY - board.cell * 0.45, '+1', colors.forest, board.cell * 0.26);
  }
  if (tile.level > best) {
    stage.shake(4 + tile.level);
  }
}

function hasMoves() {
  if (tiles.length < SIZE * SIZE) return true;
  for (const tile of tiles) {
    const right = tileAt(tile.row, tile.column + 1);
    const below = tileAt(tile.row + 1, tile.column);
    if ((right && right.level === tile.level) || (below && below.level === tile.level)) return true;
  }
  return false;
}

function update(dt) {
  particles.update(dt);
  const follow = Math.min(1, SLIDE_SPEED * dt);
  for (const tile of [...tiles, ...dying]) {
    const target = cellPosition(tile.row, tile.column);
    tile.x = Engine.lerp(tile.x, target.x, follow);
    tile.y = Engine.lerp(tile.y, target.y, follow);
    tile.pop = Math.max(0, tile.pop - dt);
    tile.born = Math.max(0, tile.born - dt);
  }
  for (const tile of dying) tile.dying -= dt;
  dying = dying.filter((tile) => tile.dying > 0);
}

function rgb(hex) {
  const value = parseInt(hex.replace('#', ''), 16);
  return [(value >> 16) & 255, (value >> 8) & 255, value & 255];
}

function tileColor(level) {
  const amount = (level / (LEVELS.length - 1)) * MAX_TILE_TINT;
  const from = rgb(colors.mintHigh);
  const to = rgb(colors.forest);
  const mixed = from.map((channel, index) => Math.round(Engine.lerp(channel, to[index], amount)));
  return `rgb(${mixed.join(',')})`;
}

function drawTile(ctx, tile) {
  const cell = board.cell;
  let scale = 1;
  if (tile.born > 0) scale = Engine.ease.outBack(1 - tile.born / BORN_SECONDS);
  if (tile.pop > 0) scale = 1 + Math.sin((1 - tile.pop / POP_SECONDS) * Math.PI) * 0.15;
  ctx.save();
  ctx.translate(tile.x + cell / 2, tile.y + cell / 2);
  ctx.scale(scale, scale);
  ctx.fillStyle = tileColor(tile.level);
  Engine.roundRect(ctx, -cell / 2, -cell / 2, cell, cell, cell * 0.2);
  ctx.fill();
  ctx.restore();
  Engine.drawGlyph(ctx, LEVELS[tile.level], tile.x + cell / 2, tile.y + cell / 2, cell * 0.52, 0, scale);
}

function drawLegend(ctx) {
  const top = board.y + board.size + stage.height * LEGEND_HEIGHT_RATIO * 0.25;
  const height = stage.height * LEGEND_HEIGHT_RATIO * 0.6;
  const step = Math.min(stage.width / LEVELS.length, height * 1.3);
  const startX = stage.width / 2 - (step * (LEVELS.length - 1)) / 2;
  LEVELS.forEach((level, index) => {
    ctx.globalAlpha = index <= best ? 1 : 0.25;
    Engine.drawGlyph(ctx, level, startX + index * step, top + height / 2, Math.min(height, step * 0.8));
  });
  ctx.globalAlpha = 1;
}

function render(dt) {
  const ctx = stage.ctx;
  stage.begin(dt);
  ctx.fillStyle = colors.card;
  ctx.fillRect(0, 0, stage.width, stage.height);
  ctx.fillStyle = colors.mint;
  Engine.roundRect(ctx, board.x, board.y, board.size, board.size, board.cell * 0.24);
  ctx.fill();
  ctx.fillStyle = colors.isDark ? 'rgba(255,255,255,0.05)' : 'rgba(31,107,71,0.07)';
  for (let row = 0; row < SIZE; row += 1) {
    for (let column = 0; column < SIZE; column += 1) {
      const position = cellPosition(row, column);
      Engine.roundRect(ctx, position.x, position.y, board.cell, board.cell, board.cell * 0.2);
      ctx.fill();
    }
  }
  for (const tile of dying) drawTile(ctx, tile);
  for (const tile of tiles) drawTile(ctx, tile);
  drawLegend(ctx);
  particles.render(ctx);
}

Engine.input(stage.canvas, { swipe: move });
document.addEventListener('keydown', (event) => {
  const keys = { ArrowLeft: 'left', ArrowRight: 'right', ArrowUp: 'up', ArrowDown: 'down' };
  if (keys[event.code]) move(keys[event.code]);
});

reset();
Engine.loop(update, render);
Engine.startScreen(GP.t(TEXT.title), GP.t(TEXT.hint), start);
