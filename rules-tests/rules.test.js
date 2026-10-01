const fs = require('fs');
const path = require('path');
const { after, before, beforeEach, test } = require('node:test');
const { assertFails, assertSucceeds, initializeTestEnvironment } = require('@firebase/rules-unit-testing');
const { arrayRemove, arrayUnion, collection, doc, getDoc, getDocs, query, setDoc, updateDoc, where } = require('firebase/firestore');
const { ref, uploadBytes, getBytes } = require('firebase/storage');

const ROOT = 'apps/greenpassport';
const ALICE = 'alice';
const BOB = 'bob';
const ADMIN = 'moderator';
const CAROL = 'carol';

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
    await setDoc(doc(db, `${ROOT}/groups/group1`), {
      name: 'Эко',
      memberIds: [BOB, CAROL],
      ownerId: BOB,
      createdAtEpochMillis: 1,
      inviteCode: 'ABC234',
    });
    await setDoc(doc(db, `${ROOT}/chats/group1/messages/m1`), { senderId: BOB, text: 'Привет', createdAtEpochMillis: 1 });
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
  await assertFails(setDoc(doc(db, `${ROOT}/groups/dirty`), newGroup(ALICE, { name: 'Бляди' })));
});

const newGroup = (ownerId, overrides = {}) => ({
  name: 'Велосипедисты',
  memberIds: [ownerId],
  ownerId,
  createdAtEpochMillis: 1,
  inviteCode: 'XYZ789',
  ...overrides,
});

test('group creator must be its owner and only member and give an invite code', async () => {
  const db = firestoreOf(ALICE);
  await assertSucceeds(setDoc(doc(db, `${ROOT}/groups/clean`), newGroup(ALICE)));
  await assertFails(setDoc(doc(db, `${ROOT}/groups/foreignOwner`), newGroup(ALICE, { ownerId: BOB })));
  await assertFails(setDoc(doc(db, `${ROOT}/groups/extraMember`), newGroup(ALICE, { memberIds: [ALICE, BOB] })));
  await assertFails(setDoc(doc(db, `${ROOT}/groups/noCode`), newGroup(ALICE, { inviteCode: null })));
  await assertFails(setDoc(doc(db, `${ROOT}/groups/badCode`), newGroup(ALICE, { inviteCode: 'abc' })));
  await assertFails(setDoc(doc(db, `${ROOT}/groups/legacy`), { name: 'Старая', memberIds: [ALICE] }));
});

test('group members may only add or remove themselves', async () => {
  const group = (uid) => doc(firestoreOf(uid), `${ROOT}/groups/group1`);
  await assertSucceeds(updateDoc(group(ALICE), { memberIds: arrayUnion(ALICE) }));
  await assertSucceeds(updateDoc(group(ALICE), { memberIds: arrayRemove(ALICE) }));
  await assertFails(updateDoc(group(ALICE), { memberIds: [] }));
  await assertFails(updateDoc(group(ALICE), { memberIds: arrayRemove(BOB) }));
  await assertFails(updateDoc(group(ALICE), { memberIds: [BOB, CAROL, ALICE, 'mallory'] }));
  await assertFails(updateDoc(group(CAROL), { memberIds: [CAROL] }));
  await assertFails(updateDoc(group(ALICE), { name: 'Захвачено' }));
  await assertFails(updateDoc(group(ALICE), { inviteCode: 'AAAAAA' }));
});

test('only group members read and write the group chat', async () => {
  const messages = (uid) => collection(firestoreOf(uid), `${ROOT}/chats/group1/messages`);
  const message = (senderId) => ({ senderId, senderName: 'Кэрол', senderAvatar: 'SKY', text: 'Привет', createdAtEpochMillis: 2 });
  await assertSucceeds(getDocs(messages(CAROL)));
  await assertSucceeds(setDoc(doc(messages(CAROL), 'm2'), message(CAROL)));
  await assertFails(setDoc(doc(messages(CAROL), 'm3'), message(BOB)));
  await assertFails(setDoc(doc(messages(CAROL), 'm4'), { ...message(CAROL), text: 'ну ты и пиздюк' }));
  await assertFails(getDocs(messages(ALICE)));
  await assertFails(setDoc(doc(messages(ALICE), 'm5'), message(ALICE)));
  await assertFails(updateDoc(doc(messages(BOB), 'm1'), { text: 'Изменено' }));
});

test('forum post with author name and avatar is accepted', async () => {
  const db = firestoreOf(ALICE);
  const post = { authorId: ALICE, authorName: 'Алиса Иванова', authorAvatar: 'LIME', text: 'Привет', createdAtEpochMillis: 1 };
  await assertSucceeds(setDoc(doc(db, `${ROOT}/posts/withAvatar`), post));
  await assertSucceeds(setDoc(doc(db, `${ROOT}/posts/anonymous`), { ...post, authorName: null, authorAvatar: null }));
  await assertFails(setDoc(doc(db, `${ROOT}/posts/emptyName`), { ...post, authorName: '' }));
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
