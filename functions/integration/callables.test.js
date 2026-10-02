const assert = require('node:assert/strict');
const { createHmac } = require('node:crypto');
const { before, test } = require('node:test');
const { initializeApp } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');

const PROJECT_ID = process.env.GCLOUD_PROJECT || 'demo-greenpassport';
const REGION = 'europe-central2';
const FUNCTIONS_URL = `http://127.0.0.1:5001/${PROJECT_ID}/${REGION}`;
const AUTH_URL = 'http://127.0.0.1:9099/identitytoolkit.googleapis.com/v1/accounts:signUp?key=fake-key';
const ROOT = 'apps/greenpassport';

initializeApp({ projectId: PROJECT_ID });
const db = getFirestore();

async function signUp(email) {
  const response = await fetch(AUTH_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(email ? { email, password: 'secret123', returnSecureToken: true } : { returnSecureToken: true }),
  });
  const body = await response.json();
  return { uid: body.localId, token: body.idToken };
}

async function call(name, user, data) {
  const response = await fetch(`${FUNCTIONS_URL}/${name}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${user.token}` },
    body: JSON.stringify({ data }),
  });
  const body = await response.json();
  return body.error ? { error: body.error.status, message: body.error.message } : { result: body.result };
}

async function points(uid) {
  const snapshot = await db.doc(`${ROOT}/users/${uid}`).get();
  return snapshot.get('availablePoints') ?? 0;
}

let alice;
let bob;
let moderator;

before(async () => {
  alice = await signUp();
  bob = await signUp();
  moderator = await signUp();
  await db.doc(`${ROOT}/admins/${moderator.uid}`).set({ grantedAt: 1 });
  for (const id of ['self1', 'self2', 'self3', 'self4']) {
    await db.doc(`${ROOT}/tasks/${id}`).set({ verification: 'SELF', rewardPoints: 20, rewardXp: 20 });
  }
  await db.doc(`${ROOT}/tasks/photo1`).set({ verification: 'PHOTO', rewardPoints: 100, rewardXp: 100 });
  await db.doc(`${ROOT}/tasks/qr1`).set({ verification: 'QR', rewardPoints: 70, rewardXp: 70 });
  await db.doc(`${ROOT}/taskSecrets/qr1`).set({ code: 'secret' });
  await db.doc(`${ROOT}/ecoTips/tip1`).set({ rewardPoints: 15, rewardXp: 15 });
  await db.doc(`${ROOT}/events/soon`).set({ title: 'Soon', startAtEpochMillis: Date.now() + 60 * 60 * 1000, rewardPoints: 40 });
  await db.doc(`${ROOT}/eventSecrets/soon`).set({ code: 'secret' });
  await db.doc(`${ROOT}/events/later`).set({ title: 'Later', startAtEpochMillis: Date.now() + 5 * 24 * 60 * 60 * 1000, rewardPoints: 40 });
  await db.doc(`${ROOT}/eventSecrets/later`).set({ code: 'secret' });
  await db.doc(`${ROOT}/games/water_saver`).set({ path: 'water_saver/index.html', maxPoints: 20, isActive: true });
  await db.doc(`${ROOT}/games/retired`).set({ path: 'retired/index.html', maxPoints: 30, isActive: false });
  await db.doc(`${ROOT}/surveys/s1`).set({ question: 'Q', options: ['A', 'B'], isActive: true });
  await db.doc(`${ROOT}/shopItems/coffee`).set({ pointsCost: 100, validityDays: 14 });
});

test('self tasks: awarded once each and at most three per day', async () => {
  assert.deepEqual((await call('completeSelfTask', alice, { taskId: 'self1' })).result, { points: 20, xp: 20, taskId: 'self1', streakBonus: 5 });
  assert.equal((await call('completeSelfTask', alice, { taskId: 'self1' })).error, 'ALREADY_EXISTS');
  await call('completeSelfTask', alice, { taskId: 'self2' });
  await call('completeSelfTask', alice, { taskId: 'self3' });
  assert.equal((await call('completeSelfTask', alice, { taskId: 'self4' })).error, 'RESOURCE_EXHAUSTED');
  assert.equal((await call('completeSelfTask', alice, { taskId: 'photo1' })).error, 'FAILED_PRECONDITION');
  assert.equal(await points(alice.uid), 65);
});

