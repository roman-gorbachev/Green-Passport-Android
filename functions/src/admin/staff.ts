import { getAuth } from 'firebase-admin/auth';
import { HttpsError, onCall } from 'firebase-functions/v2/https';
import { REGION } from '../config';
import { db, paths } from '../db';
import { requireString } from '../guards';
import { EDITORS, parseStaffRole, requireRole, STAFF_ROLES, StaffRole, SUPER_ADMINS } from './access';

export interface StaffMember {
  uid: string;
  email: string | null;
  role: StaffRole | null;
  partnerId: string | null;
}

const USER_NOT_FOUND_CODE = 'auth/user-not-found';
const MAX_USERS_PER_LOOKUP = 100;

async function uidByEmail(email: string): Promise<string> {
  try {
    return (await getAuth().getUserByEmail(email)).uid;
  } catch (error) {
    if ((error as { code?: string }).code === USER_NOT_FOUND_CODE) throw new HttpsError('not-found', 'user_not_found');
    throw error;
  }
}

async function staffMember(uid: string, email: string): Promise<StaffMember> {
  const [admin, partnerUser] = await Promise.all([
    db.doc(paths.admin(uid)).get(),
    db.doc(paths.partnerUser(uid)).get(),
  ]);
  return {
    uid,
    email,
    role: admin.exists ? parseStaffRole(admin.get('role')) : null,
    partnerId: (partnerUser.get('partnerId') as string | undefined) ?? null,
  };
}

function optionalString(data: unknown, field: string): string | null {
  const value = (data as Record<string, unknown> | null)?.[field];
  if (value === null || value === undefined) return null;
  if (typeof value !== 'string' || value.length === 0) throw new HttpsError('invalid-argument', `${field} is invalid`);
  return value;
}

export const adminSetStaffRole = onCall({ region: REGION }, async (request): Promise<StaffMember> => {
  const callerId = await requireRole(request, SUPER_ADMINS);
  const email = requireString(request.data, 'email').trim().toLowerCase();
  const role = optionalString(request.data, 'role');
  if (role !== null && !STAFF_ROLES.some((known) => known === role)) throw new HttpsError('invalid-argument', 'Unknown role');
  const uid = await uidByEmail(email);
  if (uid === callerId && role !== 'SUPER_ADMIN') throw new HttpsError('failed-precondition', 'cannot_demote_self');

  const adminRef = db.doc(paths.admin(uid));
  if (role === null) {
    await adminRef.delete();
  } else {
    await adminRef.set({ role, email, grantedBy: callerId, grantedAtEpochMillis: Date.now() }, { merge: true });
  }
  return staffMember(uid, email);
});

export const adminSetPartnerUser = onCall({ region: REGION }, async (request): Promise<StaffMember> => {
  const callerId = await requireRole(request, SUPER_ADMINS);
  const email = requireString(request.data, 'email').trim().toLowerCase();
  const partnerId = optionalString(request.data, 'partnerId');
  if (partnerId !== null && !(await db.doc(paths.partner(partnerId)).get()).exists) {
    throw new HttpsError('not-found', 'partner_not_found');
  }
  const uid = await uidByEmail(email);

  const partnerUserRef = db.doc(paths.partnerUser(uid));
  if (partnerId === null) {
    await partnerUserRef.delete();
  } else {
    await partnerUserRef.set({ partnerId, email, grantedBy: callerId, grantedAtEpochMillis: Date.now() });
  }
  return staffMember(uid, email);
});

async function emailsOf(uids: string[]): Promise<Map<string, string>> {
  const emails = new Map<string, string>();
  for (let start = 0; start < uids.length; start += MAX_USERS_PER_LOOKUP) {
    const chunk = uids.slice(start, start + MAX_USERS_PER_LOOKUP).map((uid) => ({ uid }));
    const { users } = await getAuth().getUsers(chunk);
    users.forEach((user) => user.email && emails.set(user.uid, user.email));
  }
  return emails;
}

export const adminListStaff = onCall({ region: REGION }, async (request): Promise<StaffMember[]> => {
  await requireRole(request, EDITORS);
  const [admins, partnerUsers] = await Promise.all([
    db.collection(paths.admins()).get(),
    db.collection(paths.partnerUsers()).get(),
  ]);
  const members = new Map<string, StaffMember>();
  const memberOf = (uid: string) => {
    const member = members.get(uid) ?? { uid, email: null, role: null, partnerId: null };
    members.set(uid, member);
    return member;
  };
  admins.docs.forEach((admin) => {
    const member = memberOf(admin.id);
    member.role = parseStaffRole(admin.get('role'));
    member.email = (admin.get('email') as string | undefined) ?? member.email;
  });
  partnerUsers.docs.forEach((partnerUser) => {
    const member = memberOf(partnerUser.id);
    member.partnerId = (partnerUser.get('partnerId') as string | undefined) ?? null;
    member.email = member.email ?? (partnerUser.get('email') as string | undefined) ?? null;
  });
  const emails = await emailsOf([...members.keys()]);
  return [...members.values()]
    .map((member) => ({ ...member, email: emails.get(member.uid) ?? member.email }))
    .sort((left, right) => (left.email ?? left.uid).localeCompare(right.email ?? right.uid));
});
