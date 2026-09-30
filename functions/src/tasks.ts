import { FieldValue } from 'firebase-admin/firestore';
import { HttpsError, onCall } from 'firebase-functions/v2/https';
import {
  MAX_REJECTION_REASON_LENGTH,
  REGION,
  SELF_TASKS_PER_DAY,
  TASK_CODE_PREFIX,
} from './config';
import { dayKey } from './dates';
import { db, paths, storage } from './db';
import { requireAdmin, requireString, requireUser } from './guards';
import { award, readDailyCount, RewardResult, taskReward, writeDailyCount } from './rewards';
import { withStreak } from './streak';

const SELF_TASKS_COUNTER = 'selfTasks';

export const completeSelfTask = onCall({ region: REGION }, async (request): Promise<RewardResult & { taskId: string; streakBonus: number }> => {
  const uid = requireUser(request);
  const taskId = requireString(request.data, 'taskId');
  const day = dayKey();

  const reward = await db.runTransaction(async (tx) => {
    const task = await tx.get(db.doc(paths.task(taskId)));
    const progress = await tx.get(db.doc(paths.taskProgress(uid, taskId)));
    const doneToday = await readDailyCount(tx, uid, day, SELF_TASKS_COUNTER);

    const verification = task.get('verification') ?? 'SELF';
    if (!task.exists) throw new HttpsError('not-found', 'Task not found');
    if (verification !== 'SELF') throw new HttpsError('failed-precondition', 'Task needs a photo or a QR code');
    if (progress.exists) throw new HttpsError('already-exists', 'Task already completed');
    if (doneToday >= SELF_TASKS_PER_DAY) throw new HttpsError('resource-exhausted', 'Daily limit reached');

    writeDailyCount(tx, uid, day, SELF_TASKS_COUNTER, doneToday + 1);
    tx.create(db.doc(paths.taskProgress(uid, taskId)), {
      userId: uid,
      taskId,
      verification: 'SELF',
      completedAtEpochMillis: Date.now(),
    });
    return award(tx, uid, taskReward(task.data()), 'TASK_COMPLETED', taskId);
  });
  return withStreak(uid, { ...reward, taskId });
});

export const redeemTaskCode = onCall({ region: REGION }, async (request): Promise<RewardResult & { taskId: string; streakBonus: number }> => {
  const uid = requireUser(request);
  const code = requireString(request.data, 'code').trim();
  if (!code.startsWith(TASK_CODE_PREFIX)) throw new HttpsError('not-found', 'Unknown code');
  const [taskId, secret] = code.slice(TASK_CODE_PREFIX.length).split(':');
  if (!taskId || !secret) throw new HttpsError('not-found', 'Unknown code');

  const reward = await db.runTransaction(async (tx) => {
    const task = await tx.get(db.doc(paths.task(taskId)));
    const taskSecret = await tx.get(db.doc(paths.taskSecret(taskId)));
    const progress = await tx.get(db.doc(paths.taskProgress(uid, taskId)));

    if (!task.exists || !taskSecret.exists || taskSecret.get('code') !== secret) {
      throw new HttpsError('not-found', 'Unknown code');
    }
    if (progress.exists) throw new HttpsError('already-exists', 'Task already completed');

    tx.create(db.doc(paths.taskProgress(uid, taskId)), {
      userId: uid,
      taskId,
      verification: 'QR',
      completedAtEpochMillis: Date.now(),
    });
    return award(tx, uid, taskReward(task.data()), 'TASK_COMPLETED', taskId);
  });
  return withStreak(uid, { ...reward, taskId });
});

export const reviewSubmission = onCall({ region: REGION }, async (request): Promise<{ status: string }> => {
  const moderatorId = await requireAdmin(request);
  const submissionId = requireString(request.data, 'submissionId');
  const approve = (request.data as { approve?: unknown })?.approve === true;
  const rawReason = (request.data as { reason?: unknown })?.reason;
  const reason = typeof rawReason === 'string' ? rawReason.slice(0, MAX_REJECTION_REASON_LENGTH) : null;

  let photoPathToDelete: string | null = null;
  const status = await db.runTransaction(async (tx) => {
    const submissionRef = db.doc(paths.submission(submissionId));
    const submission = await tx.get(submissionRef);
    if (!submission.exists) throw new HttpsError('not-found', 'Submission not found');
    if (submission.get('status') !== 'PENDING') throw new HttpsError('failed-precondition', 'Already reviewed');

    const uid = submission.get('userId') as string;
    const taskId = submission.get('taskId') as string;
    const task = await tx.get(db.doc(paths.task(taskId)));
    const progress = await tx.get(db.doc(paths.taskProgress(uid, taskId)));

    const review = { reviewedBy: moderatorId, reviewedAt: FieldValue.serverTimestamp() };
    if (!approve) {
      tx.update(submissionRef, { ...review, status: 'REJECTED', rejectionReason: reason });
      photoPathToDelete = submission.get('photoPath') as string;
      return 'REJECTED';
    }
    tx.update(submissionRef, { ...review, status: 'APPROVED', rejectionReason: null });
    if (!progress.exists) {
      tx.create(db.doc(paths.taskProgress(uid, taskId)), {
        userId: uid,
        taskId,
        verification: 'PHOTO',
        completedAtEpochMillis: Date.now(),
      });
      award(tx, uid, taskReward(task.data()), 'TASK_COMPLETED', taskId);
    }
    return 'APPROVED';
  });

  if (photoPathToDelete) {
    await storage.bucket().file(photoPathToDelete).delete({ ignoreNotFound: true });
  }
  return { status };
});