test('QR codes: wrong code rejected, right code awarded once', async () => {
  assert.equal((await call('redeemTaskCode', alice, { code: 'greenpassport:task:qr1:wrong' })).error, 'NOT_FOUND');
  const qr = (await call('redeemTaskCode', alice, { code: 'greenpassport:task:qr1:secret' })).result;
  assert.equal(qr.points, 70);
  assert.equal(qr.streakBonus, 0);
  assert.equal((await call('redeemTaskCode', alice, { code: 'greenpassport:task:qr1:secret' })).error, 'ALREADY_EXISTS');
  assert.equal(await points(alice.uid), 135);
});

test('tips and games: tips once, game points capped', async () => {
  assert.equal((await call('recordTipRead', alice, { tipId: 'tip1' })).result.points, 15);
  assert.equal((await call('recordTipRead', alice, { tipId: 'tip1' })).result.points, 0);
  assert.equal((await call('recordGameResult', alice, { gameId: 'eco_quiz', score: 1000 })).result.points, 30);
  assert.equal(await points(alice.uid), 180);
});

test('shop: spends points on the server and refuses when short', async () => {
  const purchase = await call('redeemReward', alice, { rewardId: 'coffee' });
  assert.ok(purchase.result.couponId);
  assert.match(purchase.result.code, /^[A-HJKMNP-Z2-9]{8}$/);
  const validityMillis = purchase.result.expiresAtEpochMillis - purchase.result.redeemedAtEpochMillis;
  assert.equal(validityMillis, 14 * 24 * 60 * 60 * 1000);
  assert.equal(await points(alice.uid), 80);
  assert.equal((await call('redeemReward', alice, { rewardId: 'coffee' })).error, 'FAILED_PRECONDITION');

  const couponId = purchase.result.couponId;
  assert.equal((await call('markCouponUsed', moderator, { couponId })).error, 'NOT_FOUND');
  assert.ok((await call('markCouponUsed', alice, { couponId })).result.usedAtEpochMillis);
  assert.equal((await call('markCouponUsed', alice, { couponId })).error, 'ALREADY_EXISTS');
  const coupon = await db.doc(`${ROOT}/purchases/${couponId}`).get();
  assert.equal(coupon.get('status'), 'USED');
});

test('shop: expired coupons cannot be marked as used', async () => {
  const expired = await db.collection(`${ROOT}/purchases`).add({
    userId: alice.uid,
    rewardId: 'coffee',
    redeemedAtEpochMillis: 1,
    expiresAtEpochMillis: 2,
    status: 'ACTIVE',
  });
  assert.equal((await call('markCouponUsed', alice, { couponId: expired.id })).error, 'FAILED_PRECONDITION');
});

test('shop: the coupon link now leads to the partner cabinet and redeems nothing', async () => {
  const active = await db.collection(`${ROOT}/purchases`).add({
    userId: alice.uid,
    rewardId: 'coffee',
    redeemedAtEpochMillis: 1,
    expiresAtEpochMillis: Date.now() + 60 * 60 * 1000,
    code: 'SCAN2345',
    status: 'ACTIVE',
  });
  const response = await fetch(`${FUNCTIONS_URL}/scanCoupon?id=${active.id}&code=SCAN2345`, { redirect: 'manual' });
  assert.equal(response.status, 302);
  assert.equal(response.headers.get('location'), `https://greenpassport-admin.web.app/redeem?id=${active.id}&code=SCAN2345`);
  assert.equal((await db.doc(`${ROOT}/purchases/${active.id}`).get()).get('status'), 'ACTIVE');
});

test('photo review: only moderators, approval awards points', async () => {
  const submissionId = `${alice.uid}_photo1`;
  await db.doc(`${ROOT}/taskSubmissions/${submissionId}`).set({
    userId: alice.uid,
    taskId: 'photo1',
    status: 'PENDING',
    photoPath: `greenpassport/submissions/${alice.uid}/a.jpg`,
    createdAtEpochMillis: 1,
  });
  assert.equal((await call('reviewSubmission', alice, { submissionId, approve: true })).error, 'PERMISSION_DENIED');
  assert.equal((await call('reviewSubmission', moderator, { submissionId, approve: true })).result.status, 'APPROVED');
  assert.equal(await points(alice.uid), 180);
  const progress = await db.doc(`${ROOT}/taskProgress/${alice.uid}_photo1`).get();
  assert.equal(progress.exists, true);
});

