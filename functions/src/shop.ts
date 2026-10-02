import { FieldValue } from 'firebase-admin/firestore';
import { HttpsError, onCall, onRequest } from 'firebase-functions/v2/https';
import { CODE_STATUS_AVAILABLE, CODE_STATUS_ISSUED } from './admin/coupons';
import { DAY_MILLIS, DEFAULT_COUPON_VALIDITY_DAYS, REGION } from './config';
import { generateCouponCode } from './couponCode';
import { COUPON_STATUS_ACTIVE, COUPON_STATUS_USED, couponRedeemUrl } from './couponState';
import { db, paths } from './db';
import { requireString, requireUser } from './guards';

const CODE_SOURCE_POOL = 'POOL';
const REDIRECT_STATUS = 302;

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
    const rewardRef = db.doc(paths.shopItem(rewardId));
    const reward = await tx.get(rewardRef);
    const user = await tx.get(db.doc(paths.user(uid)));
    if (!reward.exists || reward.get('isActive') === false) throw new HttpsError('not-found', 'Reward not found');

    const cost = Number(reward.get('pointsCost') ?? 0);
    const balance = Number(user.get('availablePoints') ?? 0);
    if (balance < cost) throw new HttpsError('failed-precondition', 'Not enough points');

    const stockLimit = Number(reward.get('stockLimit') ?? 0);
    if (stockLimit > 0 && Number(reward.get('issuedCount') ?? 0) >= stockLimit) {
      throw new HttpsError('resource-exhausted', 'reward_sold_out');
    }
    const isPool = reward.get('codeSource') === CODE_SOURCE_POOL;
    const poolQuery = db.collection(paths.codePool(rewardId)).where('status', '==', CODE_STATUS_AVAILABLE).limit(1);
    const poolCode = isPool ? (await tx.get(poolQuery)).docs[0] : undefined;
    if (isPool && !poolCode) throw new HttpsError('resource-exhausted', 'reward_sold_out');

    const validityDays = Number(reward.get('validityDays') ?? DEFAULT_COUPON_VALIDITY_DAYS);
    const redeemedAtEpochMillis = Date.now();
    const expiresAtEpochMillis = redeemedAtEpochMillis + validityDays * DAY_MILLIS;
    const code = poolCode?.id ?? generateCouponCode();
    const couponRef = db.collection(paths.purchases()).doc();
    const partnerId = (reward.get('partnerId') as string | undefined) ?? null;
    tx.set(db.doc(paths.user(uid)), { availablePoints: balance - cost }, { merge: true });
    tx.create(couponRef, {
      userId: uid,
      rewardId,
      redeemedAtEpochMillis,
      pointsCost: cost,
      code,
      expiresAtEpochMillis,
      status: COUPON_STATUS_ACTIVE,
      ...(partnerId ? { partnerId } : {}),
    });
    if (poolCode) {
      tx.update(poolCode.ref, { status: CODE_STATUS_ISSUED, couponId: couponRef.id, issuedAtEpochMillis: redeemedAtEpochMillis });
    }
    tx.update(rewardRef, {
      issuedCount: FieldValue.increment(1),
      ...(poolCode ? { poolAvailableCount: FieldValue.increment(-1) } : {}),
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
      const rewardRef = db.doc(paths.shopItem(String(coupon.get('rewardId'))));
      const reward = await tx.get(rewardRef);
      tx.update(couponRef, { status: COUPON_STATUS_USED, usedAtEpochMillis, redeemedByUid: uid });
      if (reward.exists) {
        tx.update(rewardRef, { usedCount: FieldValue.increment(1), selfMarkedCount: FieldValue.increment(1) });
      }
      return { couponId, usedAtEpochMillis };
    });
  },
);

export const scanCoupon = onRequest({ region: REGION }, (request, response) => {
  const couponId = typeof request.query.id === 'string' ? request.query.id : '';
  const code = typeof request.query.code === 'string' ? request.query.code : '';
  response.set('Cache-Control', 'no-store').redirect(REDIRECT_STATUS, couponRedeemUrl(couponId, code));
});
