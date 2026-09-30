const { initializeApp, cert } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');
const QRCode = require('qrcode');

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

const tasks = [
  { title: 'Сдай пластик на переработку', description: 'Отнеси пластиковые бутылки и упаковку в пункт приёма вторсырья и отсканируй QR-код на стойке.', category: 'RECYCLING', city: 'Минск', verification: 'QR', rewardPoints: 60, rewardXp: 60, imageUrl: null },
  { title: 'Сортировка бумаги дома', description: 'Заведи отдельный контейнер для бумаги и картона на неделю.', category: 'RECYCLING', city: 'Минск', verification: 'SELF', rewardPoints: 20, rewardXp: 20, imageUrl: null },
  { title: 'Убери мусор в парке', description: 'Собери мусор на выбранном участке парка или сквера и сфотографируй собранные мешки.', category: 'CLEANUP', city: 'Минск', verification: 'PHOTO', rewardPoints: 100, rewardXp: 100, imageUrl: null },
  { title: 'Субботник во дворе', description: 'Прими участие в уборке двора и пришли фото с места уборки.', category: 'CLEANUP', city: 'Гомель', verification: 'PHOTO', rewardPoints: 80, rewardXp: 80, imageUrl: null },
  { title: 'Доберись на учёбу на велосипеде', description: 'Замени поездку на машине или автобусе велосипедом.', category: 'TRANSPORT', city: 'Минск', verification: 'SELF', rewardPoints: 20, rewardXp: 20, imageUrl: null },
  { title: 'Откажись от машины на день', description: 'Проведи день, используя только пешие прогулки и общественный транспорт.', category: 'TRANSPORT', city: 'Брест', verification: 'SELF', rewardPoints: 20, rewardXp: 20, imageUrl: null },
  { title: 'Используй многоразовую сумку неделю', description: 'Откажись от одноразовых пакетов при походах в магазин на протяжении недели.', category: 'REUSABLE_ITEMS', city: 'Минск', verification: 'SELF', rewardPoints: 25, rewardXp: 25, imageUrl: null },
  { title: 'Откажись от одноразовой посуды', description: 'Замени одноразовые стаканы и приборы на многоразовые в течение недели.', category: 'REUSABLE_ITEMS', city: 'Гродно', verification: 'SELF', rewardPoints: 25, rewardXp: 25, imageUrl: null },
  { title: 'Посети лекцию об экологии', description: 'Сходи на открытую лекцию и отсканируй QR-код у организатора.', category: 'LECTURE', city: 'Минск', verification: 'QR', rewardPoints: 70, rewardXp: 70, imageUrl: null },
  { title: 'Пройди онлайн-курс по переработке', description: 'Заверши бесплатный онлайн-курс о переработке отходов и пришли фото сертификата.', category: 'LECTURE', city: 'Гомель', verification: 'PHOTO', rewardPoints: 90, rewardXp: 90, imageUrl: null },
];

const shopItems = [
  { title: 'Скидка 10% на кофе', partnerName: 'Кофейня «Зелёный лист»', pointsCost: 100, validityDays: 14 },
  { title: 'Скидка 15% на органические продукты', partnerName: 'Эко-маркет «Природа»', pointsCost: 200, validityDays: 30 },
  { title: 'Бесплатная многоразовая бутылка', partnerName: 'Магазин «ЭкоДом»', pointsCost: 300, validityDays: 60 },
  { title: 'Скидка 20% на велопрокат', partnerName: 'Велопрокат «КрутиПедали»', pointsCost: 250, validityDays: 30 },
  { title: 'Купон на посадку дерева', partnerName: 'Фонд «Зелёный город»', pointsCost: 150, validityDays: 60 },
  { title: 'Скидка 10% на химчистку с эко-средствами', partnerName: 'Химчистка «ЭкоКлин»', pointsCost: 120, validityDays: 21 },
];

const mapPoints = [
  { name: 'Пункт приёма вторсырья на Притыцкого', type: 'RECYCLING_POINT', address: 'ул. Притыцкого, 29', city: 'Минск', latitude: 53.9075, longitude: 27.4735 },
  { name: 'Эко-контейнеры у ст. м. Площадь Победы', type: 'RECYCLING_POINT', address: 'пр-т Независимости, 40', city: 'Минск', latitude: 53.9087, longitude: 27.5750 },
  { name: 'Магазин «ЭкоДом»', type: 'ECO_SHOP', address: 'ул. Немига, 5', city: 'Минск', latitude: 53.9036, longitude: 27.5536 },
  { name: 'Эко-маркет «Природа»', type: 'ECO_SHOP', address: 'пр-т Победителей, 9', city: 'Минск', latitude: 53.9085, longitude: 27.5480 },
  { name: 'Точка проведения субботника', type: 'ECO_EVENT', address: 'Парк Горького, вход с пр-та Независимости', city: 'Минск', latitude: 53.9036, longitude: 27.5751 },
  { name: 'Пункт приёма стекла', type: 'RECYCLING_POINT', address: 'ул. Советская, 21', city: 'Гомель', latitude: 52.4345, longitude: 30.9754 },
  { name: 'Эко-контейнеры на бульваре Шевченко', type: 'RECYCLING_POINT', address: 'бул. Шевченко, 6', city: 'Брест', latitude: 52.0936, longitude: 23.6852 },
];

