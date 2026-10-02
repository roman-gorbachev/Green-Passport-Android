import { DocumentReference, DocumentSnapshot, FieldValue, Query, QuerySnapshot, Transaction } from 'firebase-admin/firestore';
import { HttpsError, onCall } from 'firebase-functions/v2/https';
import {
  COUPON_LOOKUP_LIMIT,
  MAX_BATCH_WRITES,
  MAX_IMPORTED_CODES_PER_CALL,
  MAX_PARTNER_CODE_LENGTH,
  PARTNER_HISTORY_LIMIT,
  REGION,
} from '../config';
import { COUPON_STATUS_USED, couponState, CouponState } from '../couponState';
import { db, paths } from '../db';
import { requireString } from '../guards';
import { callerAccess, CallerAccess, canServePartner, EDITORS, requirePartnerCabinet } from './access';

export const CODE_STATUS_AVAILABLE = 'AVAILABLE';
export const CODE_STATUS_ISSUED = 'ISSUED';

const RESERVED_ID_PATTERN = /^(\.{1,2}|__.*__)$/;
const PARTNER_CODE_PATTERN = new RegExp(`^[^\\s/]{1,${MAX_PARTNER_CODE_LENGTH}}$`);

export interface CouponPreview {
  couponId: string;
  state: CouponState;
  rewardId: string;
  rewardTitle: string;
  partnerName: string;
  code: string;
  redeemedAtEpochMillis: number | null;
  expiresAtEpochMillis: number | null;
  usedAtEpochMillis: number | null;
}

export interface Redemption {
  couponId: string;
  rewardId: string;
  rewardTitle: string;
  code: string;
  usedAtEpochMillis: number | null;
  isSelfMarked: boolean;
}

interface Reader {
  doc(ref: DocumentReference): Promise<DocumentSnapshot>;
  query(query: Query): Promise<QuerySnapshot>;
}

interface FoundCoupon {
  coupon: DocumentSnapshot;
  reward: DocumentSnapshot;
  partnerId: string | null;
}

const directReader: Reader = { doc: (ref) => ref.get(), query: (query) => query.get() };

function transactionReader(tx: Transaction): Reader {
  return { doc: (ref) => tx.get(ref), query: (query) => tx.get(query) };
}

export function isValidPartnerCode(code: string): boolean {
  return PARTNER_CODE_PATTERN.test(code) && !RESERVED_ID_PATTERN.test(code);
}

function numberOrNull(value: unknown): number | null {
  return typeof value === 'number' ? value : null;
}

async function withReward(reader: Reader, coupon: DocumentSnapshot): Promise<FoundCoupon> {
  const reward = await reader.doc(db.doc(paths.shopItem(String(coupon.get('rewardId')))));
  const partnerId = (coupon.get('partnerId') as string | undefined) ?? (reward.get('partnerId') as string | undefined) ?? null;
  return { coupon, reward, partnerId };
}

async function findCoupon(reader: Reader, access: CallerAccess, couponId: string | null, code: string): Promise<FoundCoupon | null> {
  let candidates: DocumentSnapshot[];
  if (couponId) {
    if (couponId.includes('/')) return null;
    const coupon = await reader.doc(db.doc(paths.purchase(couponId)));
    candidates = coupon.exists && coupon.get('code') === code ? [coupon] : [];
  } else {
    const query = db.collection(paths.purchases()).where('code', '==', code).limit(COUPON_LOOKUP_LIMIT);
    candidates = (await reader.query(query)).docs;
  }
  if (candidates.length === 0) return null;
  const found = await Promise.all(candidates.map((coupon) => withReward(reader, coupon)));
  const served = found.find((candidate) => canServePartner(access, candidate.partnerId));
  if (!served) throw new HttpsError('permission-denied', 'foreign_coupon');
  return served;
}

function requestedCouponId(data: unknown): string | null {
  const value = (data as { couponId?: unknown } | null)?.couponId;
  return typeof value === 'string' && value.length > 0 ? value : null;
}

export const adminImportCouponCodes = onCall(
  { region: REGION },
  async (request): Promise<{ added: number; duplicates: number; invalid: number }> => {
    const access = await callerAccess(request);
    const rewardId = requireString(request.data, 'rewardId');
    const rawCodes = (request.data as { codes?: unknown })?.codes;
    if (!Array.isArray(rawCodes) || rawCodes.length > MAX_IMPORTED_CODES_PER_CALL) {
      throw new HttpsError('invalid-argument', 'codes are invalid');
    }
    const rewardRef = db.doc(paths.shopItem(rewardId));
    const reward = rewardId.includes('/') ? undefined : await rewardRef.get();
    if (!reward?.exists) throw new HttpsError('not-found', 'reward_not_found');
    const isEditor = access.role !== null && EDITORS.includes(access.role);
    if (!isEditor && !canServePartner(access, (reward.get('partnerId') as string | undefined) ?? null)) {
      throw new HttpsError('permission-denied', 'Insufficient role');
    }

    const trimmed = rawCodes.map((code) => (typeof code === 'string' ? code.trim() : ''));
    const valid = [...new Set(trimmed.filter(isValidPartnerCode))];
    const invalid = trimmed.filter((code) => !isValidPartnerCode(code)).length;
    const repeatedInRequest = trimmed.length - invalid - valid.length;

    const pool = db.collection(paths.codePool(rewardId));
    let added = 0;
    for (let start = 0; start < valid.length; start += MAX_BATCH_WRITES) {
      const chunk = valid.slice(start, start + MAX_BATCH_WRITES);
      const existing = await db.getAll(...chunk.map((code) => pool.doc(code)));
      const fresh = existing.filter((snapshot) => !snapshot.exists);
      if (fresh.length === 0) continue;
      const batch = db.batch();
      const now = Date.now();
      fresh.forEach((snapshot) =>
        batch.create(snapshot.ref, { status: CODE_STATUS_AVAILABLE, importedBy: access.uid, importedAtEpochMillis: now }),
      );
      batch.update(rewardRef, { poolAvailableCount: FieldValue.increment(fresh.length) });
      await batch.commit();
      added += fresh.length;
    }
    return { added, duplicates: valid.length - added + repeatedInRequest, invalid };
  },
);

