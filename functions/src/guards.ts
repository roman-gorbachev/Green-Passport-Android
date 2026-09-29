import { CallableRequest, HttpsError } from 'firebase-functions/v2/https';
import { db, paths } from './db';

export function requireUser(request: CallableRequest<unknown>): string {
  const uid = request.auth?.uid;
  if (!uid) {
    throw new HttpsError('unauthenticated', 'Sign in first');
  }
  return uid;
}

export async function requireAdmin(request: CallableRequest<unknown>): Promise<string> {
  const uid = requireUser(request);
  const admin = await db.doc(paths.admin(uid)).get();
  if (!admin.exists) {
    throw new HttpsError('permission-denied', 'Moderators only');
  }
  return uid;
}

export function requireString(data: unknown, field: string): string {
  const value = (data as Record<string, unknown> | null)?.[field];
  if (typeof value !== 'string' || value.length === 0) {
    throw new HttpsError('invalid-argument', `${field} is required`);
  }
  return value;
}

export function optionalNumber(data: unknown, field: string): number | undefined {
  const value = (data as Record<string, unknown> | null)?.[field];
  return typeof value === 'number' && Number.isFinite(value) ? value : undefined;
}
