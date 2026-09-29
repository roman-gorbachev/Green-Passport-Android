import { FieldValue, Transaction } from 'firebase-admin/firestore';
import { HttpsError } from 'firebase-functions/v2/https';
import { db, paths } from './db';

export interface RewardResult {
  points: number;
  xp: number;
}

export function award(
  tx: Transaction,
  uid: string,
  reward: RewardResult,
  reason: string,
  referenceId: string,
): RewardResult {
  tx.set(
    db.doc(paths.user(uid)),
    {
      availablePoints: FieldValue.increment(reward.points),
      lifetimeXp: FieldValue.increment(reward.xp),
    },
    { merge: true },
  );
  tx.create(db.collection(paths.pointsLedger()).doc(), {
    userId: uid,
    points: reward.points,
    xp: reward.xp,
    reason,
    referenceId,
    createdAt: FieldValue.serverTimestamp(),
  });
  return reward;
}

export async function readDailyCount(
  tx: Transaction,
  uid: string,
  day: string,
  field: string,
): Promise<number> {
  const snapshot = await tx.get(db.doc(paths.dailyCounter(uid, day)));
  return (snapshot.get(field) as number | undefined) ?? 0;
}

export function writeDailyCount(tx: Transaction, uid: string, day: string, field: string, value: number): void {
  tx.set(db.doc(paths.dailyCounter(uid, day)), { [field]: value }, { merge: true });
}

export function taskReward(taskData: FirebaseFirestore.DocumentData | undefined): RewardResult {
  if (!taskData) {
    throw new HttpsError('not-found', 'Task not found');
  }
  return {
    points: Number(taskData.rewardPoints ?? 0),
    xp: Number(taskData.rewardXp ?? 0),
  };
}
