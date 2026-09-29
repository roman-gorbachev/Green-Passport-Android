import { HttpsError, onCall } from 'firebase-functions/v2/https';
import { REGION } from './config';
import { db, paths } from './db';
import { requireString, requireUser } from './guards';

export const redeemReward = onCall(
  { region: REGION },
  async (request): Promise<{ couponId: string; rewardId: string; redeemedAtEpochMillis: number }> => {
    const uid = requireUser(request);
    const rewardId = requireString(request.data, 'rewardId');

    return db.runTransaction(async (tx) => {
      const reward = await tx.get(db.doc(paths.shopItem(rewardId)));
      const user = await tx.get(db.doc(paths.user(uid)));
      if (!reward.exists) throw new HttpsError('not-found', 'Reward not found');

      const cost = Number(reward.get('pointsCost') ?? 0);
      const balance = Number(user.get('availablePoints') ?? 0);
      if (balance < cost) throw new HttpsError('failed-precondition', 'Not enough points');

      const redeemedAtEpochMillis = Date.now();
      const couponRef = db.collection(paths.purchases()).doc();
      tx.set(db.doc(paths.user(uid)), { availablePoints: balance - cost }, { merge: true });
      tx.create(couponRef, { userId: uid, rewardId, redeemedAtEpochMillis, pointsCost: cost });
      tx.create(db.collection(paths.pointsLedger()).doc(), {
        userId: uid,
        points: -cost,
        xp: 0,
        reason: 'COUPON_REDEEMED',
        referenceId: rewardId,
        createdAt: redeemedAtEpochMillis,
      });
      return { couponId: couponRef.id, rewardId, redeemedAtEpochMillis };
    });
  },
);
