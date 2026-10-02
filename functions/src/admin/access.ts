import { CallableRequest, HttpsError } from 'firebase-functions/v2/https';
import { db, paths } from '../db';
import { requireUser } from '../guards';

export const STAFF_ROLES = ['SUPER_ADMIN', 'EDITOR', 'MODERATOR'] as const;
export type StaffRole = (typeof STAFF_ROLES)[number];

export const SUPER_ADMINS: StaffRole[] = ['SUPER_ADMIN'];
export const EDITORS: StaffRole[] = ['SUPER_ADMIN', 'EDITOR'];

export interface CallerAccess {
  uid: string;
  role: StaffRole | null;
  partnerId: string | null;
}

export function parseStaffRole(value: unknown): StaffRole {
  return STAFF_ROLES.find((role) => role === value) ?? 'MODERATOR';
}

export async function callerAccess(request: CallableRequest<unknown>): Promise<CallerAccess> {
  const uid = requireUser(request);
  const [admin, partnerUser] = await Promise.all([
    db.doc(paths.admin(uid)).get(),
    db.doc(paths.partnerUser(uid)).get(),
  ]);
  const partnerId = partnerUser.get('partnerId');
  return {
    uid,
    role: admin.exists ? parseStaffRole(admin.get('role')) : null,
    partnerId: typeof partnerId === 'string' && partnerId ? partnerId : null,
  };
}

export async function requireRole(request: CallableRequest<unknown>, roles: StaffRole[]): Promise<string> {
  const access = await callerAccess(request);
  if (!access.role || !roles.includes(access.role)) throw new HttpsError('permission-denied', 'Insufficient role');
  return access.uid;
}

export function canServePartner(access: CallerAccess, partnerId: string | null): boolean {
  return access.role === 'SUPER_ADMIN' || (access.partnerId !== null && access.partnerId === partnerId);
}

export async function requirePartnerCabinet(request: CallableRequest<unknown>): Promise<CallerAccess> {
  const access = await callerAccess(request);
  if (access.role !== 'SUPER_ADMIN' && !access.partnerId) throw new HttpsError('permission-denied', 'Partners only');
  return access;
}
