const BELVTOR = { ru: 'Белвторресурсы — пункт приёма вторсырья', be: 'Белвторрэсурсы — пункт прыёму другаснай сыравіны', en: 'Belvtorresursy recycling point' };

module.exports = [
  {
    id: 'pritytskogo_recycling',
    names: { ru: 'Пункт приёма вторсырья на Притыцкого', be: 'Пункт прыёму другаснай сыравіны на Прытыцкага', en: 'Pritytskogo St recycling point' },
    addresses: { ru: 'ул. Притыцкого, 29', be: 'вул. Прытыцкага, 29', en: '29 Pritytskogo St' },
    type: 'RECYCLING_POINT', city: 'Минск', latitude: 53.9075, longitude: 27.4735,
  },
  {
    id: 'pobedy_square_containers',
    names: { ru: 'Эко-контейнеры у ст. м. Площадь Победы', be: 'Эка-кантэйнеры каля ст. м. Плошча Перамогі', en: 'Recycling bins at Ploshcha Pieramohi metro' },
    addresses: { ru: 'пр-т Независимости, 40', be: 'пр-т Незалежнасці, 40', en: '40 Independence Ave' },
    type: 'RECYCLING_POINT', city: 'Минск', latitude: 53.9087, longitude: 27.5750,
  },
  {
    id: 'ecodom_store',
    names: { ru: 'Магазин «ЭкоДом»', be: 'Крама «ЭкаДом»', en: 'EcoHome store' },
    addresses: { ru: 'ул. Немига, 5', be: 'вул. Няміга, 5', en: '5 Nemiga St' },
    type: 'ECO_SHOP', city: 'Минск', latitude: 53.9036, longitude: 27.5536,
  },
  {
    id: 'priroda_market',
    names: { ru: 'Эко-маркет «Природа»', be: 'Эка-маркет «Прырода»', en: 'Nature Eco Market' },
    addresses: { ru: 'пр-т Победителей, 9', be: 'пр-т Пераможцаў, 9', en: '9 Pobediteley Ave' },
    type: 'ECO_SHOP', city: 'Минск', latitude: 53.9085, longitude: 27.5480,
  },
  {
    id: 'gorky_park_event_point',
    names: { ru: 'Точка проведения субботника', be: 'Месца правядзення суботніка', en: 'Clean-up meeting point' },
    addresses: { ru: 'Парк Горького, вход с пр-та Независимости', be: 'Парк Горкага, уваход з пр-та Незалежнасці', en: 'Gorky Park, entrance from Independence Ave' },
    type: 'ECO_EVENT', city: 'Минск', latitude: 53.9036, longitude: 27.5751,
  },
  {
    id: 'gomel_glass_point',
    names: { ru: 'Пункт приёма стекла', be: 'Пункт прыёму шкла', en: 'Glass collection point' },
    addresses: { ru: 'ул. Советская, 21', be: 'вул. Савецкая, 21', en: '21 Sovetskaya St' },
    type: 'RECYCLING_POINT', city: 'Гомель', latitude: 52.4345, longitude: 30.9754,
  },
  {
    id: 'brest_shevchenko_containers',
    names: { ru: 'Эко-контейнеры на бульваре Шевченко', be: 'Эка-кантэйнеры на бульвары Шаўчэнкі', en: 'Recycling bins on Shevchenko Blvd' },
    addresses: { ru: 'бул. Шевченко, 6', be: 'бул. Шаўчэнкі, 6', en: '6 Shevchenko Blvd' },
    type: 'RECYCLING_POINT', city: 'Брест', latitude: 52.0936, longitude: 23.6852,
  },
  {
    id: 'minsk_aivazovskogo_49',
    names: { ru: 'Минсккоопвторресурсы — пункт приёма вторсырья', be: 'Мінсккаопвторрэсурсы — пункт прыёму другаснай сыравіны', en: 'Minskkoopvtorresursy recycling point' },
    addresses: { ru: 'ул. Айвазовского, 49', be: 'вул. Айвазоўскага, 49', en: '49 Aivazovskogo St' },
    type: 'RECYCLING_POINT', city: 'Минск', latitude: 53.8629656, longitude: 27.6217446,
  },
  {
    id: 'minsk_ilimskaya_10',
    names: BELVTOR,
    addresses: { ru: 'ул. Илимская, 10', be: 'вул. Ілімская, 10', en: '10 Ilimskaya St' },
    type: 'RECYCLING_POINT', city: 'Минск', latitude: 53.8895508, longitude: 27.6890743,
  },
  {
    id: 'minsk_krasnoslobodskaya_84',
    names: BELVTOR,
    addresses: { ru: 'ул. Краснослободская, 84', be: 'вул. Краснаслабодская, 84', en: '84 Krasnoslobodskaya St' },
    type: 'RECYCLING_POINT', city: 'Минск', latitude: 53.8462595, longitude: 27.6386126,
  },
  {
    id: 'minsk_okhotskaya_172',
    names: BELVTOR,
    addresses: { ru: 'ул. Охотская, 172', be: 'вул. Ахоцкая, 172', en: '172 Okhotskaya St' },
    type: 'RECYCLING_POINT', city: 'Минск', latitude: 53.8849978, longitude: 27.6633165,
  },
  {
    id: 'minsk_rotmistrova_4',
    names: BELVTOR,
    addresses: { ru: 'ул. Ротмистрова, 4', be: 'вул. Ратмістрава, 4', en: '4 Rotmistrova St' },
    type: 'RECYCLING_POINT', city: 'Минск', latitude: 53.8378835, longitude: 27.6927058,
  },
  {
    id: 'minsk_przhevalskogo_1',
    names: { ru: 'Пункт приёма вторсырья облпотребсоюза', be: 'Пункт прыёму другаснай сыравіны аблспажыўсаюза', en: 'Regional consumer union recycling point' },
    addresses: { ru: 'ул. Пржевальского, 1', be: 'вул. Пржавальскага, 1', en: '1 Przhevalskogo St' },
    type: 'RECYCLING_POINT', city: 'Минск', latitude: 53.8663599, longitude: 27.6659974,
  },
  {
    id: 'minsk_partizansky_28',
    names: { ru: 'Экорес — пункт приёма вторсырья', be: 'Экарэс — пункт прыёму другаснай сыравіны', en: 'Ekores recycling point' },
    addresses: { ru: 'пр-т Партизанский, 28', be: 'пр-т Партызанскі, 28', en: '28 Partizansky Ave' },
    type: 'RECYCLING_POINT', city: 'Минск', latitude: 53.8768503, longitude: 27.6108526,
  },
  {
    id: 'brest_bogdanchuka_1',
    names: { ru: 'Брестский рынок — приём вторсырья и батареек', be: 'Брэсцкі рынак — прыём другаснай сыравіны і батарэек', en: 'Brest Market — recyclables and batteries' },
    addresses: { ru: 'ул. Богданчука, 1', be: 'вул. Багданчука, 1', en: '1 Bogdanchuka St' },
    type: 'RECYCLING_POINT', city: 'Брест', latitude: 52.0952245, longitude: 23.747855,
  },
  {
    id: 'brest_kovelskaya_87',
    names: { ru: 'Брестский рынок — пункт приёма вторсырья', be: 'Брэсцкі рынак — пункт прыёму другаснай сыравіны', en: 'Brest Market recycling point' },
    addresses: { ru: 'ул. Ковельская, 87', be: 'вул. Кавельская, 87', en: '87 Kovelskaya St' },
    type: 'RECYCLING_POINT', city: 'Брест', latitude: 52.0632157, longitude: 23.6897565,
  },
  {
    id: 'baranovichi_vlksm_43a',
    names: { ru: 'Барановичское ЖКХ — пункт приёма вторсырья', be: 'Баранавіцкая ЖКГ — пункт прыёму другаснай сыравіны', en: 'Baranovichi utilities recycling point' },
    addresses: { ru: 'ул. 50 лет ВЛКСМ, 43А', be: 'вул. 50 гадоў ВЛКСМ, 43А', en: '43A 50 Let VLKSM St' },
    type: 'RECYCLING_POINT', city: 'Барановичи', latitude: 53.1069201, longitude: 26.0076602,
  },
  {
    id: 'pinsk_gaidaenko_43',
    names: BELVTOR,
    addresses: { ru: 'ул. Гайдаенко, 43', be: 'вул. Гайдаенкі, 43', en: '43 Gaidaenko St' },
    type: 'RECYCLING_POINT', city: 'Пинск', latitude: 52.1239549, longitude: 26.0860229,
  },
  {
    id: 'pinsk_kozubovskogo_21',
    names: { ru: 'Пинская МРТБ — пункт приёма вторсырья', be: 'Пінская МРТБ — пункт прыёму другаснай сыравіны', en: 'Pinsk MRTB recycling point' },
    addresses: { ru: 'ул. Козубовского, 21', be: 'вул. Казубоўскага, 21', en: '21 Kozubovskogo St' },
    type: 'RECYCLING_POINT', city: 'Пинск', latitude: 52.1216863, longitude: 26.0250253,
  },
];
