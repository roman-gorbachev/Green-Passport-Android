import assert from 'node:assert/strict';
import { test } from 'node:test';
import { isTextAllowed } from './wordFilter';

test('rejects obscene words and their disguised forms', () => {
  for (const text of ['х у й', 'xyй', 'хуууй', 'пи3дец', 'fuck', 'sh1t']) {
    assert.equal(isTextAllowed(text), false, text);
  }
});

test('accepts ordinary words that contain banned roots', () => {
  for (const text of ['оскорблять нельзя', 'колебание', 'по рублям', 'команда', 'он психует', 'Минск, субботник']) {
    assert.equal(isTextAllowed(text), true, text);
  }
});
