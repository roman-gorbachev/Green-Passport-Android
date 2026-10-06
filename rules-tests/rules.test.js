const fs = require('fs');
const path = require('path');
const { after, before, beforeEach, test } = require('node:test');
const { assertFails, assertSucceeds, initializeTestEnvironment } = require('@firebase/rules-unit-testing');
const {
  arrayRemove,
  arrayUnion,
  collection,
  deleteDoc,
  deleteField,
  doc,
  getDoc,
  getDocs,
  limit,
  query,
  setDoc,
  updateDoc,
  where,
} = require('firebase/firestore');
const { ref, uploadBytes, getBytes } = require('firebase/storage');

const ROOT = 'apps/greenpassport';
const ALICE = 'alice';
const BOB = 'bob';
const ADMIN = 'moderator';
const CAROL = 'carol';
const EDITOR = 'editor';
const SUPER_ADMIN = 'superAdmin';
const CASHIER = 'cashier';

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
    await setDoc(doc(db, `${ROOT}/admins/${EDITOR}`), { role: 'EDITOR' });
    await setDoc(doc(db, `${ROOT}/admins/${SUPER_ADMIN}`), { role: 'SUPER_ADMIN' });
    await setDoc(doc(db, `${ROOT}/partners/cafe`), { name: 'Кафе', isActive: true });
    await setDoc(doc(db, `${ROOT}/partnerUsers/${CASHIER}`), { partnerId: 'cafe' });
    await setDoc(doc(db, `${ROOT}/shopItems/coffee`), { title: 'Кофе', partnerId: 'cafe', issuedCount: 3 });
    await setDoc(doc(db, `${ROOT}/shopItems/coffee/codePool/CODE1`), { status: 'AVAILABLE' });
    await setDoc(doc(db, `${ROOT}/auditLog/entry1`), { collection: 'tasks', docId: 'selfTask', by: EDITOR, atEpochMillis: 1 });
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

const stamped = (uid, data) => ({ ...data, updatedBy: uid, updatedAtEpochMillis: 1 });

test('editors write stamped content, moderators and users cannot', async () => {
  const task = (uid) => doc(firestoreOf(uid), `${ROOT}/tasks/newTask`);
  await assertSucceeds(setDoc(task(EDITOR), stamped(EDITOR, { title: 'Новое', verification: 'SELF' })));
  await assertSucceeds(setDoc(task(SUPER_ADMIN), stamped(SUPER_ADMIN, { title: 'Новое' })));
  await assertFails(setDoc(task(EDITOR), { title: 'Без штампа' }));
  await assertFails(setDoc(task(EDITOR), stamped(SUPER_ADMIN, { title: 'Чужой штамп' })));
  await assertFails(setDoc(task(ADMIN), stamped(ADMIN, { title: 'Модератор' })));
  await assertFails(setDoc(task(ALICE), stamped(ALICE, { title: 'Пользователь' })));
  await assertFails(setDoc(doc(firestoreOf(EDITOR), `${ROOT}/users/${ALICE}`), stamped(EDITOR, { availablePoints: 1 })));
  await assertSucceeds(deleteDoc(doc(firestoreOf(EDITOR), `${ROOT}/tasks/selfTask`)));
  await assertFails(deleteDoc(doc(firestoreOf(ADMIN), `${ROOT}/tasks/photoTask`)));
});

test('editors cannot change the counters the server keeps', async () => {
  const coffee = doc(firestoreOf(EDITOR), `${ROOT}/shopItems/coffee`);
  await assertSucceeds(updateDoc(coffee, stamped(EDITOR, { title: 'Капучино' })));
  await assertFails(updateDoc(coffee, stamped(EDITOR, { issuedCount: 0 })));
  await assertFails(setDoc(doc(firestoreOf(EDITOR), `${ROOT}/shopItems/tea`), stamped(EDITOR, { usedCount: 5 })));
});

test('the code pool is closed to everyone, even editors', async () => {
  await assertFails(getDoc(doc(firestoreOf(ALICE), `${ROOT}/shopItems/coffee/codePool/CODE1`)));
  await assertFails(getDocs(collection(firestoreOf(EDITOR), `${ROOT}/shopItems/coffee/codePool`)));
  await assertFails(getDoc(doc(firestoreOf(CASHIER), `${ROOT}/shopItems/coffee/codePool/CODE1`)));
  await assertSucceeds(getDoc(doc(firestoreOf(ALICE), `${ROOT}/users/${ALICE}/rewardState/main`)));
});

