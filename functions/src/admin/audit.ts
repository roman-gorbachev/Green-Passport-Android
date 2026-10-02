import { onDocumentWritten } from 'firebase-functions/v2/firestore';
import { APP_ROOT, FIRESTORE_TRIGGER_REGION } from '../config';
import { db, paths } from '../db';
import { auditRecord } from './auditDiff';

function auditTrigger(collection: string) {
  return onDocumentWritten(
    { region: FIRESTORE_TRIGGER_REGION, document: `${APP_ROOT}/${collection}/{docId}` },
    async (event) => {
      const record = auditRecord(event.data?.before.data(), event.data?.after.data());
      if (!record) return;
      await db.collection(paths.auditLog()).add({
        ...record,
        collection,
        docId: event.params.docId,
        atEpochMillis: Date.now(),
      });
    },
  );
}

export const auditTasks = auditTrigger('tasks');
export const auditEvents = auditTrigger('events');
export const auditShopItems = auditTrigger('shopItems');
export const auditEcoTips = auditTrigger('ecoTips');
export const auditMapPoints = auditTrigger('mapPoints');
export const auditSurveys = auditTrigger('surveys');
export const auditGames = auditTrigger('games');
export const auditPartners = auditTrigger('partners');
