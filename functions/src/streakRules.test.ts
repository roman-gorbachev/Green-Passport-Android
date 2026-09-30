import assert from 'node:assert/strict';
import { test } from 'node:test';
import { STREAK_DAILY_BONUS, STREAK_WEEK_BONUS } from './config';
import { nextStreak } from './streakRules';

test('first activity starts a streak with the daily bonus', () => {
  assert.deepEqual(nextStreak(undefined, '2026-10-01', '2026-09-30'), {
    streak: { count: 1, lastDay: '2026-10-01' },
    bonus: STREAK_DAILY_BONUS,
  });
});

test('a second activity on the same day gives nothing', () => {
  assert.equal(nextStreak({ count: 3, lastDay: '2026-10-01' }, '2026-10-01', '2026-09-30'), null);
});

test('activity on the next day extends the streak', () => {
  assert.deepEqual(nextStreak({ count: 3, lastDay: '2026-09-30' }, '2026-10-01', '2026-09-30')?.streak, {
    count: 4,
    lastDay: '2026-10-01',
  });
});

test('a missed day resets the streak', () => {
  assert.equal(nextStreak({ count: 5, lastDay: '2026-09-28' }, '2026-10-01', '2026-09-30')?.streak.count, 1);
});

test('every seventh day in a row gives the weekly bonus', () => {
  assert.equal(nextStreak({ count: 6, lastDay: '2026-09-30' }, '2026-10-01', '2026-09-30')?.bonus, STREAK_WEEK_BONUS);
  assert.equal(nextStreak({ count: 13, lastDay: '2026-09-30' }, '2026-10-01', '2026-09-30')?.bonus, STREAK_WEEK_BONUS);
  assert.equal(nextStreak({ count: 7, lastDay: '2026-09-30' }, '2026-10-01', '2026-09-30')?.bonus, STREAK_DAILY_BONUS);
});