test('partner accounts are visible to their owner and editors only', async () => {
  await assertSucceeds(getDoc(doc(firestoreOf(CASHIER), `${ROOT}/partnerUsers/${CASHIER}`)));
  await assertFails(getDoc(doc(firestoreOf(ALICE), `${ROOT}/partnerUsers/${CASHIER}`)));
  await assertFails(getDocs(collection(firestoreOf(ADMIN), `${ROOT}/partnerUsers`)));
  const linked = query(collection(firestoreOf(EDITOR), `${ROOT}/partnerUsers`), where('partnerId', '==', 'cafe'), limit(1));
  await assertSucceeds(getDocs(linked));
  await assertFails(setDoc(doc(firestoreOf(CASHIER), `${ROOT}/partnerUsers/${CASHIER}`), { partnerId: 'other' }));
});

test('the audit log is read by editors and written by nobody', async () => {
  await assertSucceeds(getDocs(collection(firestoreOf(EDITOR), `${ROOT}/auditLog`)));
  await assertFails(getDocs(collection(firestoreOf(ADMIN), `${ROOT}/auditLog`)));
  await assertFails(getDoc(doc(firestoreOf(ALICE), `${ROOT}/auditLog/entry1`)));
  await assertFails(setDoc(doc(firestoreOf(SUPER_ADMIN), `${ROOT}/auditLog/fake`), { by: SUPER_ADMIN }));
});

test('staff check references and count purchases', async () => {
  const progress = query(collection(firestoreOf(EDITOR), `${ROOT}/taskProgress`), where('taskId', '==', 'selfTask'), limit(1));
  await assertSucceeds(getDocs(progress));
  await assertSucceeds(getDocs(query(collection(firestoreOf(ADMIN), `${ROOT}/purchases`), where('usedAtEpochMillis', '>=', 0))));
  await assertFails(getDocs(collection(firestoreOf(CASHIER), `${ROOT}/purchases`)));
});

test('storage: content images are public to users, uploads need an editor', async () => {
  const image = new Uint8Array([0xff, 0xd8, 0xff]);
  const path = 'greenpassport/content/tasks/selfTask/cover.jpg';
  await env.withSecurityRulesDisabled((context) => uploadBytes(ref(context.storage(), path), image, { contentType: 'image/jpeg' }));
  await assertSucceeds(getBytes(ref(env.authenticatedContext(ALICE).storage(), path)));
  await assertFails(getBytes(ref(env.unauthenticatedContext().storage(), path)));
  await assertFails(uploadBytes(ref(env.authenticatedContext(ALICE).storage(), path), image, { contentType: 'image/jpeg' }));
  await assertFails(uploadBytes(ref(env.authenticatedContext(ADMIN).storage(), path), image, { contentType: 'image/jpeg' }));
});

test('authors edit and delete their own forum posts and chat messages only', async () => {
  const post = (uid) => doc(firestoreOf(uid), `${ROOT}/posts/post1`);
  const message = (uid) => doc(firestoreOf(uid), `${ROOT}/chats/group1/messages/m1`);
  for (const target of [post, message]) {
    await assertFails(updateDoc(target(CAROL), { text: 'Чужое', editedAtEpochMillis: 2 }));
    await assertFails(updateDoc(target(BOB), { text: 'ну ты и пиздюк', editedAtEpochMillis: 2 }));
    await assertFails(updateDoc(target(BOB), { text: 'Без отметки' }));
    await assertFails(updateDoc(target(BOB), { text: 'Привет', editedAtEpochMillis: 2, hidden: false }));
    await assertSucceeds(updateDoc(target(BOB), { text: 'Привет всем', editedAtEpochMillis: 2 }));
    await assertFails(updateDoc(target(CAROL), { text: '', deleted: true }));
    await assertFails(updateDoc(target(BOB), { text: 'не пусто', deleted: true }));
    await assertSucceeds(updateDoc(target(BOB), { text: '', deleted: true, replyTo: deleteField() }));
    await assertFails(updateDoc(target(BOB), { text: 'Ожило', editedAtEpochMillis: 3 }));
    await assertFails(deleteDoc(target(BOB)));
  }
});