test('reports: third report hides the post, moderator restores it', async () => {
  await db.doc(`${ROOT}/posts/post1`).set({ authorId: 'x', text: 'Привет', createdAtEpochMillis: 1 });
  for (const reporter of ['r1', 'r2', 'r3']) {
    await db.doc(`${ROOT}/reports/post1_${reporter}`).set({ postId: 'post1', reporterId: reporter, reason: 'SPAM' });
  }
  await new Promise((resolve) => setTimeout(resolve, 3000));
  assert.equal((await db.doc(`${ROOT}/posts/post1`).get()).get('hidden'), true);
  assert.equal((await call('moderateContent', moderator, { postId: 'post1', action: 'restore' })).result.action, 'restore');
  assert.equal((await db.doc(`${ROOT}/posts/post1`).get()).get('hidden'), false);
});

test('server hides forum posts with obscene words', async () => {
  await db.doc(`${ROOT}/posts/dirty`).set({ authorId: 'x', text: 'х у й', createdAtEpochMillis: 1 });
  await new Promise((resolve) => setTimeout(resolve, 3000));
  assert.equal((await db.doc(`${ROOT}/posts/dirty`).get()).get('hidden'), true);
});

test('events: check-in works once inside the time window and extends the streak', async () => {
  assert.equal((await call('checkInEvent', bob, { code: 'greenpassport:event:soon:wrong' })).error, 'NOT_FOUND');
  assert.equal((await call('checkInEvent', bob, { code: 'greenpassport:event:later:secret' })).error, 'FAILED_PRECONDITION');
  assert.deepEqual((await call('checkInEvent', bob, { code: 'greenpassport:event:soon:secret' })).result, {
    points: 40,
    xp: 40,
    eventId: 'soon',
    streakBonus: 5,
  });
  assert.equal((await call('checkInEvent', bob, { code: 'greenpassport:event:soon:secret' })).error, 'ALREADY_EXISTS');
  assert.equal((await db.doc(`${ROOT}/eventRegistrations/${bob.uid}_soon`).get()).exists, true);
  assert.equal(await points(bob.uid), 45);
});

test('feedback: rewarded once a week, obscene text rejected', async () => {
  assert.equal((await call('submitFeedback', bob, { type: 'REVIEW', message: '' })).error, 'INVALID_ARGUMENT');
  assert.equal((await call('submitFeedback', bob, { type: 'SUGGESTION', message: 'fuck' })).error, 'INVALID_ARGUMENT');
  assert.equal((await call('submitFeedback', bob, { type: 'REVIEW', message: 'Отлично', rating: 5 })).result.points, 10);
  assert.equal((await call('submitFeedback', bob, { type: 'SUGGESTION', message: 'Добавьте карту' })).result.points, 0);
  assert.equal(await points(bob.uid), 55);
});

test('surveys: rewarded once, unknown option rejected', async () => {
  assert.equal((await call('submitSurveyAnswer', bob, { surveyId: 's1', optionIndex: 7 })).error, 'INVALID_ARGUMENT');
  assert.equal((await call('submitSurveyAnswer', bob, { surveyId: 's1', optionIndex: 1 })).result.points, 10);
  assert.equal((await call('submitSurveyAnswer', bob, { surveyId: 's1', optionIndex: 0 })).error, 'ALREADY_EXISTS');
  assert.equal(await points(bob.uid), 65);
});

test('profile: completing the profile awards the bonus exactly once', async () => {
  const carol = await signUp();
  await db.doc(`${ROOT}/users/${carol.uid}`).set({ firstName: 'Carol', profileCompletedAt: Date.now() }, { merge: true });
  await new Promise((resolve) => setTimeout(resolve, 3000));
  await db.doc(`${ROOT}/users/${carol.uid}`).set({ city: 'Минск' }, { merge: true });
  await new Promise((resolve) => setTimeout(resolve, 3000));
  assert.equal(await points(carol.uid), 50);
});

