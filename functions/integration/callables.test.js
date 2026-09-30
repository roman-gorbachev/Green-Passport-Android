const assert = require('node:assert/strict');
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

async function signUp() {
  const response = await fetch(AUTH_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ returnSecureToken: true }),
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
  return body.error ? { error: body.error.status } : { result: body.result };
}

async function points(uid) {
  const snapshot = await db.doc(`${ROOT}/users/${uid}`).get();
  return snapshot.get('availablePoints') ?? 0;
}

let alice;
let moderator;

before(async () => {
  alice = await signUp();
  moderator = await signUp();
  await db.doc(`${ROOT}/admins/${moderator.uid}`).set({ grantedAt: 1 });
  for (const id of ['self1', 'self2', 'self3', 'self4']) {
    await db.doc(`${ROOT}/tasks/${id}`).set({ verification: 'SELF', rewardPoints: 20, rewardXp: 20 });
  }
  await db.doc(`${ROOT}/tasks/photo1`).set({ verification: 'PHOTO', rewardPoints: 100, rewardXp: 100 });
  await db.doc(`${ROOT}/tasks/qr1`).set({ verification: 'QR', rewardPoints: 70, rewardXp: 70 });
  await db.doc(`${ROOT}/taskSecrets/qr1`).set({ code: 'secret' });
  await db.doc(`${ROOT}/ecoTips/tip1`).set({ rewardPoints: 15, rewardXp: 15 });
  await db.doc(`${ROOT}/shopItems/coffee`).set({ pointsCost: 100, validityDays: 14 });
});

test('self tasks: awarded once each and at most three per day', async () => {
  assert.deepEqual((await call('completeSelfTask', alice, { taskId: 'self1' })).result, { points: 20, xp: 20, taskId: 'self1' });
  assert.equal((await call('completeSelfTask', alice, { taskId: 'self1' })).error, 'ALREADY_EXISTS');
  await call('completeSelfTask', alice, { taskId: 'self2' });
  await call('completeSelfTask', alice, { taskId: 'self3' });
  assert.equal((await call('completeSelfTask', alice, { taskId: 'self4' })).error, 'RESOURCE_EXHAUSTED');
  assert.equal((await call('completeSelfTask', alice, { taskId: 'photo1' })).error, 'FAILED_PRECONDITION');
  assert.equal(await points(alice.uid), 60);
});

test('QR codes: wrong code rejected, right code awarded once', async () => {
  assert.equal((await call('redeemTaskCode', alice, { code: 'greenpassport:task:qr1:wrong' })).error, 'NOT_FOUND');
  assert.equal((await call('redeemTaskCode', alice, { code: 'greenpassport:task:qr1:secret' })).result.points, 70);
  assert.equal((await call('redeemTaskCode', alice, { code: 'greenpassport:task:qr1:secret' })).error, 'ALREADY_EXISTS');
  assert.equal(await points(alice.uid), 130);
});

test('tips and games: tips once, game points capped', async () => {
  assert.equal((await call('recordTipRead', alice, { tipId: 'tip1' })).result.points, 15);
  assert.equal((await call('recordTipRead', alice, { tipId: 'tip1' })).result.points, 0);
  assert.equal((await call('recordGameResult', alice, { gameId: 'eco_quiz', score: 1000 })).result.points, 30);
  assert.equal(await points(alice.uid), 175);
});

test('shop: spends points on the server and refuses when short', async () => {
  const purchase = await call('redeemReward', alice, { rewardId: 'coffee' });
  assert.ok(purchase.result.couponId);
  assert.match(purchase.result.code, /^[A-HJKMNP-Z2-9]{8}$/);
  const validityMillis = purchase.result.expiresAtEpochMillis - purchase.result.redeemedAtEpochMillis;
  assert.equal(validityMillis, 14 * 24 * 60 * 60 * 1000);
  assert.equal(await points(alice.uid), 75);
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
  assert.equal(await points(alice.uid), 175);
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
