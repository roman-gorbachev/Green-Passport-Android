import allowedStems from './wordlists/allowed_words.json';
import bannedRoots from './wordlists/banned_roots.json';

const WORD_SEPARATOR = /[^a-zа-я]+/;
const REPEATED_LETTERS = /(.)\1+/g;
const CYRILLIC_LOOKALIKES: Record<string, string> = {
  a: 'а', e: 'е', o: 'о', p: 'р', c: 'с', x: 'х', y: 'у', k: 'к', m: 'м', h: 'н', b: 'в', t: 'т', u: 'и', n: 'п',
  '0': 'о', '3': 'з', '6': 'б', '@': 'а',
};
const LATIN_LOOKALIKES: Record<string, string> = {
  а: 'a', е: 'e', о: 'o', р: 'p', с: 'c', х: 'x', у: 'y', к: 'k', м: 'm', т: 't', '0': 'o', '1': 'i', '3': 'e',
  '4': 'a', '@': 'a', $: 's',
};

function mapChars(text: string, table: Record<string, string>): string {
  return Array.from(text, (char) => table[char] ?? char).join('');
}

function words(text: string): string[] {
  const merged: string[] = [];
  let singleLetters = '';
  for (const token of text.split(WORD_SEPARATOR).filter((part) => part.length > 0)) {
    if (token.length === 1) {
      singleLetters += token;
    } else {
      if (singleLetters) merged.push(singleLetters);
      singleLetters = '';
      merged.push(token);
    }
  }
  if (singleLetters) merged.push(singleLetters);
  return merged.map((word) => word.replace(REPEATED_LETTERS, '$1'));
}

function isBanned(word: string): boolean {
  return !allowedStems.some((stem) => word.includes(stem)) && bannedRoots.some((root) => word.includes(root));
}

export function isTextAllowed(text: string): boolean {
  const lowercase = text.toLowerCase().replace(/ё/g, 'е');
  const candidates = [...words(mapChars(lowercase, CYRILLIC_LOOKALIKES)), ...words(mapChars(lowercase, LATIN_LOOKALIKES))];
  return !candidates.some(isBanned);
}
