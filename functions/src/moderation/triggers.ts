import vision from '@google-cloud/vision';
import { FieldValue } from 'firebase-admin/firestore';
import { logger } from 'firebase-functions/v2';
import { onDocumentCreated, onDocumentWritten } from 'firebase-functions/v2/firestore';
import { HttpsError, onCall } from 'firebase-functions/v2/https';
import { APP_ROOT, FIRESTORE_TRIGGER_REGION, REGION, REPORTS_TO_HIDE, SUBMISSIONS_STORAGE_PREFIX } from '../config';
import { db, paths, storage } from '../db';
import { requireAdmin, requireString } from '../guards';
import { isTextAllowed } from './wordFilter';

let visionClient: InstanceType<typeof vision.ImageAnnotatorClient> | null = null;

function getVisionClient() {
  visionClient ??= new vision.ImageAnnotatorClient();
  return visionClient;
}
const UNSAFE_LIKELIHOODS = new Set(['LIKELY', 'VERY_LIKELY']);
const MODERATION_ACTIONS = new Set(['hide', 'restore', 'delete']);

export const screenForumPost = onDocumentCreated(
  { region: FIRESTORE_TRIGGER_REGION, document: `${APP_ROOT}/posts/{postId}` },
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) return;
    const text = String(snapshot.get('text') ?? '');
    const authorName = String(snapshot.get('authorName') ?? '');
    if (!isTextAllowed(text) || !isTextAllowed(authorName)) {
      await snapshot.ref.update({ hidden: true, hiddenReason: 'banned_words' });
    }
  },
);

export const countContentReport = onDocumentCreated(
  { region: FIRESTORE_TRIGGER_REGION, document: `${APP_ROOT}/reports/{reportId}` },
  async (event) => {
    const postId = event.data?.get('postId') as string | undefined;
    if (!postId) return;
    const postRef = db.doc(paths.post(postId));
    await db.runTransaction(async (tx) => {
      const post = await tx.get(postRef);
      if (!post.exists) return;
      const reportCount = Number(post.get('reportCount') ?? 0) + 1;
      const update: Record<string, unknown> = { reportCount };
      if (reportCount >= REPORTS_TO_HIDE && post.get('hidden') !== true) {
        update.hidden = true;
        update.hiddenReason = 'reports';
      }
      tx.update(postRef, update);
    });
  },
);

export const screenSubmissionPhoto = onDocumentWritten(
  { region: FIRESTORE_TRIGGER_REGION, document: `${APP_ROOT}/taskSubmissions/{submissionId}` },
  async (event) => {
    const after = event.data?.after;
    const before = event.data?.before;
    if (!after?.exists || after.get('status') !== 'PENDING') return;
    const photoPath = after.get('photoPath') as string | undefined;
    if (!photoPath || photoPath === before?.get('photoPath')) return;
    if (!photoPath.startsWith(`${SUBMISSIONS_STORAGE_PREFIX}${after.get('userId')}/`)) {
      await after.ref.update({ status: 'REJECTED', rejectionReason: 'invalid_photo' });
      return;
    }

    if (process.env.FUNCTIONS_EMULATOR === 'true') {
      logger.info('Skipping Cloud Vision in the emulator', { submissionId: event.params.submissionId });
      await after.ref.update({ screenedAt: FieldValue.serverTimestamp() });
      return;
    }

    const bucket = storage.bucket();
    const [result] = await getVisionClient().safeSearchDetection(`gs://${bucket.name}/${photoPath}`);
    const annotation = result.safeSearchAnnotation;
    const isUnsafe = [annotation?.adult, annotation?.violence, annotation?.racy]
      .some((likelihood) => UNSAFE_LIKELIHOODS.has(String(likelihood)));
    if (!isUnsafe) {
      await after.ref.update({ screenedAt: FieldValue.serverTimestamp() });
      return;
    }
    logger.warn('Rejected unsafe submission photo', { submissionId: event.params.submissionId });
    await after.ref.update({ status: 'REJECTED', rejectionReason: 'unsafe_photo' });
    await bucket.file(photoPath).delete({ ignoreNotFound: true });
  },
);

export const moderateContent = onCall({ region: REGION }, async (request): Promise<{ action: string }> => {
  const moderatorId = await requireAdmin(request);
  const postId = requireString(request.data, 'postId');
  const action = requireString(request.data, 'action');
  if (!MODERATION_ACTIONS.has(action)) throw new HttpsError('invalid-argument', 'Unknown action');

  const postRef = db.doc(paths.post(postId));
  if (action === 'delete') {
    await db.recursiveDelete(postRef);
  } else {
    await postRef.update({
      hidden: action === 'hide',
      hiddenReason: action === 'hide' ? 'moderator' : null,
      reportCount: action === 'restore' ? 0 : FieldValue.increment(0),
      moderatedBy: moderatorId,
      moderatedAt: FieldValue.serverTimestamp(),
    });
  }
  return { action };
});
