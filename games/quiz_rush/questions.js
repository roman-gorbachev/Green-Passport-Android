const QUESTIONS = [
  {
    q: { ru: 'Куда выбросить пластиковую бутылку?', be: 'Куды выкінуць пластыкавую бутэльку?', en: 'Where does a plastic bottle go?' },
    a: [
      { ru: 'В контейнер для пластика', be: 'У кантэйнер для пластыку', en: 'Into the plastic bin' },
      { ru: 'В контейнер для стекла', be: 'У кантэйнер для шкла', en: 'Into the glass bin' },
      { ru: 'В общий мусор', be: 'У агульнае смецце', en: 'Into general waste' },
      { ru: 'В бак для бумаги', be: 'У бак для паперы', en: 'Into the paper bin' },
    ],
  },
  {
    q: { ru: 'Сколько лет разлагается стеклянная бутылка?', be: 'Колькі гадоў раскладаецца шкляная бутэлька?', en: 'How long does a glass bottle take to break down?' },
    a: [
      { ru: 'Больше 1000 лет', be: 'Больш за 1000 гадоў', en: 'Over 1,000 years' },
      { ru: 'Около года', be: 'Каля года', en: 'About a year' },
      { ru: '10 лет', be: '10 гадоў', en: '10 years' },
      { ru: 'Месяц', be: 'Месяц', en: 'A month' },
    ],
  },
  {
    q: { ru: 'Что означает цифра 1 в треугольнике на упаковке?', be: 'Што азначае лічба 1 у трохкутніку на ўпакоўцы?', en: 'What does the number 1 in the recycling triangle mean?' },
    a: [
      { ru: 'ПЭТ (PET)', be: 'ПЭТ (PET)', en: 'PET' },
      { ru: 'Полистирол', be: 'Полістырол', en: 'Polystyrene' },
      { ru: 'ПВХ', be: 'ПВХ', en: 'PVC' },
      { ru: 'Стекло', be: 'Шкло', en: 'Glass' },
    ],
  },
  {
    q: { ru: 'Какой транспорт не даёт выбросов CO₂ при езде?', be: 'Які транспарт не дае выкідаў CO₂ падчас язды?', en: 'Which transport emits no CO₂ while riding?' },
    a: [
      { ru: 'Велосипед', be: 'Ровар', en: 'Bicycle' },
      { ru: 'Автобус', be: 'Аўтобус', en: 'Bus' },
      { ru: 'Такси', be: 'Таксі', en: 'Taxi' },
      { ru: 'Самолёт', be: 'Самалёт', en: 'Plane' },
    ],
  },
  {
    q: { ru: 'Куда сдавать использованные батарейки?', be: 'Куды здаваць выкарыстаныя батарэйкі?', en: 'Where should used batteries go?' },
    a: [
      { ru: 'В специальный пункт приёма', be: 'У спецыяльны пункт прыёму', en: 'To a special collection point' },
      { ru: 'В контейнер для металла', be: 'У кантэйнер для металу', en: 'Into the metal bin' },
      { ru: 'В общий мусор', be: 'У агульнае смецце', en: 'Into general waste' },
      { ru: 'В унитаз', be: 'У унітаз', en: 'Down the toilet' },
    ],
  },
  {
    q: { ru: 'Сколько воды утекает, если чистить зубы с открытым краном?', be: 'Колькі вады ўцякае, калі чысціць зубы з адкрытым кранам?', en: 'How much water flows away while brushing with the tap on?' },
    a: [
      { ru: 'До 10 литров', be: 'Да 10 літраў', en: 'Up to 10 litres' },
      { ru: 'Стакан', be: 'Шклянка', en: 'A glass' },
      { ru: '100 литров', be: '100 літраў', en: '100 litres' },
      { ru: 'Нисколько', be: 'Нічога', en: 'None' },
    ],
  },
  {
    q: { ru: 'Какие лампы самые энергоэффективные?', be: 'Якія лямпы самыя энергаэфектыўныя?', en: 'Which light bulbs are the most efficient?' },
    a: [
      { ru: 'Светодиодные', be: 'Святлодыёдныя', en: 'LED' },
      { ru: 'Лампы накаливания', be: 'Лямпы напальвання', en: 'Incandescent' },
      { ru: 'Галогенные', be: 'Галагенавыя', en: 'Halogen' },
      { ru: 'Все одинаковые', be: 'Усе аднолькавыя', en: 'All the same' },
    ],
  },
  {
    q: { ru: 'Что можно положить в компост?', be: 'Што можна пакласці ў кампост?', en: 'What can go into compost?' },
    a: [
      { ru: 'Очистки овощей', be: 'Ачысткі гародніны', en: 'Vegetable peels' },
      { ru: 'Пластиковый пакет', be: 'Пластыкавы пакет', en: 'A plastic bag' },
      { ru: 'Батарейку', be: 'Батарэйку', en: 'A battery' },
      { ru: 'Стекло', be: 'Шкло', en: 'Glass' },
    ],
  },
  {
    q: { ru: 'Какой газ главный виновник глобального потепления?', be: 'Які газ галоўны вінаваты ў глабальным пацяпленні?', en: 'Which gas drives global warming the most?' },
    a: [
      { ru: 'Углекислый газ', be: 'Вуглякіслы газ', en: 'Carbon dioxide' },
      { ru: 'Кислород', be: 'Кісларод', en: 'Oxygen' },
      { ru: 'Азот', be: 'Азот', en: 'Nitrogen' },
      { ru: 'Гелий', be: 'Гелій', en: 'Helium' },
    ],
  },
  {
    q: { ru: 'Что лучше взять в магазин?', be: 'Што лепш узяць у краму?', en: 'What is best to take shopping?' },
    a: [
      { ru: 'Тканевую сумку', be: 'Тканкавую сумку', en: 'A cloth bag' },
      { ru: 'Новый пакет каждый раз', be: 'Новы пакет кожны раз', en: 'A new bag every time' },
      { ru: 'Бумажный пакет на раз', be: 'Папяровы пакет на раз', en: 'A single-use paper bag' },
      { ru: 'Ничего, взять у кассы', be: 'Нічога, узяць каля касы', en: 'Nothing, buy one at the till' },
    ],
  },
  {
    q: { ru: 'Сколько раз можно переплавить алюминиевую банку?', be: 'Колькі разоў можна пераплавіць алюмініевую бляшанку?', en: 'How many times can an aluminium can be recycled?' },
    a: [
      { ru: 'Бесконечно', be: 'Бясконца', en: 'Endlessly' },
      { ru: 'Один раз', be: 'Адзін раз', en: 'Once' },
      { ru: 'Три раза', be: 'Тры разы', en: 'Three times' },
      { ru: 'Нельзя вообще', be: 'Нельга зусім', en: 'Not at all' },
    ],
  },
  {
    q: { ru: 'Какая часть воды на Земле пресная?', be: 'Якая частка вады на Зямлі прэсная?', en: 'How much of Earth’s water is fresh?' },
    a: [
      { ru: 'Около 3%', be: 'Каля 3%', en: 'About 3%' },
      { ru: 'Половина', be: 'Палова', en: 'Half' },
      { ru: '90%', be: '90%', en: '90%' },
      { ru: '25%', be: '25%', en: '25%' },
    ],
  },
  {
    q: { ru: 'Что делать с чеком, если он не нужен?', be: 'Што рабіць з чэкам, калі ён не патрэбны?', en: 'What to do with a receipt you don’t need?' },
    a: [
      { ru: 'Отказаться от печати', be: 'Адмовіцца ад друку', en: 'Decline printing it' },
      { ru: 'Сдать в макулатуру', be: 'Здаць у макулатуру', en: 'Recycle it as paper' },
      { ru: 'Сжечь', be: 'Спаліць', en: 'Burn it' },
      { ru: 'Закопать', be: 'Закапаць', en: 'Bury it' },
    ],
  },
  {
    q: { ru: 'Почему термочек не принимают в макулатуру?', be: 'Чаму тэрмачэк не прымаюць у макулатуру?', en: 'Why can’t thermal receipts be recycled as paper?' },
    a: [
      { ru: 'В нём есть химическое покрытие', be: 'У ім ёсць хімічнае пакрыццё', en: 'It has a chemical coating' },
      { ru: 'Он слишком маленький', be: 'Ён занадта маленькі', en: 'It is too small' },
      { ru: 'Он из пластика', be: 'Ён з пластыку', en: 'It is plastic' },
      { ru: 'Принимают, это миф', be: 'Прымаюць, гэта міф', en: 'They can, it’s a myth' },
    ],
  },
  {
    q: { ru: 'Какой продукт оставляет самый большой углеродный след?', be: 'Які прадукт пакідае самы вялікі вугляродны след?', en: 'Which food has the largest carbon footprint?' },
    a: [
      { ru: 'Говядина', be: 'Ялавічына', en: 'Beef' },
      { ru: 'Картофель', be: 'Бульба', en: 'Potatoes' },
      { ru: 'Яблоки', be: 'Яблыкі', en: 'Apples' },
      { ru: 'Фасоль', be: 'Фасоля', en: 'Beans' },
    ],
  },
  {
    q: { ru: 'Что сэкономит больше всего энергии дома?', be: 'Што зэканоміць больш за ўсё энергіі дома?', en: 'What saves the most energy at home?' },
    a: [
      { ru: 'Утепление окон', be: 'Уцяпленне вокнаў', en: 'Insulating windows' },
      { ru: 'Новый телевизор', be: 'Новы тэлевізар', en: 'A new TV' },
      { ru: 'Открытая форточка зимой', be: 'Адчыненая фортка зімой', en: 'An open window in winter' },
      { ru: 'Свет днём', be: 'Святло днём', en: 'Lights on in daytime' },
    ],
  },
  {
    q: { ru: 'Из 25 переработанных бутылок можно сделать…', be: 'З 25 перапрацаваных бутэлек можна зрабіць…', en: '25 recycled bottles can become…' },
    a: [
      { ru: 'Флисовую кофту', be: 'Флісавую кофту', en: 'A fleece jacket' },
      { ru: 'Велосипед', be: 'Ровар', en: 'A bicycle' },
      { ru: 'Стеклянную вазу', be: 'Шкляную вазу', en: 'A glass vase' },
      { ru: 'Бумагу', be: 'Паперу', en: 'Paper' },
    ],
  },
  {
    q: { ru: 'Куда девать старую одежду в хорошем состоянии?', be: 'Куды падзець старое адзенне ў добрым стане?', en: 'What to do with old clothes in good condition?' },
    a: [
      { ru: 'Отдать или продать', be: 'Аддаць або прадаць', en: 'Give away or sell' },
      { ru: 'Выбросить', be: 'Выкінуць', en: 'Throw away' },
      { ru: 'Сжечь', be: 'Спаліць', en: 'Burn' },
      { ru: 'Положить в бак для пластика', be: 'Пакласці ў бак для пластыку', en: 'Put in the plastic bin' },
    ],
  },
  {
    q: { ru: 'Что такое «апсайклинг»?', be: 'Што такое «апсайклінг»?', en: 'What is “upcycling”?' },
    a: [
      { ru: 'Новая вещь из старой', be: 'Новая рэч са старой', en: 'Making something new from old' },
      { ru: 'Езда на велосипеде в гору', be: 'Язда на ровары ў гару', en: 'Cycling uphill' },
      { ru: 'Сжигание мусора', be: 'Спальванне смецця', en: 'Burning waste' },
      { ru: 'Покупка новых вещей', be: 'Купля новых рэчаў', en: 'Buying new things' },
    ],
  },
  {
    q: { ru: 'Какое дерево — символ Беловежской пущи?', be: 'Якое дрэва — сімвал Белавежскай пушчы?', en: 'Which tree is a symbol of Białowieża Forest?' },
    a: [
      { ru: 'Дуб', be: 'Дуб', en: 'Oak' },
      { ru: 'Пальма', be: 'Пальма', en: 'Palm' },
      { ru: 'Кактус', be: 'Кактус', en: 'Cactus' },
      { ru: 'Баобаб', be: 'Баабаб', en: 'Baobab' },
    ],
  },
  {
    q: { ru: 'Какое животное — символ Беларуси?', be: 'Якая жывёла — сімвал Беларусі?', en: 'Which animal is a symbol of Belarus?' },
    a: [
      { ru: 'Зубр', be: 'Зубр', en: 'Bison' },
      { ru: 'Кенгуру', be: 'Кенгуру', en: 'Kangaroo' },
      { ru: 'Пингвин', be: 'Пінгвін', en: 'Penguin' },
      { ru: 'Жираф', be: 'Жырафа', en: 'Giraffe' },
    ],
  },
  {
    q: { ru: 'Сколько разлагается пластиковый пакет?', be: 'Колькі раскладаецца пластыкавы пакет?', en: 'How long does a plastic bag take to break down?' },
    a: [
      { ru: 'Сотни лет', be: 'Сотні гадоў', en: 'Hundreds of years' },
      { ru: 'Неделю', be: 'Тыдзень', en: 'A week' },
      { ru: 'Год', be: 'Год', en: 'A year' },
      { ru: 'День', be: 'Дзень', en: 'A day' },
    ],
  },
  {
    q: { ru: 'Какая энергия возобновляемая?', be: 'Якая энергія аднаўляльная?', en: 'Which energy is renewable?' },
    a: [
      { ru: 'Солнечная', be: 'Сонечная', en: 'Solar' },
      { ru: 'Угольная', be: 'Вугальная', en: 'Coal' },
      { ru: 'Нефтяная', be: 'Нафтавая', en: 'Oil' },
      { ru: 'Газовая', be: 'Газавая', en: 'Gas' },
    ],
  },
  {
    q: { ru: 'Что нужно сделать с банкой перед сдачей?', be: 'Што трэба зрабіць са слоікам перад здачай?', en: 'What should you do with a jar before recycling?' },
    a: [
      { ru: 'Сполоснуть', be: 'Спаласнуць', en: 'Rinse it' },
      { ru: 'Разбить', be: 'Разбіць', en: 'Smash it' },
      { ru: 'Покрасить', be: 'Пафарбаваць', en: 'Paint it' },
      { ru: 'Заклеить', be: 'Заляпіць', en: 'Seal it' },
    ],
  },
  {
    q: { ru: 'Куда сдать старый телефон?', be: 'Куды здаць стары тэлефон?', en: 'Where should an old phone go?' },
    a: [
      { ru: 'На переработку электроники', be: 'На перапрацоўку электронікі', en: 'To electronics recycling' },
      { ru: 'В бак для бумаги', be: 'У бак для паперы', en: 'Into the paper bin' },
      { ru: 'В общий мусор', be: 'У агульнае смецце', en: 'Into general waste' },
      { ru: 'В компост', be: 'У кампост', en: 'Into compost' },
    ],
  },
  {
    q: { ru: 'Сколько энергии экономит переработка алюминия?', be: 'Колькі энергіі эканоміць перапрацоўка алюмінію?', en: 'How much energy does recycling aluminium save?' },
    a: [
      { ru: 'Около 95%', be: 'Каля 95%', en: 'About 95%' },
      { ru: '5%', be: '5%', en: '5%' },
      { ru: '30%', be: '30%', en: '30%' },
      { ru: 'Нисколько', be: 'Нічога', en: 'None' },
    ],
  },
  {
    q: { ru: 'Зачем выключать зарядку из розетки?', be: 'Навошта выключаць зарадку з разеткі?', en: 'Why unplug a charger?' },
    a: [
      { ru: 'Она тратит энергию впустую', be: 'Яна марнуе энергію', en: 'It wastes energy' },
      { ru: 'Она испортится', be: 'Яна сапсуецца', en: 'It will break' },
      { ru: 'Так требует закон', be: 'Так патрабуе закон', en: 'The law says so' },
      { ru: 'Незачем', be: 'Няма навошта', en: 'No reason' },
    ],
  },
  {
    q: { ru: 'Какой контейнер обычно для стекла?', be: 'Які кантэйнер звычайна для шкла?', en: 'Which bin is usually for glass?' },
    a: [
      { ru: 'Зелёный', be: 'Зялёны', en: 'Green' },
      { ru: 'Синий', be: 'Сіні', en: 'Blue' },
      { ru: 'Жёлтый', be: 'Жоўты', en: 'Yellow' },
      { ru: 'Красный', be: 'Чырвоны', en: 'Red' },
    ],
  },
  {
    q: { ru: 'Что такое «зелёный паспорт» в приложении?', be: 'Што такое «зялёны пашпарт» у праграме?', en: 'What is the “Green Passport” app about?' },
    a: [
      { ru: 'Награды за эко-действия', be: 'Узнагароды за эка-дзеянні', en: 'Rewards for eco actions' },
      { ru: 'Документ для поездок', be: 'Дакумент для паездак', en: 'A travel document' },
      { ru: 'Пропуск в парк', be: 'Пропуск у парк', en: 'A park pass' },
      { ru: 'Лицензия водителя', be: 'Ліцэнзія вадзіцеля', en: 'A driving licence' },
    ],
  },
  {
    q: { ru: 'Какие насекомые опыляют больше всего растений?', be: 'Якія насякомыя апыляюць больш за ўсё раслін?', en: 'Which insects pollinate the most plants?' },
    a: [
      { ru: 'Пчёлы', be: 'Пчолы', en: 'Bees' },
      { ru: 'Комары', be: 'Камары', en: 'Mosquitoes' },
      { ru: 'Тараканы', be: 'Прусакі', en: 'Cockroaches' },
      { ru: 'Муравьи', be: 'Мурашкі', en: 'Ants' },
    ],
  },
  {
    q: { ru: 'Что лучше для планеты?', be: 'Што лепш для планеты?', en: 'What is better for the planet?' },
    a: [
      { ru: 'Многоразовая бутылка', be: 'Шматразовая бутэлька', en: 'A reusable bottle' },
      { ru: 'Вода в пластике каждый день', be: 'Вада ў пластыку кожны дзень', en: 'Bottled water every day' },
      { ru: 'Одноразовые стаканы', be: 'Аднаразовыя шклянкі', en: 'Disposable cups' },
      { ru: 'Трубочки в каждом напитке', be: 'Трубачкі ў кожным напоі', en: 'A straw in every drink' },
    ],
  },
  {
    q: { ru: 'Сколько деревьев спасает тонна макулатуры?', be: 'Колькі дрэў ратуе тона макулатуры?', en: 'How many trees does a tonne of recycled paper save?' },
    a: [
      { ru: 'Около 17', be: 'Каля 17', en: 'About 17' },
      { ru: 'Одно', be: 'Адно', en: 'One' },
      { ru: '500', be: '500', en: '500' },
      { ru: 'Ни одного', be: 'Ніводнага', en: 'None' },
    ],
  },
  {
    q: { ru: 'Какой знак означает, что упаковку можно переработать?', be: 'Які знак азначае, што ўпакоўку можна перапрацаваць?', en: 'Which sign means packaging can be recycled?' },
    a: [
      { ru: 'Три стрелки по кругу', be: 'Тры стрэлкі па крузе', en: 'Three arrows in a loop' },
      { ru: 'Красный крест', be: 'Чырвоны крыж', en: 'A red cross' },
      { ru: 'Звезда', be: 'Зорка', en: 'A star' },
      { ru: 'Молния', be: 'Маланка', en: 'A lightning bolt' },
    ],
  },
  {
    q: { ru: 'Что такое смог?', be: 'Што такое смог?', en: 'What is smog?' },
    a: [
      { ru: 'Загрязнённый воздух', be: 'Забруджанае паветра', en: 'Polluted air' },
      { ru: 'Сильный дождь', be: 'Моцны дождж', en: 'Heavy rain' },
      { ru: 'Вид облаков', be: 'Від аблокаў', en: 'A type of cloud' },
      { ru: 'Морской туман', be: 'Марскі туман', en: 'Sea fog' },
    ],
  },
  {
    q: { ru: 'Когда лучше поливать растения летом?', be: 'Калі лепш паліваць расліны летам?', en: 'When is best to water plants in summer?' },
    a: [
      { ru: 'Утром или вечером', be: 'Раніцай або ўвечары', en: 'Morning or evening' },
      { ru: 'В полдень', be: 'Апоўдні', en: 'At noon' },
      { ru: 'Каждый час', be: 'Кожную гадзіну', en: 'Every hour' },
      { ru: 'Ночью под дождём', be: 'Ноччу пад дажджом', en: 'At night in the rain' },
    ],
  },
  {
    q: { ru: 'Какой вид транспорта самый экологичный для города?', be: 'Які від транспарту самы экалагічны для горада?', en: 'Which city transport is the greenest?' },
    a: [
      { ru: 'Пешком', be: 'Пешшу', en: 'Walking' },
      { ru: 'Личный автомобиль', be: 'Асабісты аўтамабіль', en: 'A private car' },
      { ru: 'Такси', be: 'Таксі', en: 'Taxi' },
      { ru: 'Вертолёт', be: 'Верталёт', en: 'Helicopter' },
    ],
  },
  {
    q: { ru: 'Что такое «экослед» вещи?', be: 'Што такое «эка-след» рэчы?', en: 'What is an item’s “eco footprint”?' },
    a: [
      { ru: 'Её влияние на природу', be: 'Яе ўплыў на прыроду', en: 'Its impact on nature' },
      { ru: 'Её цена', be: 'Яе кошт', en: 'Its price' },
      { ru: 'Её вес', be: 'Яе вага', en: 'Its weight' },
      { ru: 'Её цвет', be: 'Яе колер', en: 'Its colour' },
    ],
  },
  {
    q: { ru: 'Какой мусор нельзя сжигать во дворе?', be: 'Якое смецце нельга паліць у двары?', en: 'Which waste must never be burned outdoors?' },
    a: [
      { ru: 'Любой, особенно пластик', be: 'Ніякае, асабліва пластык', en: 'Any, especially plastic' },
      { ru: 'Только бумагу', be: 'Толькі паперу', en: 'Only paper' },
      { ru: 'Только листья', be: 'Толькі лісце', en: 'Only leaves' },
      { ru: 'Можно всё', be: 'Можна ўсё', en: 'Everything is fine' },
    ],
  },
  {
    q: { ru: 'Зачем сминать бутылки перед сдачей?', be: 'Навошта сціскаць бутэлькі перад здачай?', en: 'Why squash bottles before recycling?' },
    a: [
      { ru: 'Занимают меньше места', be: 'Займаюць менш месца', en: 'They take less space' },
      { ru: 'Так они чище', be: 'Так яны чысцейшыя', en: 'It cleans them' },
      { ru: 'Так требует цвет', be: 'Так патрабуе колер', en: 'Because of the colour' },
      { ru: 'Незачем', be: 'Няма навошта', en: 'No reason' },
    ],
  },
  {
    q: { ru: 'Что съедает больше всего воды в доме?', be: 'Што спажывае больш за ўсё вады ў доме?', en: 'What uses the most water at home?' },
    a: [
      { ru: 'Ванна и душ', be: 'Ванна і душ', en: 'Bath and shower' },
      { ru: 'Чайник', be: 'Чайнік', en: 'The kettle' },
      { ru: 'Аквариум', be: 'Акварыум', en: 'An aquarium' },
      { ru: 'Цветы на окне', be: 'Кветкі на акне', en: 'Window plants' },
    ],
  },
];
