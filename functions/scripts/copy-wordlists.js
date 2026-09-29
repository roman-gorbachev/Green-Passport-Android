const fs = require('fs');
const path = require('path');

const sourceDir = path.join(__dirname, '..', '..', 'core', 'src', 'main', 'res', 'raw');
const targetDir = path.join(__dirname, '..', 'src', 'moderation', 'wordlists');

fs.mkdirSync(targetDir, { recursive: true });
for (const name of ['banned_roots', 'allowed_words']) {
  const lines = fs.readFileSync(path.join(sourceDir, `${name}.txt`), 'utf8')
    .split('\n')
    .map((line) => line.trim().toLowerCase())
    .filter((line) => line.length > 0);
  fs.writeFileSync(path.join(targetDir, `${name}.json`), JSON.stringify(lines, null, 2));
}
console.log(`Word lists copied from ${sourceDir}`);
