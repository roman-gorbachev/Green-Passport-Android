import assert from 'node:assert/strict';
import { test } from 'node:test';
import { couponScanOutcome, renderScanPage, scanLanguage } from './couponScan';

const NOW = 1_000_000;

test('an active coupon with the right code is redeemed', () => {
  assert.equal(couponScanOutcome({ code: 'ABC', status: 'ACTIVE', expiresAtEpochMillis: NOW + 1 }, 'ABC', NOW), 'redeemed');
  assert.equal(couponScanOutcome({ code: 'ABC', status: 'ACTIVE' }, 'ABC', NOW), 'redeemed');
});

test('a wrong code or a missing coupon is not found', () => {
  assert.equal(couponScanOutcome(undefined, 'ABC', NOW), 'notFound');
  assert.equal(couponScanOutcome({ code: 'ABC', status: 'ACTIVE' }, 'XYZ', NOW), 'notFound');
  assert.equal(couponScanOutcome({ code: 'ABC', status: 'ACTIVE' }, '', NOW), 'notFound');
});

test('used and expired coupons are reported', () => {
  assert.equal(couponScanOutcome({ code: 'ABC', status: 'USED' }, 'ABC', NOW), 'alreadyUsed');
  assert.equal(couponScanOutcome({ code: 'ABC', status: 'EXPIRED' }, 'ABC', NOW), 'expired');
  assert.equal(couponScanOutcome({ code: 'ABC', status: 'ACTIVE', expiresAtEpochMillis: NOW - 1 }, 'ABC', NOW), 'expired');
});

test('the page language follows Accept-Language and escapes the reward title', () => {
  assert.equal(scanLanguage('be-BY,ru;q=0.9'), 'ru');
  assert.equal(scanLanguage('en-US'), 'en');
  assert.equal(scanLanguage(undefined), 'en');
  assert.ok(renderScanPage('redeemed', '<b>Кофе</b>', 'ru').includes('&#60;b&#62;Кофе'));
});
