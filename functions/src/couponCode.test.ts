import assert from 'node:assert/strict';
import { test } from 'node:test';
import { COUPON_CODE_ALPHABET, COUPON_CODE_LENGTH, generateCouponCode } from './couponCode';

test('coupon codes have a fixed length and use only unambiguous characters', () => {
  for (let attempt = 0; attempt < 500; attempt += 1) {
    const code = generateCouponCode();
    assert.equal(code.length, COUPON_CODE_LENGTH);
    for (const character of code) {
      assert.ok(COUPON_CODE_ALPHABET.includes(character), code);
    }
  }
});

test('coupon codes are not repeated in a small sample', () => {
  const codes = new Set(Array.from({ length: 1000 }, () => generateCouponCode()));
  assert.equal(codes.size, 1000);
});
