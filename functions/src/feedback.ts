import { HttpsError, onCall } from 'firebase-functions/v2/https';
import { DAY_MILLIS, FEEDBACK_MAX_LENGTH, FEEDBACK_POINTS, FEEDBACK_REWARD_COOLDOWN_DAYS, REGION, SURVEY_POINTS } from './config';
import { db, paths } from './db';
import { optionalNumber, requireString, requireUser } from './guards';
import { isTextAllowed } from './moderation/wordFilter';
import { award } from './rewards';
import { RewardWithStreak, withStreak } from './streak';

const FEEDBACK_TYPES = new Set(['REVIEW', 'SUGGESTION']);
const MIN_RATING = 1;
const MAX_RATING = 5;

export const submitFeedback = onCall({ region: REGION }, async (request): Promise<RewardWithStreak> => {
  const uid = requireUser(request);
  const type = requireString(request.data, 'type');
  const rawMessage = (request.data as { message?: unknown } | null)?.message;
  const message = typeof rawMessage === 'string' ? rawMessage.trim() : '';
  const rating = optionalNumber(request.data, 'rating');
  if (!FEEDBACK_TYPES.has(type)) throw new HttpsError('invalid-argument', 'Unknown feedback type');
  if (message.length > FEEDBACK_MAX_LENGTH) throw new HttpsError('invalid-argument', 'Message is too long');
  if (type === 'SUGGESTION' && message.length === 0) throw new HttpsError('invalid-argument', 'Message is required');
  if (type === 'REVIEW' && (rating === undefined || rating < MIN_RATING || rating > MAX_RATING)) {
    throw new HttpsError('invalid-argument', 'Rating is required');
  }
  if (message.length > 0 && !isTextAllowed(message)) throw new HttpsError('invalid-argument', 'Text contains banned words');

  const reward = await db.runTransaction(async (tx) => {
    const stateRef = db.doc(paths.rewardState(uid));
    const state = await tx.get(stateRef);
    const now = Date.now();
    tx.create(db.collection(paths.feedback()).doc(), {
      userId: uid,
      type,
      message,
      rating: type === 'REVIEW' ? rating : null,
      createdAtEpochMillis: now,
    });
    const lastRewardAt = Number(state.get('lastFeedbackRewardAtEpochMillis') ?? 0);
    if (now - lastRewardAt < FEEDBACK_REWARD_COOLDOWN_DAYS * DAY_MILLIS) return { points: 0, xp: 0 };
    tx.set(stateRef, { lastFeedbackRewardAtEpochMillis: now }, { merge: true });
    return award(tx, uid, { points: FEEDBACK_POINTS, xp: FEEDBACK_POINTS }, 'FEEDBACK_SUBMITTED', type);
  });
  return withStreak(uid, reward);
});

export const submitSurveyAnswer = onCall({ region: REGION }, async (request): Promise<RewardWithStreak> => {
  const uid = requireUser(request);
  const surveyId = requireString(request.data, 'surveyId');
  const optionIndex = optionalNumber(request.data, 'optionIndex');
  if (optionIndex === undefined || !Number.isInteger(optionIndex) || optionIndex < 0) {
    throw new HttpsError('invalid-argument', 'optionIndex is required');
  }

  const reward = await db.runTransaction(async (tx) => {
    const survey = await tx.get(db.doc(paths.survey(surveyId)));
    const answer = await tx.get(db.doc(paths.surveyAnswer(uid, surveyId)));
    if (!survey.exists) throw new HttpsError('not-found', 'Survey not found');
    if (answer.exists) throw new HttpsError('already-exists', 'Survey already answered');
    const options = (survey.get('options') as unknown[] | undefined) ?? [];
    if (optionIndex >= options.length) throw new HttpsError('invalid-argument', 'Unknown option');

    tx.create(db.doc(paths.surveyAnswer(uid, surveyId)), { userId: uid, surveyId, optionIndex });
    return award(tx, uid, { points: SURVEY_POINTS, xp: SURVEY_POINTS }, 'SURVEY_ANSWERED', surveyId);
  });
  return withStreak(uid, reward);
});
