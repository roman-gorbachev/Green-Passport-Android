const fs = require('fs');
const path = require('path');
const { after, before, beforeEach, test } = require('node:test');
const { assertFails, assertSucceeds, initializeTestEnvironment } = require('@firebase/rules-unit-testing');
const { collection, doc, getDoc, getDocs, query, setDoc, updateDoc, where } = require('firebase/firestore');
const { ref, uploadBytes, getBytes } = require('firebase/storage');

const ROOT = 'apps/greenpassport';
const ALICE = 'alice';
const BOB = 'bob';
const ADMIN = 'moderator';

let env;

before(async () => {
  env = await initializeTestEnvironment({
    projectId: 'demo-greenpassport',
    firestore: {
      rules: fs.readFileSync(path.join(__dirname, '..', 'firestore.rules'), 'utf8'),
      host: '127.0.0.1',
      port: 8080,
    },
    storage: {
      rules: fs.readFileSync(path.join(__dirname, '..', 'storage.rules'), 'utf8'),
      host: '127.0.0.1',
      port: 9199,
    },
  });
});

beforeEach(async () => {
  await env.clearFirestore();
  await env.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await setDoc(doc(db, `${ROOT}/users/${ALICE}`), { availablePoints: 100, lifetimeXp: 100, firstName: 'Алиса' });
    await setDoc(doc(db, `${ROOT}/admins/${ADMIN}`), { grantedAt: 1 });
    await setDoc(doc(db, `${ROOT}/tasks/photoTask`), { title: 'Уборка', verification: 'PHOTO', rewardPoints: 100 });
    await setDoc(doc(db, `${ROOT}/tasks/selfTask`), { title: 'Сумка', verification: 'SELF', rewardPoints: 20 });
    await setDoc(doc(db, `${ROOT}/taskSecrets/qrTask`), { code: 'secret' });
    await setDoc(doc(db, `${ROOT}/posts/post1`), { authorId: BOB, text: 'Привет', createdAtEpochMillis: 1 });
    await setDoc(doc(db, `${ROOT}/groups/group1`), { name: 'Эко', memberIds: [BOB] });
    await setDoc(doc(db, `${ROOT}/taskProgress/${ALICE}_selfTask`), { userId: ALICE, taskId: 'selfTask' });
  });
});

after(async () => {
  await env.cleanup();
});

const firestoreOf = (uid) => env.authenticatedContext(uid).firestore();

test('user cannot change own points but can edit profile', async () => {
  const db = firestoreOf(ALICE);
  await assertFails(updateDoc(doc(db, `${ROOT}/users/${ALICE}`), { availablePoints: 100000 }));
  await assertFails(updateDoc(doc(db, `${ROOT}/users/${ALICE}`), { lifetimeXp: 5000 }));
  await assertSucceeds(updateDoc(doc(db, `${ROOT}/users/${ALICE}`), { firstName: 'Алиса', city: 'Минск' }));
});

test('new user document cannot start with points', async () => {
  const db = firestoreOf(BOB);
  await assertFails(setDoc(doc(db, `${ROOT}/users/${BOB}`), { availablePoints: 500 }));
  await assertSucceeds(setDoc(doc(db, `${ROOT}/users/${BOB}`), { firstName: 'Боб' }));
});

test('profile names with obscene words are rejected', async () => {
  const db = firestoreOf(ALICE);
  await assertFails(updateDoc(doc(db, `${ROOT}/users/${ALICE}`), { firstName: 'Хуй' }));
});

test('clients cannot mark tasks completed or read QR secrets', async () => {
  const db = firestoreOf(ALICE);
  await assertFails(setDoc(doc(db, `${ROOT}/taskProgress/${ALICE}_selfTask`), { userId: ALICE, taskId: 'selfTask' }));
  await assertFails(getDoc(doc(db, `${ROOT}/taskSecrets/qrTask`)));
});