test('replies and forwards are validated, server fields cannot be set on create', async () => {
  const db = firestoreOf(ALICE);
  const post = { authorId: ALICE, text: 'Ответ', createdAtEpochMillis: 1 };
  const replyTo = { messageId: 'post1', senderName: 'Боб', text: 'Привет' };
  await assertSucceeds(setDoc(doc(db, `${ROOT}/posts/reply`), { ...post, replyTo }));
  await assertSucceeds(setDoc(doc(db, `${ROOT}/posts/forward`), { ...post, forwardedFrom: { senderName: 'Боб' } }));
  await assertFails(setDoc(doc(db, `${ROOT}/posts/longQuote`), { ...post, replyTo: { ...replyTo, text: 'а'.repeat(201) } }));
  await assertFails(setDoc(doc(db, `${ROOT}/posts/extraQuote`), { ...post, replyTo: { ...replyTo, hidden: true } }));
  await assertFails(setDoc(doc(db, `${ROOT}/posts/dirtyForward`), { ...post, forwardedFrom: { senderName: 'Бляди' } }));
  await assertFails(setDoc(doc(db, `${ROOT}/posts/selfHidden`), { ...post, hidden: false }));
  await assertFails(setDoc(doc(db, `${ROOT}/posts/selfCounted`), { ...post, reportCount: 0 }));
  await assertFails(setDoc(doc(db, `${ROOT}/posts/preDeleted`), { ...post, deleted: true }));
  const messages = collection(firestoreOf(CAROL), `${ROOT}/chats/group1/messages`);
  const message = { senderId: CAROL, text: 'Ответ', createdAtEpochMillis: 2 };
  await assertSucceeds(setDoc(doc(messages, 'reply'), { ...message, replyTo: { messageId: 'm1', senderName: null, text: 'Привет' } }));
  await assertFails(setDoc(doc(messages, 'preEdited'), { ...message, editedAtEpochMillis: 2 }));
});

test('chat settings and devices are private to their owner', async () => {
  const settings = { userId: ALICE, chatId: 'forum', pinned: true, archived: false, muted: false, updatedAtEpochMillis: 1 };
  const aliceSettings = doc(firestoreOf(ALICE), `${ROOT}/chatSettings/${ALICE}_forum`);
  await assertSucceeds(setDoc(aliceSettings, settings));
  await assertSucceeds(getDoc(aliceSettings));
  await assertSucceeds(updateDoc(aliceSettings, { archived: true, updatedAtEpochMillis: 2 }));
  await assertFails(getDoc(doc(firestoreOf(BOB), `${ROOT}/chatSettings/${ALICE}_forum`)));
  await assertFails(setDoc(doc(firestoreOf(BOB), `${ROOT}/chatSettings/${ALICE}_forum`), { ...settings, userId: BOB }));
  await assertFails(setDoc(doc(firestoreOf(ALICE), `${ROOT}/chatSettings/${ALICE}_group1`), settings));
  await assertFails(setDoc(doc(firestoreOf(ALICE), `${ROOT}/chatSettings/${ALICE}_x`), { ...settings, chatId: 'x', muted: 'yes' }));
  await assertSucceeds(getDocs(query(collection(firestoreOf(ALICE), `${ROOT}/chatSettings`), where('userId', '==', ALICE))));
  await assertFails(getDocs(collection(firestoreOf(ALICE), `${ROOT}/chatSettings`)));

  const device = { userId: ALICE, platform: 'ANDROID', updatedAtEpochMillis: 1 };
  const aliceDevice = doc(firestoreOf(ALICE), `${ROOT}/userDevices/token1`);
  await assertSucceeds(setDoc(aliceDevice, device));
  await assertSucceeds(getDoc(aliceDevice));
  await assertFails(getDoc(doc(firestoreOf(BOB), `${ROOT}/userDevices/token1`)));
  await assertFails(setDoc(doc(firestoreOf(ALICE), `${ROOT}/userDevices/token2`), { ...device, userId: BOB }));
  await assertFails(setDoc(doc(firestoreOf(ALICE), `${ROOT}/userDevices/token3`), { ...device, platform: 'WEB' }));
  await assertSucceeds(deleteDoc(aliceDevice));
});
