const { initializeApp, cert } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');
const path = require('path');

const serviceAccountPath = process.argv[2] || path.join(__dirname, 'service-account.json');

initializeApp({
  credential: cert(require(serviceAccountPath)),
});

const db = getFirestore();
const root = db.collection('apps').doc('greenpassport');

const tasks = [
  { title: 'Сдай пластик на переработку', description: 'Отнеси пластиковые бутылки и упаковку в ближайший пункт приёма вторсырья.', category: 'RECYCLING', city: 'Москва', rewardPoints: 50, rewardXp: 50, imageUrl: null },
  { title: 'Сортировка бумаги дома', description: 'Заведи отдельный контейнер для бумаги и картона на неделю.', category: 'RECYCLING', city: 'Москва', rewardPoints: 30, rewardXp: 30, imageUrl: null },
  { title: 'Убери мусор в парке', description: 'Собери мусор на выбранном участке парка или сквера.', category: 'CLEANUP', city: 'Москва', rewardPoints: 100, rewardXp: 100, imageUrl: null },
  { title: 'Субботник во дворе', description: 'Прими участие в уборке территории своего двора.', category: 'CLEANUP', city: 'Санкт-Петербург', rewardPoints: 80, rewardXp: 80, imageUrl: null },
  { title: 'Доберись на работу на велосипеде', description: 'Замени поездку на машине или общественном транспорте на велосипед.', category: 'TRANSPORT', city: 'Москва', rewardPoints: 40, rewardXp: 40, imageUrl: null },
  { title: 'Откажись от машины на день', description: 'Проведи день, используя только пешие прогулки и общественный транспорт.', category: 'TRANSPORT', city: 'Санкт-Петербург', rewardPoints: 60, rewardXp: 60, imageUrl: null },
  { title: 'Используй многоразовую сумку неделю', description: 'Откажись от одноразовых пакетов при походах в магазин на протяжении недели.', category: 'REUSABLE_ITEMS', city: 'Москва', rewardPoints: 35, rewardXp: 35, imageUrl: null },
  { title: 'Откажись от одноразовой посуды', description: 'Замени одноразовые стаканы и приборы на многоразовые в течение недели.', category: 'REUSABLE_ITEMS', city: 'Москва', rewardPoints: 45, rewardXp: 45, imageUrl: null },
  { title: 'Посети лекцию об экологии', description: 'Сходи на открытую лекцию или встречу, посвящённую экологии города.', category: 'LECTURE', city: 'Москва', rewardPoints: 70, rewardXp: 70, imageUrl: null },
  { title: 'Пройди онлайн-курс по переработке', description: 'Заверши короткий бесплатный онлайн-курс о переработке отходов.', category: 'LECTURE', city: 'Санкт-Петербург', rewardPoints: 90, rewardXp: 90, imageUrl: null },
];

const shopItems = [
  { title: 'Скидка 10% на кофе', partnerName: 'Кофейня «Зелёный лист»', pointsCost: 100 },
  { title: 'Скидка 15% на органические продукты', partnerName: 'Эко-маркет «Природа»', pointsCost: 200 },
  { title: 'Бесплатная многоразовая бутылка', partnerName: 'Магазин «ЭкоДом»', pointsCost: 300 },
  { title: 'Скидка 20% на велопрокат', partnerName: 'Велопрокат «КрутиПедали»', pointsCost: 250 },
  { title: 'Купон на посадку дерева', partnerName: 'Фонд «Зелёный город»', pointsCost: 150 },
  { title: 'Скидка 10% на химчистку с эко-средствами', partnerName: 'Химчистка «ЭкоКлин»', pointsCost: 120 },
];

const mapPoints = [
  { name: 'Пункт приёма пластика на Тверской', type: 'RECYCLING_POINT', address: 'ул. Тверская, 15', city: 'Москва', latitude: 55.7658, longitude: 37.6058 },
  { name: 'Эко-контейнеры у м. Сокольники', type: 'RECYCLING_POINT', address: 'Сокольнический Вал, 1', city: 'Москва', latitude: 55.7887, longitude: 37.6799 },
  { name: 'Магазин «ЭкоДом»', type: 'ECO_SHOP', address: 'Кутузовский проспект, 30', city: 'Москва', latitude: 55.7387, longitude: 37.5347 },
  { name: 'Эко-маркет «Природа»', type: 'ECO_SHOP', address: 'Проспект Мира, 45', city: 'Москва', latitude: 55.7943, longitude: 37.6377 },
  { name: 'Точка проведения субботника', type: 'ECO_EVENT', address: 'Парк Горького, главный вход', city: 'Москва', latitude: 55.7298, longitude: 37.6019 },
];

const events = [
  { title: 'Эко-субботник в парке Горького', description: 'Совместная уборка территории парка, инвентарь предоставляется.', location: 'Парк Горького, главный вход', city: 'Москва', startAtEpochMillis: 1787472000000 },
  { title: 'Лекция «Осознанное потребление»', description: 'Открытая лекция о том, как сократить количество отходов в быту.', location: 'Библиотека им. Некрасова', city: 'Москва', startAtEpochMillis: 1787931000000 },
  { title: 'День вторсырья', description: 'Приём макулатуры, пластика и стекла на переработку.', location: 'Сокольники, площадь у ДК', city: 'Москва', startAtEpochMillis: 1788591600000 },
  { title: 'Велопробег за чистый воздух', description: 'Массовый велопробег по центру города в поддержку чистого воздуха.', location: 'Старт у ВДНХ', city: 'Москва', startAtEpochMillis: 1789192800000 },
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

async function seedCollection(collectionName, documents) {
  const collectionRef = root.collection(collectionName);
  const batch = db.batch();
  documents.forEach((doc) => {
    const docRef = collectionRef.doc();
    batch.set(docRef, doc);
  });
  await batch.commit();
  console.log(`${collectionName}: ${documents.length} documents written`);
}

async function main() {
  await seedCollection('tasks', tasks);
  await seedCollection('shopItems', shopItems);
  await seedCollection('mapPoints', mapPoints);
  await seedCollection('events', events);
  await seedCollection('ecoTips', ecoTips);
  await seedCollection('surveys', surveys);
  console.log('Done.');
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
