import { onDocumentWritten } from 'firebase-functions/v2/firestore';
import { APP_ROOT, FIRESTORE_TRIGGER_REGION, PROFILE_BONUS_POINTS } from './config';
import { db, paths } from './db';
import { award } from './rewards';

export const awardProfileBonus = onDocumentWritten(
  { document: `${APP_ROOT}/users/{userId}`, region: FIRESTORE_TRIGGER_REGION },
  async (event) => {
    const after = event.data?.after;
    if (!after?.exists || after.get('profileCompletedAt') == null || after.get('profileBonusAwarded') === true) {
      return;
    }
    const uid = event.params.userId;
    await db.runTransaction(async (tx) => {
      const userRef = db.doc(paths.user(uid));
      const user = await tx.get(userRef);
      if (user.get('profileBonusAwarded') === true) return;
      tx.set(userRef, { profileBonusAwarded: true }, { merge: true });
      award(tx, uid, { points: PROFILE_BONUS_POINTS, xp: PROFILE_BONUS_POINTS }, 'PROFILE_COMPLETED', uid);
    });
  },
);
