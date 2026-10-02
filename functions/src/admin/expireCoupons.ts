import { onSchedule } from 'firebase-functions/v2/scheduler';
import { MAX_BATCH_WRITES, REGION, TIME_ZONE } from '../config';
import { COUPON_STATUS_ACTIVE, COUPON_STATUS_EXPIRED } from '../couponState';
import { db, paths } from '../db';

export const expireCoupons = onSchedule(
  { region: REGION, schedule: 'every day 03:00', timeZone: TIME_ZONE },
  async () => {
    const now = Date.now();
    for (;;) {
      const expired = await db.collection(paths.purchases())
        .where('status', '==', COUPON_STATUS_ACTIVE)
        .where('expiresAtEpochMillis', '<', now)
        .limit(MAX_BATCH_WRITES)
        .get();
      if (expired.empty) return;
      const batch = db.batch();
      expired.docs.forEach((coupon) => batch.update(coupon.ref, { status: COUPON_STATUS_EXPIRED }));
      await batch.commit();
    }
  },
);
