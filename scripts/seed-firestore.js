const { initializeApp, cert } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');
const QRCode = require('qrcode');
const tasks = require('./content/tasks');
const events = require('./content/events');
const shopItems = require('./content/shop');
const mapPoints = require('./content/map-points');
const surveys = require('./content/surveys');
const { ecoTips, plainText, COVERS_BASE_URL } = require('./content/eco-tips');
const { games, retiredGameIds } = require('./content/games');

const args = process.argv.slice(2);
const serviceAccountPath = args.find((arg) => !arg.startsWith('--')) || path.join(__dirname, 'service-account.json');
const onlyArg = args.find((arg) => arg.startsWith('--only='));
const onlyCollections = onlyArg ? onlyArg.slice('--only='.length).split(',') : null;
const adminArg = args.find((arg) => arg.startsWith('--admin='));
const adminUid = adminArg ? adminArg.slice('--admin='.length) : null;
const QR_OUTPUT_DIR = path.join(__dirname, 'qr');
const QR_SECRET_BYTES = 16;

initializeApp({
  credential: cert(require(serviceAccountPath)),
});

const db = getFirestore();
const root = db.collection('apps').doc('greenpassport');

function isSelected(collectionName) {
  return !onlyCollections || onlyCollections.includes(collectionName);
}

async function upsert(collectionName, items, matchKey, toDocument) {
  if (!isSelected(collectionName)) {
    return [];
  }
  const collectionRef = root.collection(collectionName);
  const existing = await collectionRef.get();
  const idByKey = new Map(existing.docs.map((doc) => [matchKey(doc.data()), doc.id]));
  const batch = db.batch();
  let updated = 0;
  const written = items.map(({ id, ...content }) => {
    const document = toDocument(content, id);
    const existingId = idByKey.get(matchKey(document));
    if (existingId) {
      updated += 1;
    }
    const docRef = collectionRef.doc(existingId ?? id);
    batch.set(docRef, document);
    return { id: docRef.id, doc: document };
  });
  await batch.commit();
  console.log(`${collectionName}: ${items.length} documents written, ${updated} updated in place`);
  return written;
}

async function ensureSecrets(written, secretsCollection, kind, filePrefix) {
  if (written.length === 0) {
    return;
  }
  fs.mkdirSync(QR_OUTPUT_DIR, { recursive: true });
  for (const { id, doc } of written) {
    const secretRef = root.collection(secretsCollection).doc(id);
    const secretDoc = await secretRef.get();
    let secret = secretDoc.exists ? secretDoc.get('code') : null;
    if (!secret) {
      secret = crypto.randomBytes(QR_SECRET_BYTES).toString('hex');
      await secretRef.set({ code: secret });
    }
    const fileName = path.join(QR_OUTPUT_DIR, `${filePrefix}${id}.png`);
    await QRCode.toFile(fileName, `greenpassport:${kind}:${id}:${secret}`, { width: 600, margin: 2 });
    console.log(`QR for "${doc.title}" -> ${fileName}`);
  }
}

async function seedTasks() {
  const written = await upsert('tasks', tasks, (doc) => doc.title, (task) => ({
    title: task.titles.ru,
    description: task.descriptions.ru,
    titles: task.titles,
    descriptions: task.descriptions,
    category: task.category,
    city: task.city,
    verification: task.verification,
    rewardPoints: task.rewardPoints,
    rewardXp: task.rewardXp,
    imageUrl: task.imageUrl ?? null,
  }));
  await ensureSecrets(written.filter(({ doc }) => doc.verification === 'QR'), 'taskSecrets', 'task', '');
}

async function seedEvents() {
  const written = await upsert('events', events, (doc) => doc.title, (event) => ({
    title: event.titles.ru,
    description: event.descriptions.ru,
    location: event.locations.ru,
    titles: event.titles,
    descriptions: event.descriptions,
    locations: event.locations,
    city: event.city,
    startAtEpochMillis: Date.parse(event.startAt),
    imageUrl: event.imageUrl ?? null,
    rewardPoints: event.rewardPoints,
  }));
  await ensureSecrets(written, 'eventSecrets', 'event', 'event_');
}

async function seedShop() {
  await upsert('shopItems', shopItems, (doc) => doc.title, (item) => ({
    title: item.titles.ru,
    partnerName: item.partnerNames.ru,
    titles: item.titles,
    partnerNames: item.partnerNames,
    pointsCost: item.pointsCost,
    validityDays: item.validityDays,
  }));
}

async function seedMapPoints() {
  await upsert('mapPoints', mapPoints, (doc) => `${doc.name}|${doc.address}`, (point) => ({
    name: point.names.ru,
    address: point.addresses.ru,
    names: point.names,
    addresses: point.addresses,
    type: point.type,
    city: point.city,
    latitude: point.latitude,
    longitude: point.longitude,
  }));
}

async function seedSurveys() {
  await upsert('surveys', surveys, (doc) => doc.question, (survey) => ({
    question: survey.questions.ru,
    options: survey.optionLists.ru,
    questions: survey.questions,
    optionLists: survey.optionLists,
    isActive: survey.isActive,
  }));
}

async function seedEcoTips() {
  await upsert('ecoTips', ecoTips, (doc) => doc.title, (tip, id) => ({
    category: tip.category,
    title: tip.titles.ru,
    body: plainText(tip.bodies.ru),
    titles: tip.titles,
    bodies: tip.bodies,
    mediaUrl: tip.mediaUrl,
    imageUrl: `${COVERS_BASE_URL}/${id}.jpg`,
    isDailyTip: tip.isDailyTip,
    rewardPoints: tip.rewardPoints,
    rewardXp: tip.rewardXp,
  }));
}

async function seedGames() {
  if (!isSelected('games')) {
    return;
  }
  const batch = db.batch();
  for (const { id, ...game } of games) {
    batch.set(root.collection('games').doc(id), game);
  }
  for (const id of retiredGameIds) {
    batch.set(root.collection('games').doc(id), { isActive: false }, { merge: true });
  }
  await batch.commit();
  console.log(`games: ${games.length} documents written, ${retiredGameIds.length} retired`);
}

async function seedAdmin() {
  if (!adminUid) {
    return;
  }
  await root.collection('admins').doc(adminUid).set({ grantedAt: Date.now() });
  console.log(`admins: ${adminUid} is now a moderator`);
}

async function main() {
  await seedTasks();
  await seedShop();
  await seedMapPoints();
  await seedEvents();
  await seedEcoTips();
  await seedSurveys();
  await seedGames();
  await seedAdmin();
  console.log('Done.');
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
