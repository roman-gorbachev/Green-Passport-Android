const GP = (() => {
  const SUPPORTED_LANGUAGES = ['ru', 'be', 'en'];
  const params = new URLSearchParams(window.location.search);
  const requestedLanguage = params.get('lang');
  const lang = SUPPORTED_LANGUAGES.includes(requestedLanguage) ? requestedLanguage : 'ru';
  const requestedTheme = params.get('theme');
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  const theme = requestedTheme === 'dark' || requestedTheme === 'light' ? requestedTheme : prefersDark ? 'dark' : 'light';
  document.documentElement.dataset.theme = theme;
  document.documentElement.lang = lang;

  const COMMON = {
    playAgain: { ru: 'Играть снова', be: 'Гуляць зноў', en: 'Play again' },
  };

  function t(entry, ...args) {
    const text = entry[lang] ?? entry.ru;
    return text.replace(/\{(\d+)\}/g, (_, index) => String(args[Number(index)]));
  }

  function post(message) {
    const ios = window.webkit?.messageHandlers?.greenPassport;
    if (ios) {
      ios.postMessage(message);
    } else if (window.GreenPassportAndroid) {
      if (message.type === 'finish') {
        window.GreenPassportAndroid.finish(message.score);
      } else {
        window.GreenPassportAndroid.close();
      }
    } else {
      console.log('GreenPassport', message);
    }
  }

  function finish(score) {
    post({ type: 'finish', score: Math.max(0, Math.round(score)) });
  }

  function close() {
    post({ type: 'close' });
  }

  function vibrate(pattern) {
    if (navigator.vibrate) {
      navigator.vibrate(pattern);
    }
  }

  function showResult(message, onPlayAgain) {
    const overlay = document.createElement('div');
    overlay.className = 'result';
    const mascot = document.createElement('img');
    mascot.src = '../common/mascot.png';
    mascot.alt = '';
    const title = document.createElement('h2');
    title.textContent = message;
    const button = document.createElement('button');
    button.className = 'button';
    button.textContent = t(COMMON.playAgain);
    button.addEventListener('click', () => {
      overlay.remove();
      onPlayAgain();
    });
    overlay.append(mascot, title, button);
    document.body.append(overlay);
  }

  function shuffle(items) {
    const copy = [...items];
    for (let index = copy.length - 1; index > 0; index -= 1) {
      const other = Math.floor(Math.random() * (index + 1));
      [copy[index], copy[other]] = [copy[other], copy[index]];
    }
    return copy;
  }

  function randomItem(items) {
    return items[Math.floor(Math.random() * items.length)];
  }

  function element(tag, className, text) {
    const node = document.createElement(tag);
    if (className) node.className = className;
    if (text !== undefined) node.textContent = text;
    return node;
  }

  return { lang, t, finish, close, vibrate, showResult, shuffle, randomItem, element };
})();