test('games: catalog games use their own point cap, inactive and unknown games are rejected', async () => {
  assert.equal((await call('recordGameResult', bob, { gameId: 'water_saver', score: 500 })).result.points, 20);
  assert.equal((await call('recordGameResult', bob, { gameId: 'retired', score: 10 })).error, 'INVALID_ARGUMENT');
  assert.equal((await call('recordGameResult', bob, { gameId: 'nope', score: 10 })).error, 'INVALID_ARGUMENT');
});

const DYNAMIC_WINDOW_MILLIS = 30_000;

function dynamicCode(eventId, key, window) {
  const signature = createHmac('sha256', Buffer.from(key, 'hex')).update(`${eventId}:${window}`).digest('hex').slice(0, 12);
  return `greenpassport:event:${eventId}:d${window}.${signature}`;
}

let superAdmin;
let editor;
let cashier;
let otherCashier;

test('staff: the super admin grants roles by e-mail and cannot demote themselves', async () => {
  superAdmin = await signUp('boss@example.com');
  editor = await signUp('editor@example.com');
  cashier = await signUp('cashier@example.com');
  otherCashier = await signUp('other@example.com');
  await db.doc(`${ROOT}/admins/${superAdmin.uid}`).set({ role: 'SUPER_ADMIN' });
  await db.doc(`${ROOT}/partners/cafe`).set({ name: 'Кафе', isActive: true });
  await db.doc(`${ROOT}/partners/bakery`).set({ name: 'Пекарня', isActive: true });

  assert.equal((await call('adminSetStaffRole', moderator, { email: 'editor@example.com', role: 'EDITOR' })).error, 'PERMISSION_DENIED');
  assert.equal((await call('adminSetStaffRole', superAdmin, { email: 'nobody@example.com', role: 'EDITOR' })).message, 'user_not_found');
  assert.deepEqual((await call('adminSetStaffRole', superAdmin, { email: 'editor@example.com', role: 'EDITOR' })).result, {
    uid: editor.uid,
    email: 'editor@example.com',
    role: 'EDITOR',
    partnerId: null,
  });
  assert.equal((await call('adminSetStaffRole', superAdmin, { email: 'boss@example.com', role: null })).message, 'cannot_demote_self');
  assert.equal((await call('adminSetPartnerUser', superAdmin, { email: 'cashier@example.com', partnerId: 'nope' })).message, 'partner_not_found');
  assert.equal((await call('adminSetPartnerUser', superAdmin, { email: 'cashier@example.com', partnerId: 'cafe' })).result.partnerId, 'cafe');
  await call('adminSetPartnerUser', superAdmin, { email: 'other@example.com', partnerId: 'bakery' });

  const staff = (await call('adminListStaff', editor, {})).result;
  assert.equal(staff.find((member) => member.uid === cashier.uid).email, 'cashier@example.com');
  assert.equal(staff.find((member) => member.uid === moderator.uid).role, 'MODERATOR');
  assert.equal((await call('adminListStaff', moderator, {})).error, 'PERMISSION_DENIED');
});

