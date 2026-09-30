import { FieldValue } from 'firebase-admin/firestore';
import { dayKey, previousDayKey } from './dates';
import { db, paths } from './db';
import { RewardResult } from './rewards';
import { nextStreak, Streak } from './streakRules';

export interface RewardWithStreak extends RewardResult {
  streakBonus: number;
}

export async function applyDailyStreak(uid: string): Promise<number> {
  return db.runTransaction(async (tx) => {
    const userRef = db.doc(paths.user(uid));
    const user = await tx.get(userRef);
    const update = nextStreak(user.get('streak') as Streak | undefined, dayKey(), previousDayKey());
    if (!update) return 0;
    tx.set(
      userRef,
      {
        streak: update.streak,
        availablePoints: FieldValue.increment(update.bonus),
        lifetimeXp: FieldValue.increment(update.bonus),
      },
      { merge: true },
    );
    tx.create(db.collection(paths.pointsLedger()).doc(), {
      userId: uid,
      points: update.bonus,
      xp: update.bonus,
      reason: 'STREAK_BONUS',
      referenceId: update.streak.lastDay,
      createdAt: FieldValue.serverTimestamp(),
    });
    return update.bonus;
  });
}

export async function withStreak<T extends RewardResult>(uid: string, reward: T): Promise<T & { streakBonus: number }> {
  const streakBonus = reward.points > 0 || reward.xp > 0 ? await applyDailyStreak(uid) : 0;
  return { ...reward, streakBonus };
}