const events = [
  { title: 'Эко-субботник в парке Горького', description: 'Совместная уборка территории парка, инвентарь предоставляется.', location: 'Парк Горького, вход с пр-та Независимости', city: 'Минск', startAtEpochMillis: 1792224000000, imageUrl: null, rewardPoints: 50 },
  { title: 'Лекция «Осознанное потребление»', description: 'Открытая лекция о том, как сократить количество отходов в быту.', location: 'Национальная библиотека Беларуси', city: 'Минск', startAtEpochMillis: 1792857600000, imageUrl: null, rewardPoints: 20 },
  { title: 'День вторсырья', description: 'Приём макулатуры, пластика и стекла на переработку.', location: 'Лошицкий парк, главный вход', city: 'Минск', startAtEpochMillis: 1794042000000, imageUrl: null, rewardPoints: 30 },
  { title: 'Велопробег за чистый воздух', description: 'Массовый велопробег по центру города в поддержку чистого воздуха.', location: 'Старт у стелы «Минск — город-герой»', city: 'Минск', startAtEpochMillis: 1795244400000, imageUrl: null, rewardPoints: 40 },
];

const ecoTips = [
  { category: 'ARTICLE', title: 'Как правильно сортировать пластик', body: 'Разбираем маркировку пластика на упаковке и что можно сдать на переработку уже сегодня.', mediaUrl: null, isDailyTip: true, rewardPoints: 15, rewardXp: 15 },
  { category: 'ARTICLE', title: '5 привычек для дома без отходов', body: 'Простые изменения в быту, которые заметно снижают количество мусора.', mediaUrl: null, isDailyTip: false, rewardPoints: 15, rewardXp: 15 },
  { category: 'ARTICLE', title: 'Что такое углеродный след', body: 'Объясняем простыми словами, как повседневные привычки влияют на климат.', mediaUrl: null, isDailyTip: false, rewardPoints: 20, rewardXp: 20 },
  { category: 'VIDEO', title: 'Как перерабатывают стекло', body: 'Короткое видео о полном цикле переработки стеклянной тары.', mediaUrl: 'https://youtu.be/example-glass-recycling', isDailyTip: false, rewardPoints: 20, rewardXp: 20 },
  { category: 'VIDEO', title: 'Путешествие пластиковой бутылки', body: 'От прилавка магазина до нового изделия — весь путь пластиковой бутылки.', mediaUrl: 'https://youtu.be/example-bottle-journey', isDailyTip: false, rewardPoints: 20, rewardXp: 20 },
  { category: 'KIDS', title: 'Почему нужно беречь воду', body: 'Простое объяснение для детей о том, откуда берётся вода и почему её нельзя тратить зря.', mediaUrl: null, isDailyTip: false, rewardPoints: 10, rewardXp: 10 },
  { category: 'KIDS', title: 'Сказка про мусорного гнома', body: 'Добрая история о гноме, который учит зверей сортировать мусор в лесу.', mediaUrl: null, isDailyTip: false, rewardPoints: 10, rewardXp: 10 },
];

const surveys = [
  { question: 'Как вы обычно избавляетесь от старой одежды?', options: ['Выбрасываю', 'Отдаю на переработку', 'Отдаю нуждающимся', 'Продаю или меняю'], isActive: true },
];

