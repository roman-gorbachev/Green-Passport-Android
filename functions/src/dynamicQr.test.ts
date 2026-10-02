import assert from 'node:assert/strict';
import { test } from 'node:test';
import { dynamicSignature, isValidDynamicSecret } from './dynamicQr';

const KEY = '000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f';
const NOW = 1_790_000_000_000;
const WINDOW = 59_666_666;

test('the signature matches the admin test vector', () => {
  assert.equal(dynamicSignature(KEY, 'cleanup_2026', WINDOW), '362f08279b8a');
});

test('the current and the previous window are accepted', () => {
  assert.ok(isValidDynamicSecret(`d${WINDOW}.362f08279b8a`, 'cleanup_2026', KEY, NOW));
  const previous = WINDOW - 1;
  assert.ok(isValidDynamicSecret(`d${previous}.${dynamicSignature(KEY, 'cleanup_2026', previous)}`, 'cleanup_2026', KEY, NOW));
});

test('old, future, foreign and malformed secrets are rejected', () => {
  const old = WINDOW - 2;
  assert.equal(isValidDynamicSecret(`d${old}.${dynamicSignature(KEY, 'cleanup_2026', old)}`, 'cleanup_2026', KEY, NOW), false);
  const future = WINDOW + 1;
  assert.equal(isValidDynamicSecret(`d${future}.${dynamicSignature(KEY, 'cleanup_2026', future)}`, 'cleanup_2026', KEY, NOW), false);
  assert.equal(isValidDynamicSecret(`d${WINDOW}.362f08279b8a`, 'other_event', KEY, NOW), false);
  assert.equal(isValidDynamicSecret(`d${WINDOW}.362f`, 'cleanup_2026', KEY, NOW), false);
  assert.equal(isValidDynamicSecret('static-secret', 'cleanup_2026', KEY, NOW), false);
});
