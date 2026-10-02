import assert from 'node:assert/strict';
import { test } from 'node:test';
import { couponRedeemUrl, couponState } from './couponState';

const NOW = 1_000_000;

test('an active coupon with the right code is active', () => {
  assert.equal(couponState({ code: 'ABC', status: 'ACTIVE', expiresAtEpochMillis: NOW + 1 }, 'ABC', NOW), 'active');
  assert.equal(couponState({ code: 'ABC', status: 'ACTIVE' }, 'ABC', NOW), 'active');
});

test('a wrong code or a missing coupon is not found', () => {
  assert.equal(couponState(undefined, 'ABC', NOW), 'notFound');
  assert.equal(couponState({ code: 'ABC', status: 'ACTIVE' }, 'XYZ', NOW), 'notFound');
  assert.equal(couponState({ code: 'ABC', status: 'ACTIVE' }, '', NOW), 'notFound');
});

test('used and expired coupons are reported', () => {
  assert.equal(couponState({ code: 'ABC', status: 'USED' }, 'ABC', NOW), 'used');
  assert.equal(couponState({ code: 'ABC', status: 'EXPIRED' }, 'ABC', NOW), 'expired');
  assert.equal(couponState({ code: 'ABC', status: 'ACTIVE', expiresAtEpochMillis: NOW - 1 }, 'ABC', NOW), 'expired');
});

test('the coupon link leads to the partner cabinet with the code escaped', () => {
  assert.equal(couponRedeemUrl('abc', 'AB2CD3EF'), 'https://greenpassport-admin.web.app/redeem?id=abc&code=AB2CD3EF');
  assert.equal(couponRedeemUrl('', 'A&B'), 'https://greenpassport-admin.web.app/redeem?code=A%26B');
});