test('photo submission is allowed only for PHOTO tasks and as PENDING', async () => {
  const db = firestoreOf(ALICE);
  const valid = {
    userId: ALICE,
    taskId: 'photoTask',
    userName: 'Алиса',
    photoPath: `greenpassport/submissions/${ALICE}/abc-123.jpg`,
    status: 'PENDING',
    createdAtEpochMillis: 1,
  };
  await assertSucceeds(setDoc(doc(db, `${ROOT}/taskSubmissions/${ALICE}_photoTask`), valid));
  await assertFails(setDoc(doc(db, `${ROOT}/taskSubmissions/${ALICE}_selfTask`), { ...valid, taskId: 'selfTask' }));
  await assertFails(setDoc(doc(db, `${ROOT}/taskSubmissions/${BOB}_photoTask`), { ...valid, userId: BOB }));
  await assertFails(updateDoc(doc(db, `${ROOT}/taskSubmissions/${ALICE}_photoTask`), { status: 'APPROVED' }));
  await assertFails(getDoc(doc(firestoreOf(BOB), `${ROOT}/taskSubmissions/${ALICE}_photoTask`)));
  await assertSucceeds(getDoc(doc(firestoreOf(ADMIN), `${ROOT}/taskSubmissions/${ALICE}_photoTask`)));
});

test('reports: one per user, readable only by moderators', async () => {
  const db = firestoreOf(ALICE);
  const report = { postId: 'post1', reporterId: ALICE, reason: 'SPAM', createdAtEpochMillis: 1 };
  await assertSucceeds(setDoc(doc(db, `${ROOT}/reports/post1_${ALICE}`), report));
  await assertFails(setDoc(doc(db, `${ROOT}/reports/post1_${BOB}`), report));
  await assertFails(getDoc(doc(db, `${ROOT}/reports/post1_${ALICE}`)));
  await assertSucceeds(getDoc(doc(firestoreOf(ADMIN), `${ROOT}/reports/post1_${ALICE}`)));
});

test('forum posts and group names with obscene words are rejected', async () => {
  const db = firestoreOf(ALICE);
  const post = { authorId: ALICE, text: 'Всем привет, идём на субботник', createdAtEpochMillis: 1 };
  await assertSucceeds(setDoc(doc(db, `${ROOT}/posts/clean`), post));
  await assertFails(setDoc(doc(db, `${ROOT}/posts/dirty`), { ...post, text: 'ну ты и пиздюк' }));
  await assertFails(setDoc(doc(db, `${ROOT}/groups/dirty`), { name: 'Бляди', memberIds: [ALICE] }));
});

test('group update may only change members', async () => {
  const db = firestoreOf(ALICE);
  await assertSucceeds(updateDoc(doc(db, `${ROOT}/groups/group1`), { memberIds: [BOB, ALICE] }));
  await assertFails(updateDoc(doc(db, `${ROOT}/groups/group1`), { name: 'Захвачено' }));
});

test('moderator role document is readable only by its owner', async () => {
  await assertSucceeds(getDoc(doc(firestoreOf(ADMIN), `${ROOT}/admins/${ADMIN}`)));
  await assertFails(getDoc(doc(firestoreOf(ALICE), `${ROOT}/admins/${ADMIN}`)));
});

test('storage: submission photos are private to the owner and moderators', async () => {
  const photo = new Uint8Array([0xff, 0xd8, 0xff]);
  const path = `greenpassport/submissions/${ALICE}/abc.jpg`;
  await assertSucceeds(
    uploadBytes(ref(env.authenticatedContext(ALICE).storage(), path), photo, { contentType: 'image/jpeg' }),
  );
  await assertFails(
    uploadBytes(ref(env.authenticatedContext(BOB).storage(), path), photo, { contentType: 'image/jpeg' }),
  );
  await assertFails(
    uploadBytes(
      ref(env.authenticatedContext(ALICE).storage(), `greenpassport/submissions/${ALICE}/x.png`),
      photo,
      { contentType: 'image/png' },
    ),
  );
  await assertFails(getBytes(ref(env.authenticatedContext(BOB).storage(), path)));
});

test('users can query only their own progress, everyone reads the forum', async () => {
  const progress = collection(firestoreOf(ALICE), `${ROOT}/taskProgress`);
  await assertSucceeds(getDocs(query(progress, where('userId', '==', ALICE))));
  await assertFails(getDocs(progress));
  await assertFails(getDocs(query(collection(firestoreOf(BOB), `${ROOT}/taskProgress`), where('userId', '==', ALICE))));
  await assertSucceeds(getDocs(collection(firestoreOf(BOB), `${ROOT}/posts`)));
  await assertSucceeds(getDocs(collection(firestoreOf(BOB), `${ROOT}/tasks`)));
});
