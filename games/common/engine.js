const Engine = (() => {
  const MAX_FRAME_SECONDS = 0.05;
  const SWIPE_MIN_DISTANCE = 24;
  const TAP_MAX_DISTANCE = 12;
  const SHAKE_DECAY_SECONDS = 0.3;
  const EMOJI_FONT = '"Apple Color Emoji", "Segoe UI Emoji", "Noto Color Emoji", sans-serif';
  const glyphCache = new Map();

  function token(name) {
    return getComputedStyle(document.documentElement).getPropertyValue(name).trim();
  }

  function palette() {
    return {
      forest: token('--forest'),
      onForest: token('--on-forest'),
      lime: token('--lime'),
      mint: token('--mint-surface'),
      mintHigh: token('--mint-surface-high'),
      background: token('--background'),
      card: token('--card'),
      text: token('--text'),
      secondary: token('--secondary-text'),
      error: token('--error'),
      isDark: document.documentElement.dataset.theme === 'dark',
    };
  }

  function createStage(canvas) {
    const ctx = canvas.getContext('2d');
    const stage = { canvas, ctx, width: 0, height: 0, ratio: 1, shakePower: 0, shakeLeft: 0, onResize: null };

    function resize() {
      const rect = canvas.getBoundingClientRect();
      stage.ratio = window.devicePixelRatio || 1;
      stage.width = rect.width;
      stage.height = rect.height;
      canvas.width = Math.round(rect.width * stage.ratio);
      canvas.height = Math.round(rect.height * stage.ratio);
      if (stage.onResize) stage.onResize(stage);
    }

    stage.shake = (power = 8, seconds = SHAKE_DECAY_SECONDS) => {
      stage.shakePower = power;
      stage.shakeLeft = seconds;
    };

    stage.begin = (dt) => {
      ctx.setTransform(stage.ratio, 0, 0, stage.ratio, 0, 0);
      ctx.clearRect(0, 0, stage.width, stage.height);
      stage.shakeLeft = Math.max(0, stage.shakeLeft - dt);
      if (stage.shakeLeft > 0) {
        const power = stage.shakePower * (stage.shakeLeft / SHAKE_DECAY_SECONDS);
        ctx.translate((Math.random() - 0.5) * power, (Math.random() - 0.5) * power);
      }
    };

    new ResizeObserver(resize).observe(canvas);
    resize();
    return stage;
  }

  function loop(update, render) {
    let last = performance.now();
    function frame(now) {
      const dt = Math.min(MAX_FRAME_SECONDS, Math.max(0, (now - last) / 1000));
      last = now;
      update(dt);
      render(dt);
      requestAnimationFrame(frame);
    }
    document.addEventListener('visibilitychange', () => {
      last = performance.now();
    });
    requestAnimationFrame(frame);
  }

  function input(canvas, handlers) {
    let start = null;

    function point(event) {
      const rect = canvas.getBoundingClientRect();
      return { x: event.clientX - rect.left, y: event.clientY - rect.top };
    }

    canvas.addEventListener('pointerdown', (event) => {
      event.preventDefault();
      canvas.setPointerCapture?.(event.pointerId);
      start = point(event);
      handlers.down?.(start.x, start.y);
    });
    canvas.addEventListener('pointermove', (event) => {
      event.preventDefault();
      if (!start) return;
      const current = point(event);
      handlers.move?.(current.x, current.y);
    });
    const finish = (event) => {
      if (!start) return;
      const end = point(event);
      const dx = end.x - start.x;
      const dy = end.y - start.y;
      const distance = Math.hypot(dx, dy);
      if (distance <= TAP_MAX_DISTANCE) {
        handlers.tap?.(start.x, start.y);
      } else if (distance >= SWIPE_MIN_DISTANCE) {
        const direction = Math.abs(dx) > Math.abs(dy) ? (dx > 0 ? 'right' : 'left') : dy > 0 ? 'down' : 'up';
        handlers.swipe?.(direction);
      }
      handlers.up?.(end.x, end.y);
      start = null;
    };
    canvas.addEventListener('pointerup', finish);
    canvas.addEventListener('pointercancel', finish);
  }

  function glyph(emoji, size) {
    const ratio = window.devicePixelRatio || 1;
    const key = emoji + '|' + Math.round(size) + '|' + ratio;
    let image = glyphCache.get(key);
    if (!image) {
      const side = Math.ceil(size * 1.3 * ratio);
      image = document.createElement('canvas');
      image.width = side;
      image.height = side;
      const context = image.getContext('2d');
      context.textAlign = 'center';
      context.textBaseline = 'middle';
      context.font = `${size * ratio}px ${EMOJI_FONT}`;
      context.fillText(emoji, side / 2, side / 2 + size * ratio * 0.06);
      glyphCache.set(key, image);
    }
    return image;
  }

  function drawGlyph(ctx, emoji, x, y, size, rotation = 0, scale = 1) {
    const image = glyph(emoji, size);
    const side = size * 1.3;
    ctx.save();
    ctx.translate(x, y);
    if (rotation) ctx.rotate(rotation);
    if (scale !== 1) ctx.scale(scale, scale);
    ctx.drawImage(image, -side / 2, -side / 2, side, side);
    ctx.restore();
  }

  function createParticles() {
    let items = [];
    return {
      burst(x, y, options = {}) {
        const count = options.count ?? 14;
        const colors = options.colors ?? ['#34C77B'];
        const speed = options.speed ?? 220;
        for (let index = 0; index < count; index += 1) {
          const angle = options.angle !== undefined
            ? options.angle + (Math.random() - 0.5) * (options.spread ?? Math.PI)
            : Math.random() * Math.PI * 2;
          const velocity = speed * (0.4 + Math.random() * 0.6);
          items.push({
            x,
            y,
            vx: Math.cos(angle) * velocity,
            vy: Math.sin(angle) * velocity,
            gravity: options.gravity ?? 520,
            life: options.life ?? 0.7,
            age: 0,
            size: (options.size ?? 6) * (0.6 + Math.random() * 0.8),
            color: colors[index % colors.length],
            glyph: options.glyph,
            spin: (Math.random() - 0.5) * 8,
            rotation: Math.random() * Math.PI,
          });
        }
      },
      text(x, y, value, color, size = 22) {
        items.push({ x, y, vx: 0, vy: -70, gravity: 0, life: 0.8, age: 0, size, color, label: value, spin: 0, rotation: 0 });
      },
      update(dt) {
        for (const item of items) {
          item.age += dt;
          item.vy += item.gravity * dt;
          item.x += item.vx * dt;
          item.y += item.vy * dt;
          item.rotation += item.spin * dt;
        }
        items = items.filter((item) => item.age < item.life);
      },
      render(ctx) {
        for (const item of items) {
          const progress = item.age / item.life;
          ctx.globalAlpha = 1 - progress * progress;
          if (item.label) {
            ctx.fillStyle = item.color;
            ctx.font = `800 ${item.size}px -apple-system, system-ui, Roboto, sans-serif`;
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(item.label, item.x, item.y);
          } else if (item.glyph) {
            drawGlyph(ctx, item.glyph, item.x, item.y, item.size * 2.4, item.rotation);
          } else {
            ctx.save();
            ctx.translate(item.x, item.y);
            ctx.rotate(item.rotation);
            ctx.fillStyle = item.color;
            ctx.fillRect(-item.size / 2, -item.size / 2, item.size, item.size * 0.6);
            ctx.restore();
          }
        }
        ctx.globalAlpha = 1;
      },
      clear() {
        items = [];
      },
    };
  }

  const ease = {
    outCubic: (t) => 1 - Math.pow(1 - t, 3),
    inOutSine: (t) => -(Math.cos(Math.PI * t) - 1) / 2,
    outBack: (t) => {
      const overshoot = 1.70158;
      return 1 + (overshoot + 1) * Math.pow(t - 1, 3) + overshoot * Math.pow(t - 1, 2);
    },
  };

  function lerp(from, to, t) {
    return from + (to - from) * t;
  }

  function clamp(value, min, max) {
    return Math.min(max, Math.max(min, value));
  }

  function random(min, max) {
    return min + Math.random() * (max - min);
  }

  const sound = (() => {
    let context = null;

    function unlock() {
      if (!context) {
        const AudioContext = window.AudioContext || window.webkitAudioContext;
        if (!AudioContext) return;
        context = new AudioContext();
      }
      if (context.state === 'suspended') context.resume();
    }

    function tone(frequency, seconds, type = 'sine', volume = 0.12, slideTo = frequency, delay = 0) {
      if (!context) return;
      const start = context.currentTime + delay;
      const oscillator = context.createOscillator();
      const gain = context.createGain();
      oscillator.type = type;
      oscillator.frequency.setValueAtTime(frequency, start);
      oscillator.frequency.exponentialRampToValueAtTime(slideTo, start + seconds);
      gain.gain.setValueAtTime(0.0001, start);
      gain.gain.exponentialRampToValueAtTime(volume, start + 0.01);
      gain.gain.exponentialRampToValueAtTime(0.0001, start + seconds);
      oscillator.connect(gain).connect(context.destination);
      oscillator.start(start);
      oscillator.stop(start + seconds + 0.02);
    }

    window.addEventListener('pointerdown', unlock, { passive: true });

    return {
      unlock,
      pick: () => tone(880, 0.09, 'triangle', 0.1, 1320),
      good: () => {
        tone(660, 0.08, 'triangle', 0.1);
        tone(990, 0.12, 'triangle', 0.1, 990, 0.07);
      },
      bad: () => tone(220, 0.25, 'sawtooth', 0.08, 90),
      jump: () => tone(380, 0.12, 'square', 0.04, 760),
      pop: (level = 1) => tone(320 + level * 70, 0.1, 'sine', 0.12, 480 + level * 90),
      splash: () => tone(900, 0.15, 'sine', 0.06, 200),
      over: () => {
        tone(523, 0.15, 'triangle', 0.1);
        tone(392, 0.15, 'triangle', 0.1, 392, 0.14);
        tone(262, 0.3, 'triangle', 0.1, 262, 0.28);
      },
    };
  })();

  const START_TEXT = {
    tap: { ru: 'Коснись, чтобы начать', be: 'Націсні, каб пачаць', en: 'Tap to start' },
  };

  function startScreen(title, hint, onStart) {
    const overlay = GP.element('div', 'start');
    const mascot = document.createElement('img');
    mascot.src = '../common/mascot.png';
    mascot.alt = '';
    const tap = GP.element('p', 'pulse', GP.t(START_TEXT.tap));
    overlay.append(mascot, GP.element('h2', '', title), GP.element('p', 'hint', hint), tap);
    overlay.addEventListener('pointerdown', (event) => {
      event.preventDefault();
      sound.unlock();
      overlay.classList.add('leaving');
      setTimeout(() => overlay.remove(), 200);
      onStart();
    }, { once: true });
    document.body.append(overlay);
  }

  function roundRect(ctx, x, y, width, height, radius) {
    ctx.beginPath();
    ctx.roundRect(x, y, width, height, radius);
  }

  function scoreLabel(element) {
    return {
      set(value) {
        element.textContent = String(value);
        element.classList.remove('bump');
        void element.offsetWidth;
        element.classList.add('bump');
      },
    };
  }

  return { scoreLabel, palette, createStage, loop, input, glyph, drawGlyph, createParticles, ease, lerp, clamp, random, sound, startScreen, roundRect };
})();
