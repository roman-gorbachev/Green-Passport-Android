module.exports = [
  {
    id: 'recycle_plastic_minsk',
    titles: { ru: 'Сдай пластик на переработку', be: 'Здай пластык на перапрацоўку', en: 'Recycle plastic' },
    descriptions: {
      ru: 'Отнеси пластиковые бутылки и упаковку в пункт приёма вторсырья и отсканируй QR-код на стойке.',
      be: 'Аднясі пластыкавыя бутэлькі і ўпакоўку ў пункт прыёму другаснай сыравіны і адскануй QR-код на стойцы.',
      en: 'Take plastic bottles and packaging to a recycling point and scan the QR code on the stand.',
    },
    category: 'RECYCLING', city: 'Минск', verification: 'QR', rewardPoints: 60, rewardXp: 60,
  },
  {
    id: 'sort_paper_minsk',
    titles: { ru: 'Сортировка бумаги дома', be: 'Сартаванне паперы дома', en: 'Sort paper at home' },
    descriptions: {
      ru: 'Заведи отдельный контейнер для бумаги и картона на неделю.',
      be: 'Завядзі асобны кантэйнер для паперы і кардону на тыдзень.',
      en: 'Keep a separate box for paper and cardboard for a week.',
    },
    category: 'RECYCLING', city: 'Минск', verification: 'SELF', rewardPoints: 20, rewardXp: 20,
  },
  {
    id: 'park_cleanup_minsk',
    titles: { ru: 'Убери мусор в парке', be: 'Прыбяры смецце ў парку', en: 'Clean up a park' },
    descriptions: {
      ru: 'Собери мусор на выбранном участке парка или сквера и сфотографируй собранные мешки.',
      be: 'Збяры смецце на выбраным участку парку або сквера і сфатаграфуй сабраныя мяшкі.',
      en: 'Pick up litter in a part of a park or square and take a photo of the full bags.',
    },
    category: 'CLEANUP', city: 'Минск', verification: 'PHOTO', rewardPoints: 100, rewardXp: 100,
  },
  {
    id: 'yard_cleanup_gomel',
    titles: { ru: 'Субботник во дворе', be: 'Суботнік у двары', en: 'Yard clean-up day' },
    descriptions: {
      ru: 'Прими участие в уборке двора и пришли фото с места уборки.',
      be: 'Прымі ўдзел у прыбіранні двара і дашлі фота з месца прыбірання.',
      en: 'Join a clean-up of your yard and send a photo from the spot.',
    },
    category: 'CLEANUP', city: 'Гомель', verification: 'PHOTO', rewardPoints: 80, rewardXp: 80,
  },
  {
    id: 'bike_to_study_minsk',
    titles: { ru: 'Доберись на учёбу на велосипеде', be: 'Даедзь на вучобу на ровары', en: 'Cycle to school or college' },
    descriptions: {
      ru: 'Замени поездку на машине или автобусе велосипедом.',
      be: 'Замяні паездку на машыне або аўтобусе роварам.',
      en: 'Replace a car or bus ride with your bike.',
    },
    category: 'TRANSPORT', city: 'Минск', verification: 'SELF', rewardPoints: 20, rewardXp: 20,
  },
  {
    id: 'car_free_day_brest',
    titles: { ru: 'Откажись от машины на день', be: 'Адмоўся ад машыны на дзень', en: 'Go car-free for a day' },
    descriptions: {
      ru: 'Проведи день, используя только пешие прогулки и общественный транспорт.',
      be: 'Правядзі дзень, карыстаючыся толькі пешымі прагулкамі і грамадскім транспартам.',
      en: 'Spend a day getting around only on foot and by public transport.',
    },
    category: 'TRANSPORT', city: 'Брест', verification: 'SELF', rewardPoints: 20, rewardXp: 20,
  },
  {
    id: 'reusable_bag_minsk',
    titles: { ru: 'Используй многоразовую сумку неделю', be: 'Карыстайся шматразовай сумкай тыдзень', en: 'Use a reusable bag for a week' },
    descriptions: {
      ru: 'Откажись от одноразовых пакетов при походах в магазин на протяжении недели.',
      be: 'Адмоўся ад аднаразовых пакетаў падчас паходаў у краму на працягу тыдня.',
      en: 'Say no to single-use bags whenever you go shopping for a week.',
    },
    category: 'REUSABLE_ITEMS', city: 'Минск', verification: 'SELF', rewardPoints: 25, rewardXp: 25,
  },
  {
    id: 'no_disposables_grodno',
    titles: { ru: 'Откажись от одноразовой посуды', be: 'Адмоўся ад аднаразовага посуду', en: 'Skip disposable tableware' },
    descriptions: {
      ru: 'Замени одноразовые стаканы и приборы на многоразовые в течение недели.',
      be: 'Замяні аднаразовыя шклянкі і прыборы на шматразовыя на працягу тыдня.',
      en: 'Swap disposable cups and cutlery for reusable ones for a week.',
    },
    category: 'REUSABLE_ITEMS', city: 'Гродно', verification: 'SELF', rewardPoints: 25, rewardXp: 25,
  },
  {
    id: 'eco_lecture_minsk',
    titles: { ru: 'Посети лекцию об экологии', be: 'Наведай лекцыю пра экалогію', en: 'Attend an ecology talk' },
    descriptions: {
      ru: 'Сходи на открытую лекцию и отсканируй QR-код у организатора.',
      be: 'Схадзі на адкрытую лекцыю і адскануй QR-код у арганізатара.',
      en: 'Go to an open talk and scan the organiser’s QR code.',
    },
    category: 'LECTURE', city: 'Минск', verification: 'QR', rewardPoints: 70, rewardXp: 70,
  },
  {
    id: 'recycling_course_gomel',
    titles: { ru: 'Пройди онлайн-курс по переработке', be: 'Прайдзі анлайн-курс па перапрацоўцы', en: 'Take an online recycling course' },
    descriptions: {
      ru: 'Заверши бесплатный онлайн-курс о переработке отходов и пришли фото сертификата.',
      be: 'Скончы бясплатны анлайн-курс пра перапрацоўку адходаў і дашлі фота сертыфіката.',
      en: 'Finish a free online course on waste recycling and send a photo of the certificate.',
    },
    category: 'LECTURE', city: 'Гомель', verification: 'PHOTO', rewardPoints: 90, rewardXp: 90,
  },
  {
    id: 'batteries_vitebsk',
    titles: { ru: 'Сдай батарейки', be: 'Здай батарэйкі', en: 'Recycle batteries' },
    descriptions: {
      ru: 'Собери дома использованные батарейки и отнеси их в специальный бокс в магазине. Сфотографируй, как опускаешь их в бокс.',
      be: 'Збяры дома выкарыстаныя батарэйкі і аднясі іх у спецыяльны бокс у краме. Сфатаграфуй, як апускаеш іх у бокс.',
      en: 'Collect used batteries at home and drop them into a battery box at a shop. Take a photo as you drop them in.',
    },
    category: 'RECYCLING', city: 'Витебск', verification: 'PHOTO', rewardPoints: 50, rewardXp: 50,
  },
  {
    id: 'riverbank_cleanup_vitebsk',
    titles: { ru: 'Очисти берег реки', be: 'Ачысці бераг ракі', en: 'Clean a riverbank' },
    descriptions: {
      ru: 'Собери мусор на берегу Западной Двины или Витьбы и пришли фото собранных мешков.',
      be: 'Збяры смецце на беразе Заходняй Дзвіны або Віцьбы і дашлі фота сабраных мяшкоў.',
      en: 'Pick up litter on the bank of the Western Dvina or the Vitba and send a photo of the bags.',
    },
    category: 'CLEANUP', city: 'Витебск', verification: 'PHOTO', rewardPoints: 100, rewardXp: 100,
  },
  {
    id: 'walk_to_work_mogilev',
    titles: { ru: 'Пройди пешком 5 000 шагов вместо поездки', be: 'Прайдзі пешшу 5 000 крокаў замест паездкі', en: 'Walk 5,000 steps instead of driving' },
    descriptions: {
      ru: 'Замени короткую поездку на транспорте прогулкой пешком.',
      be: 'Замяні кароткую паездку на транспарце прагулкай пешшу.',
      en: 'Replace a short ride with a walk.',
    },
    category: 'TRANSPORT', city: 'Могилёв', verification: 'SELF', rewardPoints: 20, rewardXp: 20,
  },
  {
    id: 'glass_jars_mogilev',
    titles: { ru: 'Сдай стеклянные банки', be: 'Здай шкляныя слоікі', en: 'Recycle glass jars' },
    descriptions: {
      ru: 'Отнеси вымытые стеклянные банки и бутылки в зелёный контейнер для стекла и сфотографируй это.',
      be: 'Аднясі вымытыя шкляныя слоікі і бутэлькі ў зялёны кантэйнер для шкла і сфатаграфуй гэта.',
      en: 'Take rinsed glass jars and bottles to the green glass bin and take a photo.',
    },
    category: 'RECYCLING', city: 'Могилёв', verification: 'PHOTO', rewardPoints: 50, rewardXp: 50,
  },
  {
    id: 'thermos_week_bobruisk',
    titles: { ru: 'Неделя с термокружкой', be: 'Тыдзень з тэрмакубкам', en: 'A week with a travel mug' },
    descriptions: {
      ru: 'Бери напитки навынос только в свою термокружку в течение недели.',
      be: 'Бяры напоі на вынас толькі ў свой тэрмакубак на працягу тыдня.',
      en: 'Get takeaway drinks only in your own mug for a week.',
    },
    category: 'REUSABLE_ITEMS', city: 'Бобруйск', verification: 'SELF', rewardPoints: 25, rewardXp: 25,
  },
  {
    id: 'plant_tree_bobruisk',
    titles: { ru: 'Посади дерево', be: 'Пасадзі дрэва', en: 'Plant a tree' },
    descriptions: {
      ru: 'Посади дерево или куст во дворе, на даче или на городской акции и пришли фото саженца.',
      be: 'Пасадзі дрэва або куст у двары, на лецішчы або на гарадской акцыі і дашлі фота саджанца.',
      en: 'Plant a tree or shrub in a yard, at a summer house or at a city event and send a photo of it.',
    },
    category: 'CLEANUP', city: 'Бобруйск', verification: 'PHOTO', rewardPoints: 90, rewardXp: 90,
  },
  {
    id: 'old_clothes_baranovichi',
    titles: { ru: 'Отдай вещи на вторую жизнь', be: 'Аддай рэчы на другое жыццё', en: 'Give clothes a second life' },
    descriptions: {
      ru: 'Отнеси ненужную одежду в пункт приёма вещей или благотворительный магазин и сфотографируй пакет с вещами.',
      be: 'Аднясі непатрэбнае адзенне ў пункт прыёму рэчаў або дабрачынную краму і сфатаграфуй пакет з рэчамі.',
      en: 'Take clothes you no longer need to a donation point or charity shop and photograph the bag.',
    },
    category: 'RECYCLING', city: 'Барановичи', verification: 'PHOTO', rewardPoints: 60, rewardXp: 60,
  },
  {
    id: 'bus_week_baranovichi',
    titles: { ru: 'Неделя на общественном транспорте', be: 'Тыдзень на грамадскім транспарце', en: 'A week on public transport' },
    descriptions: {
      ru: 'Всю неделю передвигайся по городу только на автобусе, троллейбусе или поезде.',
      be: 'Увесь тыдзень перамяшчайся па горадзе толькі на аўтобусе, тралейбусе або цягніку.',
      en: 'Get around town only by bus, trolleybus or train for a whole week.',
    },
    category: 'TRANSPORT', city: 'Барановичи', verification: 'SELF', rewardPoints: 30, rewardXp: 30,
  },
  {
    id: 'compost_borisov',
    titles: { ru: 'Начни компостировать', be: 'Пачні кампаставаць', en: 'Start composting' },
    descriptions: {
      ru: 'Заведи компостер для очистков на даче или балконе и пришли фото первой закладки.',
      be: 'Завядзі кампосцер для ачыстак на лецішчы або балконе і дашлі фота першай закладкі.',
      en: 'Set up a compost bin for food scraps at a summer house or on a balcony and send a photo of the first batch.',
    },
    category: 'RECYCLING', city: 'Борисов', verification: 'PHOTO', rewardPoints: 70, rewardXp: 70,
  },
  {
    id: 'eco_webinar_borisov',
    titles: { ru: 'Посмотри вебинар об энергосбережении', be: 'Паглядзі вебінар пра энергазберажэнне', en: 'Watch an energy-saving webinar' },
    descriptions: {
      ru: 'Посмотри онлайн-вебинар о том, как экономить энергию дома, и запиши три совета, которые применишь.',
      be: 'Паглядзі анлайн-вебінар пра тое, як эканоміць энергію дома, і запішы тры парады, якія выкарыстаеш.',
      en: 'Watch an online webinar about saving energy at home and write down three tips you will use.',
    },
    category: 'LECTURE', city: 'Борисов', verification: 'SELF', rewardPoints: 30, rewardXp: 30,
  },
  {
    id: 'forest_cleanup_pinsk',
    titles: { ru: 'Очисти опушку леса', be: 'Ачысці ўзлесак', en: 'Clean up a forest edge' },
    descriptions: {
      ru: 'Собери мусор на опушке леса или у места для пикника и сфотографируй результат.',
      be: 'Збяры смецце на ўзлеску або каля месца для пікніка і сфатаграфуй вынік.',
      en: 'Pick up litter at the edge of a forest or near a picnic spot and photograph the result.',
    },
    category: 'CLEANUP', city: 'Пинск', verification: 'PHOTO', rewardPoints: 100, rewardXp: 100,
  },
  {
    id: 'water_bottle_pinsk',
    titles: { ru: 'Носи свою бутылку для воды', be: 'Насі сваю бутэльку для вады', en: 'Carry your own water bottle' },
    descriptions: {
      ru: 'Неделю не покупай воду в пластике — наливай её в многоразовую бутылку.',
      be: 'Тыдзень не купляй ваду ў пластыку — налівай яе ў шматразовую бутэльку.',
      en: 'For a week, don’t buy bottled water — refill a reusable bottle instead.',
    },
    category: 'REUSABLE_ITEMS', city: 'Пинск', verification: 'SELF', rewardPoints: 25, rewardXp: 25,
  },
  {
    id: 'cans_orsha',
    titles: { ru: 'Сдай алюминиевые банки', be: 'Здай алюмініевыя бляшанкі', en: 'Recycle aluminium cans' },
    descriptions: {
      ru: 'Собери алюминиевые банки, сомни их и отнеси в пункт приёма металла. Пришли фото.',
      be: 'Збяры алюмініевыя бляшанкі, сцісні іх і аднясі ў пункт прыёму металу. Дашлі фота.',
      en: 'Collect aluminium cans, crush them and take them to a metal collection point. Send a photo.',
    },
    category: 'RECYCLING', city: 'Орша', verification: 'PHOTO', rewardPoints: 50, rewardXp: 50,
  },
  {
    id: 'cycle_weekend_orsha',
    titles: { ru: 'Выходные на велосипеде', be: 'Выхадныя на ровары', en: 'A weekend by bike' },
    descriptions: {
      ru: 'Проведи выходные, передвигаясь по городу только на велосипеде.',
      be: 'Правядзі выхадныя, перамяшчаючыся па горадзе толькі на ровары.',
      en: 'Spend a weekend getting around town only by bike.',
    },
    category: 'TRANSPORT', city: 'Орша', verification: 'SELF', rewardPoints: 25, rewardXp: 25,
  },
  {
    id: 'shopping_list_mozyr',
    titles: { ru: 'Покупки без лишней упаковки', be: 'Пакупкі без лішняй упакоўкі', en: 'Shop without extra packaging' },
    descriptions: {
      ru: 'Сходи в магазин со своими мешочками для овощей и купи всё без одноразовых пакетов.',
      be: 'Схадзі ў краму са сваімі мяшэчкамі для гародніны і купі ўсё без аднаразовых пакетаў.',
      en: 'Go shopping with your own produce bags and buy everything without single-use bags.',
    },
    category: 'REUSABLE_ITEMS', city: 'Мозырь', verification: 'SELF', rewardPoints: 25, rewardXp: 25,
  },
  {
    id: 'playground_cleanup_mozyr',
    titles: { ru: 'Убери детскую площадку', be: 'Прыбяры дзіцячую пляцоўку', en: 'Tidy up a playground' },
    descriptions: {
      ru: 'Собери мусор на детской площадке рядом с домом и пришли фото чистой площадки.',
      be: 'Збяры смецце на дзіцячай пляцоўцы побач з домам і дашлі фота чыстай пляцоўкі.',
      en: 'Pick up litter on a playground near your home and send a photo of it clean.',
    },
    category: 'CLEANUP', city: 'Мозырь', verification: 'PHOTO', rewardPoints: 80, rewardXp: 80,
  },
  {
    id: 'electronics_brest',
    titles: { ru: 'Сдай старую технику', be: 'Здай старую тэхніку', en: 'Recycle old electronics' },
    descriptions: {
      ru: 'Отнеси сломанный телефон, зарядку или мелкую технику в пункт приёма электроники и пришли фото.',
      be: 'Аднясі зламаны тэлефон, зарадку або дробную тэхніку ў пункт прыёму электронікі і дашлі фота.',
      en: 'Take a broken phone, charger or small appliance to an electronics collection point and send a photo.',
    },
    category: 'RECYCLING', city: 'Брест', verification: 'PHOTO', rewardPoints: 60, rewardXp: 60,
  },
  {
    id: 'unplug_week_brest',
    titles: { ru: 'Неделя без «спящих» приборов', be: 'Тыдзень без «спячых» прыбораў', en: 'A week without standby devices' },
    descriptions: {
      ru: 'Каждый вечер выключай из розетки зарядки, телевизор и компьютер вместо режима ожидания.',
      be: 'Кожны вечар выключай з разеткі зарадкі, тэлевізар і камп’ютар замест рэжыму чакання.',
      en: 'Every evening, unplug chargers, the TV and the computer instead of leaving them on standby.',
    },
    category: 'REUSABLE_ITEMS', city: 'Брест', verification: 'SELF', rewardPoints: 20, rewardXp: 20,
  },
  {
    id: 'lunchbox_grodno',
    titles: { ru: 'Обед в своём контейнере', be: 'Абед у сваім кантэйнеры', en: 'Lunch in your own box' },
    descriptions: {
      ru: 'Неделю бери обед из дома в многоразовом контейнере вместо еды навынос.',
      be: 'Тыдзень бяры абед з дому ў шматразовым кантэйнеры замест ежы на вынас.',
      en: 'For a week, bring lunch from home in a reusable box instead of buying takeaway.',
    },
    category: 'REUSABLE_ITEMS', city: 'Гродно', verification: 'SELF', rewardPoints: 25, rewardXp: 25,
  },
  {
    id: 'park_cleanup_grodno',
    titles: { ru: 'Субботник в городском парке', be: 'Суботнік у гарадскім парку', en: 'City park clean-up' },
    descriptions: {
      ru: 'Прими участие в уборке городского парка и пришли фото с собранным мусором.',
      be: 'Прымі ўдзел у прыбіранні гарадскога парку і дашлі фота са сабраным смеццем.',
      en: 'Join a clean-up of a city park and send a photo with the litter you collected.',
    },
    category: 'CLEANUP', city: 'Гродно', verification: 'PHOTO', rewardPoints: 90, rewardXp: 90,
  },
];
