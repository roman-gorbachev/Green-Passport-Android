import assert from 'node:assert/strict';
import { test } from 'node:test';
import { taskQrProblem } from './taskQr';

const NOW = 1_000_000;

test('a task without a window or a limit always accepts the code', () => {
  assert.equal(taskQrProblem({}, 100, NOW), null);
});

test('the window bounds are inclusive', () => {
  assert.equal(taskQrProblem({ qrActiveFromEpochMillis: NOW, qrActiveUntilEpochMillis: NOW }, 0, NOW), null);
  assert.equal(taskQrProblem({ qrActiveFromEpochMillis: NOW + 1 }, 0, NOW), 'notActive');
  assert.equal(taskQrProblem({ qrActiveUntilEpochMillis: NOW - 1 }, 0, NOW), 'notActive');
});

test('the limit counts scans of the current code', () => {
  assert.equal(taskQrProblem({ qrScanLimit: 2 }, 1, NOW), null);
  assert.equal(taskQrProblem({ qrScanLimit: 2 }, 2, NOW), 'limitReached');
});
