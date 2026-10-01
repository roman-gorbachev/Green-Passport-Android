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

test('shop: partner scan redeems a coupon once and reports expired coupons', async () => {
  const active = await db.collection(`${ROOT}/purchases`).add({
    userId: alice.uid,
    rewardId: 'coffee',
    redeemedAtEpochMillis: 1,
    expiresAtEpochMillis: Date.now() + 60 * 60 * 1000,
    code: 'SCAN2345',
    status: 'ACTIVE',
  });
  const scan = (id, code) => fetch(`${FUNCTIONS_URL}/scanCoupon?id=${id}&code=${code}`);
  assert.equal((await scan(active.id, 'WRONG234')).status, 404);
  assert.equal((await scan(active.id, 'SCAN2345')).status, 200);
  assert.equal((await db.doc(`${ROOT}/purchases/${active.id}`).get()).get('status'), 'USED');
  assert.equal((await scan(active.id, 'SCAN2345')).status, 409);
  const expired = await db.collection(`${ROOT}/purchases`).add({
    userId: alice.uid,
    rewardId: 'coffee',
    redeemedAtEpochMillis: 1,
    expiresAtEpochMillis: 2,
    code: 'OLD23456',
    status: 'ACTIVE',
  });
  assert.equal((await scan(expired.id, 'OLD23456')).status, 410);
  assert.equal((await db.doc(`${ROOT}/purchases/${expired.id}`).get()).get('status'), 'EXPIRED');
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
