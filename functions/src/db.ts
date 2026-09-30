import { initializeApp } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import { getStorage } from 'firebase-admin/storage';
import { APP_ROOT } from './config';

initializeApp();

export const db = getFirestore();
export const storage = getStorage();

export const paths = {
  user: (uid: string) => `${APP_ROOT}/users/${uid}`,
  dailyCounter: (uid: string, day: string) => `${APP_ROOT}/users/${uid}/dailyCounters/${day}`,
  admin: (uid: string) => `${APP_ROOT}/admins/${uid}`,
  task: (taskId: string) => `${APP_ROOT}/tasks/${taskId}`,
  taskSecret: (taskId: string) => `${APP_ROOT}/taskSecrets/${taskId}`,
  taskProgress: (uid: string, taskId: string) => `${APP_ROOT}/taskProgress/${uid}_${taskId}`,
  submission: (submissionId: string) => `${APP_ROOT}/taskSubmissions/${submissionId}`,
  ecoTip: (tipId: string) => `${APP_ROOT}/ecoTips/${tipId}`,
  ecoTipRead: (uid: string, tipId: string) => `${APP_ROOT}/ecoTipReads/${uid}_${tipId}`,
  shopItem: (rewardId: string) => `${APP_ROOT}/shopItems/${rewardId}`,
  purchases: () => `${APP_ROOT}/purchases`,
  purchase: (couponId: string) => `${APP_ROOT}/purchases/${couponId}`,
  rewardState: (uid: string) => `${APP_ROOT}/users/${uid}/rewardState/main`,
  game: (gameId: string) => `${APP_ROOT}/games/${gameId}`,
  event: (eventId: string) => `${APP_ROOT}/events/${eventId}`,
  eventSecret: (eventId: string) => `${APP_ROOT}/eventSecrets/${eventId}`,
  eventAttendance: (uid: string, eventId: string) => `${APP_ROOT}/eventAttendance/${uid}_${eventId}`,
  eventRegistration: (uid: string, eventId: string) => `${APP_ROOT}/eventRegistrations/${uid}_${eventId}`,
  feedback: () => `${APP_ROOT}/feedback`,
  survey: (surveyId: string) => `${APP_ROOT}/surveys/${surveyId}`,
  surveyAnswer: (uid: string, surveyId: string) => `${APP_ROOT}/surveyAnswers/${uid}_${surveyId}`,
  pointsLedger: () => `${APP_ROOT}/pointsLedger`,
  post: (postId: string) => `${APP_ROOT}/posts/${postId}`,
};
