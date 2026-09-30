import { randomInt } from 'node:crypto';

export const COUPON_CODE_ALPHABET = 'ABCDEFGHJKMNPQRSTUVWXYZ23456789';
export const COUPON_CODE_LENGTH = 8;

export function generateCouponCode(): string {
  let code = '';
  for (let index = 0; index < COUPON_CODE_LENGTH; index += 1) {
    code += COUPON_CODE_ALPHABET[randomInt(COUPON_CODE_ALPHABET.length)];
  }
  return code;
}