test('task QR: window and limit are enforced and rotation retires the old code', async () => {
  await db.doc(`${ROOT}/tasks/qrLimited`).set({ verification: 'QR', rewardPoints: 10, rewardXp: 10, qrScanLimit: 2 });
  await db.doc(`${ROOT}/tasks/qrLater`).set({ verification: 'QR', rewardPoints: 10, qrActiveFromEpochMillis: Date.now() + 60_000 });
  await db.doc(`${ROOT}/tasks/selfOnly`).set({ verification: 'SELF', rewardPoints: 10 });
  await db.doc(`${ROOT}/taskSecrets/selfOnly`).set({ code: 'secret' });

  assert.equal((await call('adminEnsureQrSecret', moderator, { kind: 'task', id: 'qrLimited' })).error, 'PERMISSION_DENIED');
  assert.equal((await call('adminEnsureQrSecret', editor, { kind: 'task', id: 'missing' })).message, 'content_not_found');
  const first = (await call('adminEnsureQrSecret', editor, { kind: 'task', id: 'qrLimited' })).result;
  assert.equal(first.version, 1);
  assert.deepEqual((await call('adminEnsureQrSecret', editor, { kind: 'task', id: 'qrLimited' })).result, first);
  const later = (await call('adminEnsureQrSecret', editor, { kind: 'task', id: 'qrLater' })).result;

  const carol = await signUp();
  assert.equal((await call('redeemTaskCode', alice, { code: first.payload })).result.points, 10);
  assert.equal((await call('redeemTaskCode', bob, { code: first.payload })).result.points, 10);
  assert.equal((await call('redeemTaskCode', carol, { code: first.payload })).message, 'qr_limit_reached');
  assert.equal((await call('redeemTaskCode', carol, { code: later.payload })).message, 'qr_not_active');
  assert.equal((await call('redeemTaskCode', carol, { code: 'greenpassport:task:selfOnly:secret' })).error, 'NOT_FOUND');

  const rotated = (await call('adminRotateQrSecret', editor, { kind: 'task', id: 'qrLimited' })).result;
  assert.equal(rotated.version, 2);
  assert.equal(rotated.scanCount, 0);
  assert.equal((await call('redeemTaskCode', carol, { code: first.payload })).error, 'NOT_FOUND');
  assert.equal((await call('redeemTaskCode', carol, { code: rotated.payload })).result.points, 10);

  const batch = (await call('adminGetQrPayloads', editor, { kind: 'task', ids: ['qrLimited', 'qrLater', 'missing'] })).result;
  assert.deepEqual(batch.map((payload) => payload.id).sort(), ['qrLater', 'qrLimited']);
  assert.equal(batch.find((payload) => payload.id === 'qrLimited').scanCount, 1);
});

test('dynamic event QR: the live code works, old and static codes do not', async () => {
  await db.doc(`${ROOT}/events/live`).set({ title: 'Live', startAtEpochMillis: Date.now(), rewardPoints: 25, qrMode: 'DYNAMIC' });
  const { key } = (await call('adminGetEventDynamicKey', editor, { eventId: 'live' })).result;
  assert.match(key, /^[0-9a-f]{64}$/);
  assert.equal((await call('adminGetEventDynamicKey', editor, { eventId: 'live' })).result.key, key);
  const staticCode = (await call('adminEnsureQrSecret', editor, { kind: 'event', id: 'live' })).result.payload;

  const window = Math.floor(Date.now() / DYNAMIC_WINDOW_MILLIS);
  const carol = await signUp();
  assert.equal((await call('checkInEvent', carol, { code: staticCode })).error, 'NOT_FOUND');
  assert.equal((await call('checkInEvent', carol, { code: dynamicCode('live', key, window - 2) })).error, 'NOT_FOUND');
  assert.equal((await call('checkInEvent', carol, { code: dynamicCode('soon', key, window) })).error, 'NOT_FOUND');
  assert.equal((await call('checkInEvent', carol, { code: dynamicCode('live', key, window) })).result.points, 25);
});

test('code pool and stock: pool codes are issued until they run out', async () => {
  await db.doc(`${ROOT}/shopItems/pastry`).set({ title: 'Круассан', pointsCost: 0, partnerId: 'cafe', codeSource: 'POOL' });
  await db.doc(`${ROOT}/shopItems/limited`).set({ title: 'Скидка', pointsCost: 0, partnerId: 'bakery', stockLimit: 1 });
  await db.doc(`${ROOT}/shopItems/hidden`).set({ title: 'Архив', pointsCost: 0, isActive: false });

  assert.equal((await call('adminImportCouponCodes', otherCashier, { rewardId: 'pastry', codes: ['X'] })).error, 'PERMISSION_DENIED');
  assert.deepEqual((await call('adminImportCouponCodes', cashier, { rewardId: 'pastry', codes: ['P-1', 'P-2', 'P-1', 'bad code', ''] })).result, {
    added: 2,
    duplicates: 1,
    invalid: 2,
  });
  assert.deepEqual((await call('adminImportCouponCodes', editor, { rewardId: 'pastry', codes: ['P-2', 'P-3'] })).result, {
    added: 1,
    duplicates: 1,
    invalid: 0,
  });
  assert.equal((await db.doc(`${ROOT}/shopItems/pastry`).get()).get('poolAvailableCount'), 3);

  const codes = [];
  for (let index = 0; index < 3; index += 1) codes.push((await call('redeemReward', bob, { rewardId: 'pastry' })).result.code);
  assert.deepEqual(codes.sort(), ['P-1', 'P-2', 'P-3']);
  assert.equal((await call('redeemReward', bob, { rewardId: 'pastry' })).message, 'reward_sold_out');
  const pastry = await db.doc(`${ROOT}/shopItems/pastry`).get();
  assert.equal(pastry.get('issuedCount'), 3);
  assert.equal(pastry.get('poolAvailableCount'), 0);

  assert.ok((await call('redeemReward', bob, { rewardId: 'limited' })).result.couponId);
  assert.equal((await call('redeemReward', bob, { rewardId: 'limited' })).message, 'reward_sold_out');
  assert.equal((await call('redeemReward', bob, { rewardId: 'hidden' })).error, 'NOT_FOUND');
});

