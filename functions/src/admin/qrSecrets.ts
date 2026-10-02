import { randomBytes } from 'node:crypto';
import { DocumentSnapshot, Transaction } from 'firebase-admin/firestore';
import { HttpsError, onCall } from 'firebase-functions/v2/https';
import {
  DYNAMIC_KEY_BYTES,
  EVENT_CODE_PREFIX,
  MAX_QR_PAYLOADS_PER_CALL,
  QR_SECRET_BYTES,
  REGION,
  TASK_CODE_PREFIX,
} from '../config';
import { db, paths } from '../db';
import { requireString } from '../guards';
import { EDITORS, requireRole } from './access';

type QrKind = 'task' | 'event';

export interface QrPayload {
  id: string;
  payload: string;
  version: number;
  scanCount: number;
  rotatedAtEpochMillis: number | null;
}

const kinds: Record<QrKind, { content: (id: string) => string; secret: (id: string) => string; prefix: string }> = {
  task: { content: paths.task, secret: paths.taskSecret, prefix: TASK_CODE_PREFIX },
  event: { content: paths.event, secret: paths.eventSecret, prefix: EVENT_CODE_PREFIX },
};

function requireKind(data: unknown): QrKind {
  const kind = requireString(data, 'kind');
  if (kind !== 'task' && kind !== 'event') throw new HttpsError('invalid-argument', 'Unknown kind');
  return kind;
}

export function newQrSecret(): string {
  return randomBytes(QR_SECRET_BYTES).toString('hex');
}

function freshSecret(now: number, version: number) {
  return { code: newQrSecret(), version, rotatedAtEpochMillis: now, scanCount: 0 };
}

function toPayload(kind: QrKind, id: string, secret: Record<string, unknown>): QrPayload {
  return {
    id,
    payload: `${kinds[kind].prefix}${id}:${String(secret.code)}`,
    version: Number(secret.version ?? 1),
    scanCount: Number(secret.scanCount ?? 0),
    rotatedAtEpochMillis: typeof secret.rotatedAtEpochMillis === 'number' ? secret.rotatedAtEpochMillis : null,
  };
}

async function requireContent(tx: Transaction, kind: QrKind, id: string): Promise<DocumentSnapshot> {
  const content = await tx.get(db.doc(kinds[kind].content(id)));
  if (!content.exists) throw new HttpsError('not-found', 'content_not_found');
  return content;
}

export const adminEnsureQrSecret = onCall({ region: REGION }, async (request): Promise<QrPayload> => {
  await requireRole(request, EDITORS);
  const kind = requireKind(request.data);
  const id = requireString(request.data, 'id');
  return db.runTransaction(async (tx) => {
    await requireContent(tx, kind, id);
    const secretRef = db.doc(kinds[kind].secret(id));
    const secret = await tx.get(secretRef);
    if (typeof secret.get('code') === 'string') return toPayload(kind, id, secret.data() ?? {});
    const created = freshSecret(Date.now(), 1);
    tx.set(secretRef, created, { merge: true });
    return toPayload(kind, id, created);
  });
});

export const adminRotateQrSecret = onCall({ region: REGION }, async (request): Promise<QrPayload> => {
  await requireRole(request, EDITORS);
  const kind = requireKind(request.data);
  const id = requireString(request.data, 'id');
  return db.runTransaction(async (tx) => {
    await requireContent(tx, kind, id);
    const secretRef = db.doc(kinds[kind].secret(id));
    const secret = await tx.get(secretRef);
    const rotated = freshSecret(Date.now(), Number(secret.get('version') ?? 1) + 1);
    tx.set(secretRef, rotated, { merge: true });
    return toPayload(kind, id, rotated);
  });
});

export const adminGetQrPayloads = onCall({ region: REGION }, async (request): Promise<QrPayload[]> => {
  await requireRole(request, EDITORS);
  const kind = requireKind(request.data);
  const rawIds = (request.data as { ids?: unknown })?.ids;
  if (!Array.isArray(rawIds) || rawIds.length > MAX_QR_PAYLOADS_PER_CALL) throw new HttpsError('invalid-argument', 'ids are invalid');
  const ids = [...new Set(rawIds.filter((id): id is string => typeof id === 'string' && id.length > 0 && !id.includes('/')))];
  if (ids.length === 0) return [];

  const contents = await db.getAll(...ids.map((id) => db.doc(kinds[kind].content(id))));
  const existing = contents.filter((content) => content.exists).map((content) => content.id);
  if (existing.length === 0) return [];
  const secrets = await db.getAll(...existing.map((id) => db.doc(kinds[kind].secret(id))));

  const batch = db.batch();
  const now = Date.now();
  const payloads = secrets.map((secret) => {
    if (typeof secret.get('code') === 'string') return toPayload(kind, secret.id, secret.data() ?? {});
    const created = freshSecret(now, 1);
    batch.set(secret.ref, created, { merge: true });
    return toPayload(kind, secret.id, created);
  });
  await batch.commit();
  return payloads;
});

export const adminGetEventDynamicKey = onCall({ region: REGION }, async (request): Promise<{ eventId: string; key: string }> => {
  await requireRole(request, EDITORS);
  const eventId = requireString(request.data, 'eventId');
  return db.runTransaction(async (tx) => {
    await requireContent(tx, 'event', eventId);
    const secretRef = db.doc(paths.eventSecret(eventId));
    const secret = await tx.get(secretRef);
    const existingKey = secret.get('dynamicKey');
    if (typeof existingKey === 'string') return { eventId, key: existingKey };
    const key = randomBytes(DYNAMIC_KEY_BYTES).toString('hex');
    tx.set(secretRef, { ...(typeof secret.get('code') === 'string' ? {} : freshSecret(Date.now(), 1)), dynamicKey: key }, { merge: true });
    return { eventId, key };
  });
});
