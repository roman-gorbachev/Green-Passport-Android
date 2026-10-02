import { createHmac, timingSafeEqual } from 'node:crypto';
import { DYNAMIC_ACCEPTED_PAST_WINDOWS, DYNAMIC_SIGNATURE_LENGTH, DYNAMIC_WINDOW_MILLIS } from './config';

const DYNAMIC_SECRET_PATTERN = /^d(\d+)\.([0-9a-f]+)$/;

export function dynamicSignature(keyHex: string, eventId: string, window: number): string {
  return createHmac('sha256', Buffer.from(keyHex, 'hex'))
    .update(`${eventId}:${window}`)
    .digest('hex')
    .slice(0, DYNAMIC_SIGNATURE_LENGTH);
}

export function isDynamicSecret(secret: string): boolean {
  return DYNAMIC_SECRET_PATTERN.test(secret);
}

export function isValidDynamicSecret(secret: string, eventId: string, keyHex: string, now: number): boolean {
  const match = DYNAMIC_SECRET_PATTERN.exec(secret);
  if (!match) return false;
  const window = Number(match[1]);
  const current = Math.floor(now / DYNAMIC_WINDOW_MILLIS);
  if (window > current || current - window > DYNAMIC_ACCEPTED_PAST_WINDOWS) return false;
  const expected = Buffer.from(dynamicSignature(keyHex, eventId, window));
  const actual = Buffer.from(match[2]);
  return actual.length === expected.length && timingSafeEqual(actual, expected);
}
