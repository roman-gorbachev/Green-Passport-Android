import { HttpsError, onCall } from 'firebase-functions/v2/https';
import { DAY_MILLIS, DEFAULT_COUPON_VALIDITY_DAYS, REGION } from './config';
import { generateCouponCode } from './couponCode';
import { db, paths } from './db';
import { requireString, requireUser } from './guards';

export const COUPON_STATUS_ACTIVE = 'ACTIVE';
export const COUPON_STATUS_USED = 'USED';

export interface RedeemedCoupon {
  couponId: string;
  rewardId: string;
  redeemedAtEpochMillis: number;
  code: string;
  expiresAtEpochMillis: number;
}

export const redeemReward = onCall({ region: REGION }, async (request): Promise<RedeemedCoupon> => {
  const uid = requireUser(request);
  const rewardId = requireString(request.data, 'rewardId');

  return db.runTransaction(async (tx) => {
    const reward = await tx.get(db.doc(paths.shopItem(rewardId)));
    const user = await tx.get(db.doc(paths.user(uid)));
    if (!reward.exists) throw new HttpsError('not-found', 'Reward not found');

    const cost = Number(reward.get('pointsCost') ?? 0);
    const balance = Number(user.get('availablePoints') ?? 0);
    if (balance < cost) throw new HttpsError('failed-precondition', 'Not enough points');

    const validityDays = Number(reward.get('validityDays') ?? DEFAULT_COUPON_VALIDITY_DAYS);
    const redeemedAtEpochMillis = Date.now();
    const expiresAtEpochMillis = redeemedAtEpochMillis + validityDays * DAY_MILLIS;
    const code = generateCouponCode();
    const couponRef = db.collection(paths.purchases()).doc();
    tx.set(db.doc(paths.user(uid)), { availablePoints: balance - cost }, { merge: true });
    tx.create(couponRef, {
      userId: uid,
      rewardId,
      redeemedAtEpochMillis,
      pointsCost: cost,
      code,
      expiresAtEpochMillis,
      status: COUPON_STATUS_ACTIVE,
    });
    tx.create(db.collection(paths.pointsLedger()).doc(), {
      userId: uid,
      points: -cost,
      xp: 0,
      reason: 'COUPON_REDEEMED',
      referenceId: rewardId,
      createdAt: redeemedAtEpochMillis,
    });
    return { couponId: couponRef.id, rewardId, redeemedAtEpochMillis, code, expiresAtEpochMillis };
  });
});

export const markCouponUsed = onCall(
  { region: REGION },
  async (request): Promise<{ couponId: string; usedAtEpochMillis: number }> => {
    const uid = requireUser(request);
    const couponId = requireString(request.data, 'couponId');

    return db.runTransaction(async (tx) => {
      const couponRef = db.doc(paths.purchase(couponId));
      const coupon = await tx.get(couponRef);
      if (!coupon.exists || coupon.get('userId') !== uid) throw new HttpsError('not-found', 'Coupon not found');
      if (coupon.get('status') === COUPON_STATUS_USED) throw new HttpsError('already-exists', 'Coupon already used');
      const expiresAt = coupon.get('expiresAtEpochMillis') as number | undefined;
      const usedAtEpochMillis = Date.now();
      if (expiresAt !== undefined && expiresAt < usedAtEpochMillis) {
        throw new HttpsError('failed-precondition', 'Coupon expired');
      }
      tx.update(couponRef, { status: COUPON_STATUS_USED, usedAtEpochMillis });
      return { couponId, usedAtEpochMillis };
    });
  },
);