const games = [
  { id: 'eco_puzzle', titles: { ru: 'Эко-головоломка', be: 'Эка-галаваломка', en: 'Eco puzzle' }, path: 'eco_puzzle/index.html', sfSymbol: 'puzzlepiece.extension.fill', materialIcon: 'Extension', maxPoints: 30, order: 1, isActive: true },
  { id: 'waste_sorting', titles: { ru: 'Сортировка отходов', be: 'Сартаванне адходаў', en: 'Waste sorting' }, path: 'waste_sorting/index.html', sfSymbol: 'trash.fill', materialIcon: 'DeleteSweep', maxPoints: 30, order: 2, isActive: true },
  { id: 'eco_maze', titles: { ru: 'Эко-лабиринт', be: 'Эка-лабірынт', en: 'Eco maze' }, path: 'eco_maze/index.html', sfSymbol: 'safari.fill', materialIcon: 'Explore', maxPoints: 30, order: 3, isActive: true },
  { id: 'eco_quiz', titles: { ru: 'Эко-викторина', be: 'Эка-віктарына', en: 'Eco quiz' }, path: 'eco_quiz/index.html', sfSymbol: 'questionmark.bubble.fill', materialIcon: 'Quiz', maxPoints: 30, order: 4, isActive: true },
  { id: 'waste_catcher', titles: { ru: 'Поймай отходы', be: 'Злаві адходы', en: 'Waste catcher' }, path: 'waste_catcher/index.html', sfSymbol: 'arrow.down.to.line.compact', materialIcon: 'MoveDown', maxPoints: 30, order: 5, isActive: true },
  { id: 'myth_or_fact', titles: { ru: 'Правда или миф', be: 'Праўда ці міф', en: 'Myth or fact' }, path: 'myth_or_fact/index.html', sfSymbol: 'checkmark.circle.badge.questionmark.fill', materialIcon: 'FactCheck', maxPoints: 30, order: 6, isActive: true },
  { id: 'eco_words', titles: { ru: 'Эко-слова', be: 'Эка-словы', en: 'Eco words' }, path: 'eco_words/index.html', sfSymbol: 'textformat.abc', materialIcon: 'Abc', maxPoints: 30, order: 7, isActive: true },
  { id: 'water_saver', titles: { ru: 'Сбереги воду', be: 'Беражы ваду', en: 'Water saver' }, path: 'water_saver/index.html', sfSymbol: 'drop.fill', materialIcon: 'WaterDrop', maxPoints: 30, order: 8, isActive: true },
];

async function seedCollection(collectionName, documents) {
  if (onlyCollections && !onlyCollections.includes(collectionName)) {
    return [];
  }
  const collectionRef = root.collection(collectionName);
  const batch = db.batch();
  const written = documents.map((doc) => {
    const docRef = collectionRef.doc();
    batch.set(docRef, doc);
    return { id: docRef.id, doc };
  });
  await batch.commit();
  console.log(`${collectionName}: ${documents.length} documents written`);
  return written;
}

async function seedQrSecrets(writtenTasks) {
  const qrTasks = writtenTasks.filter(({ doc }) => doc.verification === 'QR');
  if (qrTasks.length === 0) {
    return;
  }
  fs.mkdirSync(QR_OUTPUT_DIR, { recursive: true });
  for (const { id, doc } of qrTasks) {
    const secret = crypto.randomBytes(QR_SECRET_BYTES).toString('hex');
    await root.collection('taskSecrets').doc(id).set({ code: secret });
    const payload = `greenpassport:task:${id}:${secret}`;
    const fileName = path.join(QR_OUTPUT_DIR, `${id}.png`);
    await QRCode.toFile(fileName, payload, { width: 600, margin: 2 });
    console.log(`QR for "${doc.title}" -> ${fileName}`);
  }
}

async function seedEventSecrets(writtenEvents) {
  if (writtenEvents.length === 0) {
    return;
  }
  fs.mkdirSync(QR_OUTPUT_DIR, { recursive: true });
  for (const { id, doc } of writtenEvents) {
    const secret = crypto.randomBytes(QR_SECRET_BYTES).toString('hex');
    await root.collection('eventSecrets').doc(id).set({ code: secret });
    const payload = `greenpassport:event:${id}:${secret}`;
    const fileName = path.join(QR_OUTPUT_DIR, `event_${id}.png`);
    await QRCode.toFile(fileName, payload, { width: 600, margin: 2 });
    console.log(`Check-in QR for "${doc.title}" -> ${fileName}`);
  }
}

async function seedGames() {
  if (onlyCollections && !onlyCollections.includes('games')) {
    return;
  }
  const batch = db.batch();
  for (const { id, ...game } of games) {
    batch.set(root.collection('games').doc(id), game);
  }
  await batch.commit();
  console.log(`games: ${games.length} documents written`);
}

async function seedAdmin() {
  if (!adminUid) {
    return;
  }
  await root.collection('admins').doc(adminUid).set({ grantedAt: Date.now() });
  console.log(`admins: ${adminUid} is now a moderator`);
}

async function main() {
  const writtenTasks = await seedCollection('tasks', tasks);
  await seedQrSecrets(writtenTasks);
  await seedCollection('shopItems', shopItems);
  await seedCollection('mapPoints', mapPoints);
  const writtenEvents = await seedCollection('events', events);
  await seedEventSecrets(writtenEvents);
  await seedCollection('ecoTips', ecoTips);
  await seedCollection('surveys', surveys);
  await seedGames();
  await seedAdmin();
  console.log('Done.');
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
