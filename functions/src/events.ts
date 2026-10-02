import { FieldValue } from 'firebase-admin/firestore';
import { HttpsError, onCall } from 'firebase-functions/v2/https';
import {
  EVENT_CHECK_IN_CLOSES_AFTER_MILLIS,
  EVENT_CHECK_IN_OPENS_BEFORE_MILLIS,
  EVENT_CODE_PREFIX,
  REGION,
} from './config';
import { db, paths } from './db';
import { isValidDynamicSecret } from './dynamicQr';
import { requireString, requireUser } from './guards';
import { award } from './rewards';
import { RewardWithStreak, withStreak } from './streak';

export const checkInEvent = onCall(
  { region: REGION },
  async (request): Promise<RewardWithStreak & { eventId: string }> => {
    const uid = requireUser(request);
    const code = requireString(request.data, 'code').trim();
    if (!code.startsWith(EVENT_CODE_PREFIX)) throw new HttpsError('not-found', 'Unknown code');
    const [eventId, secret] = code.slice(EVENT_CODE_PREFIX.length).split(':');
    if (!eventId || !secret) throw new HttpsError('not-found', 'Unknown code');

    const reward = await db.runTransaction(async (tx) => {
      const event = await tx.get(db.doc(paths.event(eventId)));
      const eventSecret = await tx.get(db.doc(paths.eventSecret(eventId)));
      const attendance = await tx.get(db.doc(paths.eventAttendance(uid, eventId)));
      const registration = await tx.get(db.doc(paths.eventRegistration(uid, eventId)));

      const now = Date.now();
      if (!event.exists || event.get('isActive') === false || !eventSecret.exists) {
        throw new HttpsError('not-found', 'Unknown code');
      }
      const isDynamic = event.get('qrMode') === 'DYNAMIC';
      const dynamicKey = eventSecret.get('dynamicKey') as string | undefined;
      const isValidCode = isDynamic
        ? dynamicKey !== undefined && isValidDynamicSecret(secret, eventId, dynamicKey, now)
        : eventSecret.get('code') === secret;
      if (!isValidCode) throw new HttpsError('not-found', 'Unknown code');
      if (attendance.exists) throw new HttpsError('already-exists', 'Already checked in');
      const startAt = Number(event.get('startAtEpochMillis') ?? 0);
      if (now < startAt - EVENT_CHECK_IN_OPENS_BEFORE_MILLIS || now > startAt + EVENT_CHECK_IN_CLOSES_AFTER_MILLIS) {
        throw new HttpsError('failed-precondition', 'Check-in is closed');
      }

      tx.update(eventSecret.ref, { scanCount: FieldValue.increment(1) });
      tx.create(db.doc(paths.eventAttendance(uid, eventId)), { userId: uid, eventId, checkedInAtEpochMillis: now });
      if (!registration.exists) {
        tx.create(db.doc(paths.eventRegistration(uid, eventId)), {
          userId: uid,
          eventId,
          registeredAtEpochMillis: now,
        });
      }
      const points = Number(event.get('rewardPoints') ?? 0);
      return award(tx, uid, { points, xp: points }, 'EVENT_ATTENDED', eventId);
    });
    return withStreak(uid, { ...reward, eventId });
  },
);