export const partnerPreviewCoupon = onCall({ region: REGION }, async (request): Promise<CouponPreview> => {
  const access = await requirePartnerCabinet(request);
  const code = requireString(request.data, 'code').trim();
  const couponId = requestedCouponId(request.data);
  const found = await findCoupon(directReader, access, couponId, code);
  if (!found) {
    return {
      couponId: couponId ?? '',
      state: 'notFound',
      rewardId: '',
      rewardTitle: '',
      partnerName: '',
      code,
      redeemedAtEpochMillis: null,
      expiresAtEpochMillis: null,
      usedAtEpochMillis: null,
    };
  }
  const { coupon, reward, partnerId } = found;
  const partner = partnerId ? await db.doc(paths.partner(partnerId)).get() : undefined;
  return {
    couponId: coupon.id,
    state: couponState(coupon.data(), code, Date.now()),
    rewardId: String(coupon.get('rewardId')),
    rewardTitle: (reward.get('title') as string | undefined) ?? '',
    partnerName: (partner?.get('name') as string | undefined) ?? (reward.get('partnerName') as string | undefined) ?? '',
    code,
    redeemedAtEpochMillis: numberOrNull(coupon.get('redeemedAtEpochMillis')),
    expiresAtEpochMillis: numberOrNull(coupon.get('expiresAtEpochMillis')),
    usedAtEpochMillis: numberOrNull(coupon.get('usedAtEpochMillis')),
  };
});

export const partnerRedeemCoupon = onCall(
  { region: REGION },
  async (request): Promise<{ couponId: string; usedAtEpochMillis: number }> => {
    const access = await requirePartnerCabinet(request);
    const code = requireString(request.data, 'code').trim();
    const couponId = requestedCouponId(request.data);
    return db.runTransaction(async (tx) => {
      const found = await findCoupon(transactionReader(tx), access, couponId, code);
      if (!found) throw new HttpsError('not-found', 'coupon_not_found');
      const { coupon, reward, partnerId } = found;
      const usedAtEpochMillis = Date.now();
      const state = couponState(coupon.data(), code, usedAtEpochMillis);
      if (state === 'used') throw new HttpsError('already-exists', 'coupon_used');
      if (state === 'expired') throw new HttpsError('failed-precondition', 'coupon_expired');
      tx.update(coupon.ref, {
        status: COUPON_STATUS_USED,
        usedAtEpochMillis,
        redeemedByUid: access.uid,
        redeemedByPartnerId: access.partnerId,
        ...(partnerId && !coupon.get('partnerId') ? { partnerId } : {}),
      });
      if (reward.exists) tx.update(reward.ref, { usedCount: FieldValue.increment(1) });
      return { couponId: coupon.id, usedAtEpochMillis };
    });
  },
);

export const partnerListRedemptions = onCall({ region: REGION }, async (request): Promise<Redemption[]> => {
  const access = await requirePartnerCabinet(request);
  const requested = (request.data as { partnerId?: unknown } | null)?.partnerId;
  const partnerId = access.role === 'SUPER_ADMIN' && typeof requested === 'string' && requested ? requested : access.partnerId;
  if (!partnerId) throw new HttpsError('invalid-argument', 'partnerId is required');

  const purchases = await db.collection(paths.purchases())
    .where('partnerId', '==', partnerId)
    .where('status', '==', COUPON_STATUS_USED)
    .orderBy('usedAtEpochMillis', 'desc')
    .limit(PARTNER_HISTORY_LIMIT)
    .get();
  const rewardIds = [...new Set(purchases.docs.map((purchase) => String(purchase.get('rewardId'))))];
  const rewards = rewardIds.length > 0 ? await db.getAll(...rewardIds.map((id) => db.doc(paths.shopItem(id)))) : [];
  const titles = new Map(rewards.map((reward) => [reward.id, (reward.get('title') as string | undefined) ?? reward.id]));
  return purchases.docs.map((purchase) => ({
    couponId: purchase.id,
    rewardId: String(purchase.get('rewardId')),
    rewardTitle: titles.get(String(purchase.get('rewardId'))) ?? '',
    code: String(purchase.get('code') ?? ''),
    usedAtEpochMillis: numberOrNull(purchase.get('usedAtEpochMillis')),
    isSelfMarked: purchase.get('redeemedByUid') === purchase.get('userId'),
  }));
});
