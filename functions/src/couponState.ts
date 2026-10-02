import { ADMIN_REDEEM_URL } from './config';

export const COUPON_STATUS_ACTIVE = 'ACTIVE';
export const COUPON_STATUS_USED = 'USED';
export const COUPON_STATUS_EXPIRED = 'EXPIRED';

export type CouponState = 'active' | 'used' | 'expired' | 'notFound';

export interface StoredCoupon {
  code?: string;
  status?: string;
  expiresAtEpochMillis?: number;
}

export function couponState(coupon: StoredCoupon | undefined, code: string, now: number): CouponState {
  if (!coupon || !code || coupon.code !== code) return 'notFound';
  if (coupon.status === COUPON_STATUS_USED) return 'used';
  if (coupon.status === COUPON_STATUS_EXPIRED) return 'expired';
  if (coupon.expiresAtEpochMillis !== undefined && coupon.expiresAtEpochMillis < now) return 'expired';
  return 'active';
}

export function couponRedeemUrl(couponId: string, code: string): string {
  const query = new URLSearchParams();
  if (couponId) query.set('id', couponId);
  if (code) query.set('code', code);
  const search = query.toString();
  return search ? `${ADMIN_REDEEM_URL}?${search}` : ADMIN_REDEEM_URL;
}
