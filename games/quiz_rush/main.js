const TEXT = {
  title: { ru: 'Эко-блиц', be: 'Эка-бліц', en: 'Eco blitz' },
  hint: {
    ru: 'На каждый вопрос 10 секунд. Ошибка или тишина стоят жизни',
    be: 'На кожнае пытанне 10 секунд. Памылка або маўчанне каштуюць жыцця',
    en: 'You have 10 seconds per question. A mistake or a timeout costs a life',
  },
  streak: { ru: 'Серия: {0} 🔥', be: 'Серыя: {0} 🔥', en: 'Streak: {0} 🔥' },
  finished: { ru: 'Блиц окончен! Верных ответов: {0}', be: 'Бліц скончаны! Правільных адказаў: {0}', en: 'Blitz over! Correct answers: {0}' },
};

const SECONDS_PER_QUESTION = 10;
const URGENT_SECONDS = 3;
const ANSWER_PAUSE_MS = 900;
const CARD_SWAP_MS = 300;
const STREAK_TO_SHOW = 3;
const RING_RADIUS = 28;
const RING_LENGTH = 2 * Math.PI * RING_RADIUS;
const GAME_OVER_DELAY_MS = 700;

const stage = Engine.createStage(document.getElementById('fx'));
const particles = Engine.createParticles();
const scoreLabel = Engine.scoreLabel(document.getElementById('score'));
const card = document.getElementById('card');
const questionView = document.getElementById('question');
const answersView = document.getElementById('answers');
const ring = document.getElementById('ring');
const secondsView = document.getElementById('seconds');
const streakView = document.getElementById('streak');
ring.style.strokeDasharray = String(RING_LENGTH);

let colors = Engine.palette();
let state = 'ready';
let score = 0;
let streak = 0;
let deck = [];
let current = null;
let timeLeft = SECONDS_PER_QUESTION;
let answered = false;
let lives = null;

function reset() {
  colors = Engine.palette();
  score = 0;
  streak = 0;
  scoreLabel.set(0);
  streakView.textContent = '';
  deck = GP.shuffle(QUESTIONS);
  lives = GP.lives(document.getElementById('lives'), gameOver);
}

function start() {
  reset();
  state = 'playing';
  nextQuestion();
}

function nextQuestion() {
  if (state !== 'playing') return;
  if (deck.length === 0) deck = GP.shuffle(QUESTIONS);
  current = deck.pop();
  answered = false;
  timeLeft = SECONDS_PER_QUESTION;
  card.classList.add('leave');
  setTimeout(() => {
    questionView.textContent = GP.t(current.q);
    const options = GP.shuffle(current.a.map((answer, index) => ({ answer, isCorrect: index === 0 })));
    answersView.replaceChildren(...options.map((option) => {
      const button = GP.element('button', 'answer', GP.t(option.answer));
      button.dataset.correct = String(option.isCorrect);
      button.addEventListener('click', () => choose(button, option.isCorrect));
      return button;
    }));
    card.classList.remove('leave');
    card.classList.add('enter');
    requestAnimationFrame(() => requestAnimationFrame(() => card.classList.remove('enter')));
  }, CARD_SWAP_MS);
}

function burstAt(element, palette) {
  const rect = element.getBoundingClientRect();
  particles.burst(rect.left + rect.width / 2, rect.top + rect.height / 2, { count: 22, colors: palette, speed: 320, life: 0.8 });
}

function choose(button, isCorrect) {
  if (answered || state !== 'playing') return;
  answered = true;
  if (isCorrect) {
    button.classList.add('correct');
    score += 1;
    streak += 1;
    scoreLabel.set(score);
    Engine.sound.good();
    burstAt(button, ['#34C77B', colors.lime, '#FFD54F', '#ffffff']);
  } else {
    button.classList.add('wrong');
    revealCorrect();
    miss();
  }
  updateStreak();
  setTimeout(nextQuestion, ANSWER_PAUSE_MS);
}

function revealCorrect() {
  answersView.querySelector('[data-correct="true"]')?.classList.add('reveal');
}

function miss() {
  streak = 0;
  Engine.sound.bad();
  GP.vibrate(60);
  lives.lose();
}

function updateStreak() {
  streakView.textContent = streak >= STREAK_TO_SHOW ? GP.t(TEXT.streak, streak) : '';
}

function update(dt) {
  particles.update(dt);
  if (state !== 'playing' || answered || !current) return;
  timeLeft = Math.max(0, timeLeft - dt);
  if (timeLeft === 0) {
    answered = true;
    revealCorrect();
    miss();
    updateStreak();
    setTimeout(nextQuestion, ANSWER_PAUSE_MS);
  }
}

function render(dt) {
  stage.begin(dt);
  particles.render(stage.ctx);
  const progress = timeLeft / SECONDS_PER_QUESTION;
  ring.style.strokeDashoffset = String(RING_LENGTH * (1 - progress));
  ring.classList.toggle('urgent', timeLeft <= URGENT_SECONDS);
  secondsView.textContent = String(Math.ceil(timeLeft));
}

function gameOver() {
  if (state !== 'playing') return;
  state = 'over';
  Engine.sound.over();
  GP.finish(score);
  setTimeout(() => GP.showResult(GP.t(TEXT.finished, score), start), GAME_OVER_DELAY_MS);
}

reset();
Engine.loop(update, render);
Engine.startScreen(GP.t(TEXT.title), GP.t(TEXT.hint), start);