test('partner cabinet: previews and redeems only own coupons, history marks self-marked ones', async () => {
  await db.doc(`${ROOT}/shopItems/tea`).set({ title: 'Чай', pointsCost: 0, partnerId: 'cafe' });
  const purchase = (await call('redeemReward', alice, { rewardId: 'tea' })).result;
  const second = (await call('redeemReward', alice, { rewardId: 'tea' })).result;

  assert.equal((await call('partnerPreviewCoupon', alice, { code: purchase.code })).message, 'Partners only');
  assert.equal((await call('partnerPreviewCoupon', otherCashier, { couponId: purchase.couponId, code: purchase.code })).message, 'foreign_coupon');
  assert.equal((await call('partnerPreviewCoupon', cashier, { couponId: purchase.couponId, code: 'WRONG234' })).result.state, 'notFound');
  const preview = (await call('partnerPreviewCoupon', cashier, { code: purchase.code })).result;
  assert.equal(preview.state, 'active');
  assert.equal(preview.rewardTitle, 'Чай');
  assert.equal(preview.partnerName, 'Кафе');

  assert.equal((await call('partnerRedeemCoupon', otherCashier, { couponId: purchase.couponId, code: purchase.code })).message, 'foreign_coupon');
  assert.ok((await call('partnerRedeemCoupon', cashier, { couponId: purchase.couponId, code: purchase.code })).result.usedAtEpochMillis);
  assert.equal((await call('partnerRedeemCoupon', cashier, { couponId: purchase.couponId, code: purchase.code })).message, 'coupon_used');
  assert.equal((await call('partnerPreviewCoupon', cashier, { couponId: purchase.couponId, code: purchase.code })).result.state, 'used');
  assert.ok((await call('markCouponUsed', alice, { couponId: second.couponId })).result.usedAtEpochMillis);

  const tea = await db.doc(`${ROOT}/shopItems/tea`).get();
  assert.equal(tea.get('usedCount'), 2);
  assert.equal(tea.get('selfMarkedCount'), 1);
  const history = (await call('partnerListRedemptions', cashier, {})).result;
  assert.deepEqual(
    history.filter((item) => item.rewardId === 'tea').map((item) => [item.couponId, item.isSelfMarked]).sort(),
    [[purchase.couponId, false], [second.couponId, true]].sort(),
  );
  assert.equal((await call('partnerListRedemptions', superAdmin, { partnerId: 'cafe' })).result.length, history.length);
});

test('audit log: content writes are logged with their author, counters are not', async () => {
  await db.doc(`${ROOT}/tasks/audited`).set({ title: 'Было', updatedBy: editor.uid, updatedAtEpochMillis: 1 });
  await db.doc(`${ROOT}/tasks/audited`).set({ title: 'Стало', updatedBy: superAdmin.uid, updatedAtEpochMillis: 2 });
  await db.doc(`${ROOT}/shopItems/tea`).update({ issuedCount: 99 });
  await new Promise((resolve) => setTimeout(resolve, 3000));
  const entries = await db.collection(`${ROOT}/auditLog`).where('docId', '==', 'audited').get();
  const actions = entries.docs.map((entry) => [entry.get('action'), entry.get('by'), entry.get('changedFields')]);
  assert.deepEqual(actions.sort(), [['create', editor.uid, ['title']], ['update', superAdmin.uid, ['title']]].sort());
  assert.equal((await db.collection(`${ROOT}/auditLog`).where('docId', '==', 'tea').where('action', '==', 'update').get()).size, 0);
});
