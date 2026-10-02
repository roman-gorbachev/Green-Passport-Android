const COVERS_BASE_URL = 'https://chatroom-85fb8.web.app/covers';

const ecoTips = [
  {
    id: 'plastic_sorting',
    category: 'ARTICLE',
    isDailyTip: true,
    rewardPoints: 15,
    rewardXp: 15,
    mediaUrl: null,
    titles: {
      ru: 'Как правильно сортировать пластик',
      be: 'Як правільна сартаваць пластык',
      en: 'How to sort plastic the right way',
    },
    bodies: {
      ru: `Пластик окружает нас повсюду: бутылки, упаковка, пакеты, контейнеры. Но не весь пластик одинаково хорошо перерабатывается. Чтобы отходы не уехали на полигон, важно научиться читать маркировку и правильно готовить упаковку к сдаче.

## Что означают цифры в треугольнике

На большинстве упаковок есть значок из трёх стрелок с цифрой внутри. Это код типа пластика:

- **1 — PET (ПЭТ):** бутылки от воды и напитков, прозрачные лотки. Перерабатывается почти везде.
- **2 — HDPE (ПНД):** флаконы от шампуня и бытовой химии, крышки, канистры. Принимается хорошо.
- **4 — LDPE (ПВД):** плёнка, пакеты, стретч. Принимают во многих пунктах, но не во всех.
- **5 — PP (ПП):** ведёрки от йогурта, контейнеры для еды, крышки. Перерабатывается.
- **3 — PVC, 6 — PS, 7 — OTHER:** переработать сложно, такие упаковки лучше просто не покупать.

## Как подготовить пластик

- Опустошите упаковку и сполосните её холодной водой — идеальная чистота не нужна.
- Сомните бутылки, чтобы они занимали меньше места.
- Крышки снимите и сдавайте отдельно: часто они сделаны из другого пластика.
- Снимите этикетки, если они легко отходят.

## Куда сдавать

Ищите контейнеры для пластика во дворах и пункты приёма вторсырья — их адреса есть на карте в приложении. Некоторые магазины принимают бутылки и крышки прямо у входа.

**Главное правило:** если не уверены, что упаковку примут, уточните в пункте приёма. Один «чужой» предмет может испортить целую партию вторсырья.`,
      be: `Пластык атачае нас усюды: бутэлькі, упакоўка, пакеты, кантэйнеры. Але не ўвесь пластык аднолькава добра перапрацоўваецца. Каб адходы не трапілі на палігон, важна навучыцца чытаць маркіроўку і правільна рыхтаваць упакоўку да здачы.

## Што азначаюць лічбы ў трохкутніку

На большасці ўпаковак ёсць значок з трох стрэлак з лічбай унутры. Гэта код тыпу пластыку:

- **1 — PET (ПЭТ):** бутэлькі ад вады і напояў, празрыстыя латкі. Перапрацоўваецца амаль усюды.
- **2 — HDPE (ПНЦ):** флаконы ад шампуню і бытавой хіміі, накрыўкі, каністры. Прымаецца добра.
- **4 — LDPE (ПВЦ):** плёнка, пакеты, стрэйч. Прымаюць у многіх пунктах, але не ва ўсіх.
- **5 — PP (ПП):** вядзёркі ад ёгурту, кантэйнеры для ежы, накрыўкі. Перапрацоўваецца.
- **3 — PVC, 6 — PS, 7 — OTHER:** перапрацаваць складана, такія ўпакоўкі лепш проста не купляць.

## Як падрыхтаваць пластык

- Апусташыце ўпакоўку і спаласніце яе халоднай вадой — ідэальная чысціня не патрэбна.
- Сцісніце бутэлькі, каб яны займалі менш месца.
- Накрыўкі зніміце і здавайце асобна: часта яны зроблены з іншага пластыку.
- Зніміце этыкеткі, калі яны лёгка адыходзяць.

## Куды здаваць

Шукайце кантэйнеры для пластыку ў дварах і пункты прыёму другаснай сыравіны — іх адрасы ёсць на карце ў праграме. Некаторыя крамы прымаюць бутэлькі і накрыўкі проста каля ўвахода.

**Галоўнае правіла:** калі не ўпэўнены, што ўпакоўку прымуць, удакладніце ў пункце прыёму. Адзін «чужы» прадмет можа сапсаваць цэлую партыю сыравіны.`,
      en: `Plastic is everywhere: bottles, packaging, bags, containers. But not all plastic recycles equally well. To keep your waste out of landfill, learn to read the labels and prepare packaging properly before you drop it off.

## What the numbers in the triangle mean

Most packaging carries a symbol of three arrows with a number inside. That is the plastic type code:

- **1 — PET:** water and drink bottles, clear trays. Recycled almost everywhere.
- **2 — HDPE:** shampoo and cleaning product bottles, caps, canisters. Widely accepted.
- **4 — LDPE:** film, bags, stretch wrap. Accepted at many drop-off points, but not all.
- **5 — PP:** yoghurt pots, food containers, caps. Recyclable.
- **3 — PVC, 6 — PS, 7 — OTHER:** hard to recycle, so it is best not to buy them at all.

## How to prepare plastic

- Empty the packaging and give it a quick rinse with cold water — it does not have to be spotless.
- Squash bottles so they take up less space.
- Take the caps off and recycle them separately: they are often a different plastic.
- Peel off labels if they come away easily.

## Where to take it

Look for plastic bins in your neighbourhood and recycling drop-off points — you will find them on the map in the app. Some shops accept bottles and caps right at the entrance.

**The golden rule:** if you are not sure an item will be accepted, ask at the drop-off point. One wrong item can spoil a whole batch of recyclables.`,
    },
  },
  {
    id: 'zero_waste_home',
    category: 'ARTICLE',
    isDailyTip: false,
    rewardPoints: 15,
    rewardXp: 15,
    mediaUrl: null,
    titles: {
      ru: '5 привычек для дома без отходов',
      be: '5 звычак для дома без адходаў',
      en: '5 habits for a zero-waste home',
    },
    bodies: {
      ru: `Жить совсем без мусора сложно, но сократить его в два-три раза может каждый. Для этого не нужны большие траты — достаточно нескольких простых привычек.

## 1. Своя сумка и мешочки

Держите тканевую сумку в рюкзаке или у двери. Для овощей и фруктов подойдут лёгкие сетчатые мешочки — они заменят десятки пакетов в месяц.

## 2. Многоразовая бутылка и кружка

Бутылка для воды и термокружка окупаются за пару недель. Многие кофейни делают скидку, если прийти со своей кружкой.

## 3. Покупки без лишней упаковки

- Выбирайте товары на развес и в крупной фасовке.
- Берите продукты в стекле — банки пригодятся для хранения круп.
- Не покупайте то, что быстро станет мусором: одноразовую посуду, салфетки, пробники.

## 4. Еда без потерь

Почти треть продуктов в мире выбрасывается. Составляйте список перед походом в магазин, храните старые продукты ближе к краю полки, а из остатков готовьте супы, запеканки и смузи.

## 5. Ремонт и вторая жизнь вещей

Прежде чем выбросить вещь, подумайте: можно ли её починить, отдать или продать? Одежду принимают в пунктах благотворительности, технику — в сервисы и на переработку.

**Начните с одной привычки** на этой неделе и добавляйте по одной в месяц — так изменения закрепятся надолго.`,
      be: `Жыць зусім без смецця складана, але скараціць яго ў два-тры разы можа кожны. Для гэтага не патрэбны вялікія выдаткі — дастаткова некалькіх простых звычак.

## 1. Свая сумка і мяшэчкі

Трымайце тканкавую сумку ў заплечніку або каля дзвярэй. Для гародніны і садавіны падыдуць лёгкія сеткаватыя мяшэчкі — яны заменяць дзясяткі пакетаў за месяц.

## 2. Шматразовая бутэлька і кубак

Бутэлька для вады і тэрмакубак акупляюцца за пару тыдняў. Многія кавярні робяць зніжку, калі прыйсці са сваім кубкам.

## 3. Пакупкі без лішняй упакоўкі

- Выбірайце тавары на развагу і ў буйной фасоўцы.
- Бярыце прадукты ў шкле — слоікі спатрэбяцца для захоўвання круп.
- Не купляйце тое, што хутка стане смеццем: аднаразовы посуд, сурвэткі, пробнікі.

## 4. Ежа без страт

Амаль трэць прадуктаў у свеце выкідваецца. Складайце спіс перад паходам у краму, захоўвайце старыя прадукты бліжэй да краю паліцы, а з рэшткаў гатуйце супы, запяканкі і смузі.

## 5. Рамонт і другое жыццё рэчаў

Перш чым выкінуць рэч, падумайце: ці можна яе паправіць, аддаць або прадаць? Адзенне прымаюць у пунктах дабрачыннасці, тэхніку — у сэрвісы і на перапрацоўку.

**Пачніце з адной звычкі** на гэтым тыдні і дадавайце па адной у месяц — так змены замацуюцца надоўга.`,
      en: `Living completely waste-free is hard, but anyone can cut their rubbish by half or more. You do not need to spend much — a few simple habits are enough.

## 1. Your own bag and produce pouches

Keep a cloth bag in your backpack or by the door. Light mesh pouches work for fruit and vegetables and replace dozens of plastic bags a month.

## 2. A reusable bottle and cup

A water bottle and a travel mug pay for themselves within a couple of weeks. Many cafés give a discount if you bring your own cup.

## 3. Shopping without extra packaging

- Buy loose goods and larger packs.
- Choose food in glass — the jars are handy for storing grains.
- Skip things that quickly become rubbish: disposable plates, wipes, samples.

## 4. Food without waste

Almost a third of the world's food is thrown away. Write a list before you shop, keep older food at the front of the shelf, and turn leftovers into soups, bakes and smoothies.

## 5. Repair and give things a second life

Before you throw something away, ask: can it be repaired, given away or sold? Charity points take clothes, and repair shops and recyclers take electronics.

**Start with one habit** this week and add one more each month — that is how changes stick.`,
    },
  },
  {
    id: 'carbon_footprint',
    category: 'ARTICLE',
    isDailyTip: false,
    rewardPoints: 20,
    rewardXp: 20,
    mediaUrl: null,
    titles: {
      ru: 'Что такое углеродный след',
      be: 'Што такое вугляродны след',
      en: 'What is a carbon footprint',
    },
    bodies: {
      ru: `Углеродный след — это количество парниковых газов, которое попадает в атмосферу из-за наших действий: поездок, покупок, еды и использования энергии. Его измеряют в килограммах или тоннах CO₂-эквивалента.

## Почему это важно

Парниковые газы удерживают тепло у поверхности Земли. Когда их становится слишком много, климат теплеет: чаще случаются засухи, ливни и аномальная жара. Средний след жителя Европы — около 7–8 тонн CO₂ в год, а чтобы остановить потепление, его нужно сократить в несколько раз.

## Из чего складывается след

- **Транспорт:** автомобиль и особенно самолёт дают самую большую долю.
- **Энергия дома:** отопление, электричество, горячая вода.
- **Еда:** говядина и молочные продукты оставляют гораздо больший след, чем овощи и крупы.
- **Вещи:** производство одежды и электроники требует много энергии и ресурсов.

## Как уменьшить свой след

- Ходите пешком, ездите на велосипеде и общественном транспорте.
- Выключайте свет и технику из розетки, когда они не нужны.
- Добавьте в меню больше растительных блюд.
- Покупайте меньше, но качественнее, и пользуйтесь вещами дольше.
- Сортируйте отходы: переработка экономит энергию на производстве новых материалов.

**Маленькие шаги складываются.** Если каждый житель города откажется от одной поездки на машине в неделю, это сэкономит тысячи тонн CO₂ в год.`,
      be: `Вугляродны след — гэта колькасць парніковых газаў, якая трапляе ў атмасферу з-за нашых дзеянняў: паездак, пакупак, ежы і выкарыстання энергіі. Яго вымяраюць у кілаграмах або тонах CO₂-эквіваленту.

## Чаму гэта важна

Парніковыя газы ўтрымліваюць цяпло каля паверхні Зямлі. Калі іх становіцца занадта шмат, клімат пацяплее: часцей здараюцца засухі, лівені і анамальная спякота. Сярэдні след жыхара Еўропы — каля 7–8 тон CO₂ у год, а каб спыніць пацяпленне, яго трэба скараціць у некалькі разоў.

## З чаго складаецца след

- **Транспарт:** аўтамабіль і асабліва самалёт даюць самую вялікую долю.
- **Энергія дома:** ацяпленне, электрычнасць, гарачая вада.
- **Ежа:** ялавічына і малочныя прадукты пакідаюць значна большы след, чым гародніна і крупы.
- **Рэчы:** вытворчасць адзення і электронікі патрабуе шмат энергіі і рэсурсаў.

## Як паменшыць свой след

- Хадзіце пешшу, ездзіце на ровары і грамадскім транспарце.
- Выключайце святло і тэхніку з разеткі, калі яны не патрэбны.
- Дадайце ў меню больш раслінных страў.
- Купляйце менш, але якасней, і карыстайцеся рэчамі даўжэй.
- Сартуйце адходы: перапрацоўка эканоміць энергію на вытворчасці новых матэрыялаў.

**Маленькія крокі складваюцца.** Калі кожны жыхар горада адмовіцца ад адной паездкі на машыне ў тыдзень, гэта зэканоміць тысячы тон CO₂ у год.`,
      en: `A carbon footprint is the amount of greenhouse gas released into the atmosphere because of what we do: travel, shopping, food and energy use. It is measured in kilograms or tonnes of CO₂ equivalent.

## Why it matters

Greenhouse gases trap heat near the Earth's surface. When there is too much of them, the climate warms: droughts, downpours and heatwaves become more frequent. The average European's footprint is about 7–8 tonnes of CO₂ a year, and to stop warming it has to fall several times over.

## What makes up a footprint

- **Transport:** cars and especially flights are the biggest share.
- **Home energy:** heating, electricity, hot water.
- **Food:** beef and dairy leave a much larger footprint than vegetables and grains.
- **Stuff:** making clothes and electronics takes a lot of energy and resources.

## How to shrink yours

- Walk, cycle and take public transport.
- Switch off lights and unplug devices you are not using.
- Put more plant-based dishes on your menu.
- Buy less but better, and use things for longer.
- Sort your waste: recycling saves the energy needed to make new materials.

**Small steps add up.** If every resident of a city skipped one car trip a week, it would save thousands of tonnes of CO₂ a year.`,
    },
  },
  {
    id: 'glass_recycling',
    category: 'VIDEO',
    isDailyTip: false,
    rewardPoints: 20,
    rewardXp: 20,
    mediaUrl: 'https://www.youtube.com/watch?v=NwHrndFdkVU',
    titles: {
      ru: 'Как перерабатывают стекло',
      be: 'Як перапрацоўваюць шкло',
      en: 'How glass is recycled',
    },
    bodies: {
      ru: `Стекло — один из самых благодарных материалов для переработки: его можно переплавлять бесконечно, и оно не теряет качества. В видео показан путь бутылки от контейнера во дворе до новой банки на полке магазина.

## Что происходит на заводе

- **Сортировка.** Стекло делят по цветам: прозрачное, зелёное и коричневое. Смешивать их нельзя — цвет новой бутылки зависит от сырья.
- **Очистка.** Магниты и вихревые токи убирают металлические крышки, воздух сдувает бумагу и этикетки.
- **Дробление.** Бутылки измельчают в стеклобой — мелкую крошку.
- **Плавка.** Стеклобой смешивают с песком и плавят при температуре около 1500 °C.
- **Формовка.** Из расплава выдувают новые бутылки и банки.

## Почему это выгодно

Каждая тонна стеклобоя экономит около 1,2 тонны сырья и снижает расход энергии на плавку. А на полигоне стеклянная бутылка пролежала бы тысячи лет.

## Как помочь

- Сдавайте только тару: бутылки и банки. Посуду, зеркала, лампочки и оконное стекло в контейнер не кладите.
- Снимите крышки и выплесните остатки.
- Разбитое стекло тоже принимают, если оно чистое.

Посмотрите видео по кнопке ниже, а потом отметьте материал прочитанным, чтобы получить очки.`,
      be: `Шкло — адзін з самых удзячных матэрыялаў для перапрацоўкі: яго можна пераплаўляць бясконца, і яно не губляе якасці. У відэа паказаны шлях бутэлькі ад кантэйнера ў двары да новага слоіка на паліцы крамы.

## Што адбываецца на заводзе

- **Сартаванне.** Шкло дзеляць па колерах: празрыстае, зялёнае і карычневае. Змешваць іх нельга — колер новай бутэлькі залежыць ад сыравіны.
- **Ачыстка.** Магніты і віхравыя токі прыбіраюць металічныя накрыўкі, паветра здзімае паперу і этыкеткі.
- **Драбленне.** Бутэлькі здрабняюць у шклабой — дробную крошку.
- **Плаўка.** Шклабой змешваюць з пяском і плавяць пры тэмпературы каля 1500 °C.
- **Фармоўка.** З расплаву выдзімаюць новыя бутэлькі і слоікі.

## Чаму гэта выгадна

Кожная тона шклабою эканоміць каля 1,2 тоны сыравіны і зніжае расход энергіі на плаўку. А на палігоне шкляная бутэлька праляжала б тысячы гадоў.

## Як дапамагчы

- Здавайце толькі тару: бутэлькі і слоікі. Посуд, люстэркі, лямпачкі і аконнае шкло ў кантэйнер не кладзіце.
- Зніміце накрыўкі і выліце рэшткі.
- Разбітае шкло таксама прымаюць, калі яно чыстае.

Паглядзіце відэа па кнопцы ніжэй, а потым адзначце матэрыял прачытаным, каб атрымаць ачкі.`,
      en: `Glass is one of the most rewarding materials to recycle: it can be melted down again and again without losing quality. The video follows a bottle from a neighbourhood bin to a new jar on a shop shelf.

## What happens at the plant

- **Sorting.** Glass is split by colour: clear, green and brown. They cannot be mixed — the colour of a new bottle depends on the raw material.
- **Cleaning.** Magnets and eddy currents remove metal caps, and air jets blow away paper and labels.
- **Crushing.** Bottles are ground into cullet — small glass crumbs.
- **Melting.** Cullet is mixed with sand and melted at about 1500 °C.
- **Forming.** New bottles and jars are blown from the molten glass.

## Why it pays off

Every tonne of cullet saves about 1.2 tonnes of raw materials and cuts the energy needed for melting. In a landfill, a glass bottle would lie for thousands of years.

## How you can help

- Only recycle containers: bottles and jars. Keep dishes, mirrors, light bulbs and window glass out of the bin.
- Take off the caps and pour out what is left.
- Broken glass is accepted too, as long as it is clean.

Watch the video with the button below, then mark the material as read to earn points.`,
    },
  },
  {
    id: 'bottle_journey',
    category: 'VIDEO',
    isDailyTip: false,
    rewardPoints: 20,
    rewardXp: 20,
    mediaUrl: 'https://www.youtube.com/watch?v=3SnbqMd2-tE',
    titles: {
      ru: 'Путешествие пластиковой бутылки',
      be: 'Падарожжа пластыкавай бутэлькі',
      en: 'The journey of a plastic bottle',
    },
    bodies: {
      ru: `Что происходит с бутылкой после того, как вы бросили её в жёлтый контейнер? В видео показан полный путь ПЭТ-бутылки на перерабатывающем заводе — от тюка вторсырья до новой бутылки.

## Этапы переработки

- **Сбор и прессовка.** Бутылки из контейнеров спрессовывают в тюки, чтобы их было удобно везти.
- **Сортировка.** На конвейере бутылки разделяют по цвету и отбирают посторонний мусор. Помогают оптические сканеры.
- **Измельчение и мойка.** Бутылки режут на хлопья и отмывают от клея, этикеток и остатков напитков.
- **Гранулирование.** Чистые хлопья плавят и превращают в гранулы — сырьё для новых изделий.
- **Новая жизнь.** Из гранул делают новые бутылки, волокно для одежды и ковров, плёнку и упаковку.

## Интересные факты

- Из 20–25 бутылок получается волокно для одной флисовой кофты.
- Переработка ПЭТ требует примерно на 60% меньше энергии, чем производство нового пластика.
- Без переработки бутылка разлагается в природе сотни лет.

## Как сделать путь бутылки короче

Сминайте бутылки, снимайте крышки и сдавайте их отдельно. А лучше — пользуйтесь многоразовой бутылкой: самая экологичная бутылка та, которую не пришлось производить.`,
      be: `Што адбываецца з бутэлькай пасля таго, як вы кінулі яе ў жоўты кантэйнер? У відэа паказаны поўны шлях ПЭТ-бутэлькі на перапрацоўчым заводзе — ад цюка другаснай сыравіны да новай бутэлькі.

## Этапы перапрацоўкі

- **Збор і прасоўка.** Бутэлькі з кантэйнераў спрасоўваюць у цюкі, каб іх было зручна везці.
- **Сартаванне.** На канвееры бутэлькі падзяляюць па колеры і адбіраюць старонняе смецце. Дапамагаюць аптычныя сканеры.
- **Здрабненне і мыццё.** Бутэлькі рэжуць на шматкі і адмываюць ад клею, этыкетак і рэштак напояў.
- **Грануляванне.** Чыстыя шматкі плавяць і ператвараюць у гранулы — сыравіну для новых вырабаў.
- **Новае жыццё.** З гранул робяць новыя бутэлькі, валакно для адзення і дываноў, плёнку і ўпакоўку.

## Цікавыя факты

- З 20–25 бутэлек атрымліваецца валакно для адной фліснай кофты.
- Перапрацоўка ПЭТ патрабуе прыкладна на 60% менш энергіі, чым вытворчасць новага пластыку.
- Без перапрацоўкі бутэлька раскладаецца ў прыродзе сотні гадоў.

## Як зрабіць шлях бутэлькі карацейшым

Сціскайце бутэлькі, здымайце накрыўкі і здавайце іх асобна. А лепш — карыстайцеся шматразовай бутэлькай: самая экалагічная бутэлька тая, якую не давялося вырабляць.`,
      en: `What happens to a bottle after you drop it into the recycling bin? The video shows the full journey of a PET bottle through a recycling plant — from a bale of recyclables to a brand-new bottle.

## The stages

- **Collection and baling.** Bottles from the bins are pressed into bales so they are easy to transport.
- **Sorting.** On the conveyor, bottles are split by colour and other rubbish is removed, with help from optical scanners.
- **Shredding and washing.** Bottles are cut into flakes and washed free of glue, labels and leftover drinks.
- **Pelletising.** Clean flakes are melted and turned into pellets — the raw material for new products.
- **A new life.** The pellets become new bottles, fibre for clothes and carpets, film and packaging.

## Fun facts

- 20–25 bottles make enough fibre for one fleece jacket.
- Recycling PET takes about 60% less energy than making new plastic.
- Without recycling, a bottle takes hundreds of years to break down in nature.

## How to shorten the journey

Squash bottles, take off the caps and recycle them separately. Better still, use a reusable bottle: the greenest bottle is the one that never had to be made.`,
    },
  },
  {
    id: 'save_water',
    category: 'KIDS',
    isDailyTip: false,
    rewardPoints: 10,
    rewardXp: 10,
    mediaUrl: null,
    titles: {
      ru: 'Почему нужно беречь воду',
      be: 'Чаму трэба берагчы ваду',
      en: 'Why we should save water',
    },
    bodies: {
      ru: `Кажется, что воды на Земле очень много: моря, океаны, реки и озёра. Но почти вся она солёная! Пресной воды, которую можно пить, всего около трёх капель из ведра.

## Откуда берётся вода в кране

Вода из рек и подземных источников попадает на станцию очистки. Там её фильтруют и обеззараживают, а потом по трубам отправляют в каждый дом. На это тратится много труда и электричества.

## Куда утекает вода

- Пока ты чистишь зубы с открытым краном, утекает до 10 литров — целое ведро!
- Протекающий кран теряет за сутки столько воды, сколько хватило бы, чтобы принять ванну.
- Пятиминутный душ расходует в три раза меньше воды, чем ванна.

## Как стать хранителем воды

- Закрывай кран, пока чистишь зубы или намыливаешь руки.
- Скажи взрослым, если увидишь, что кран капает.
- Принимай душ вместо ванны.
- Поливай цветы водой, которая осталась после мытья овощей.
- Не бросай мусор в реки и озёра — так вода останется чистой для рыб и птиц.

**Ты тоже можешь помочь планете!** Попробуй сегодня выполнить хотя бы два правила и расскажи о них друзьям.`,
      be: `Здаецца, што вады на Зямлі вельмі шмат: моры, акіяны, рэкі і азёры. Але амаль уся яна салёная! Прэснай вады, якую можна піць, усяго каля трох кропель з вядра.

## Адкуль бярэцца вада ў кране

Вада з рэк і падземных крыніц трапляе на станцыю ачысткі. Там яе фільтруюць і абеззаражваюць, а потым па трубах адпраўляюць у кожны дом. На гэта траціцца шмат працы і электрычнасці.

## Куды ўцякае вада

- Пакуль ты чысціш зубы з адкрытым кранам, уцякае да 10 літраў — цэлае вядро!
- Кран, які капае, губляе за суткі столькі вады, колькі хапіла б, каб прыняць ванну.
- Пяціхвілінны душ расходуе ў тры разы менш вады, чым ванна.

## Як стаць ахоўнікам вады

- Закрывай кран, пакуль чысціш зубы або намыльваеш рукі.
- Скажы дарослым, калі ўбачыш, што кран капае.
- Прымай душ замест ванны.
- Палівай кветкі вадой, якая засталася пасля мыцця гародніны.
- Не кідай смецце ў рэкі і азёры — так вада застанецца чыстай для рыб і птушак.

**Ты таксама можаш дапамагчы планеце!** Паспрабуй сёння выканаць хоць бы два правілы і раскажы пра іх сябрам.`,
      en: `It seems like there is a lot of water on Earth: seas, oceans, rivers and lakes. But almost all of it is salty! Fresh water you can drink is only about three drops out of a whole bucket.

## Where tap water comes from

Water from rivers and underground springs goes to a treatment plant. There it is filtered and disinfected, then sent through pipes to every home. That takes a lot of work and electricity.

## Where water leaks away

- While you brush your teeth with the tap running, up to 10 litres flow away — a whole bucket!
- A dripping tap loses enough water in a day to fill a bath.
- A five-minute shower uses three times less water than a bath.

## How to become a water guardian

- Turn off the tap while you brush your teeth or soap your hands.
- Tell a grown-up if you see a dripping tap.
- Take a shower instead of a bath.
- Water plants with the water left over from washing vegetables.
- Never throw rubbish into rivers and lakes — that keeps the water clean for fish and birds.

**You can help the planet too!** Try following at least two rules today and tell your friends about them.`,
    },
  },
  {
    id: 'trash_gnome_tale',
    category: 'KIDS',
    isDailyTip: false,
    rewardPoints: 10,
    rewardXp: 10,
    mediaUrl: null,
    titles: {
      ru: 'Сказка про мусорного гнома',
      be: 'Казка пра смеццевага гнома',
      en: 'The tale of the litter gnome',
    },
    bodies: {
      ru: `В одном зелёном лесу, под старым замшелым пнём, жил маленький гном по имени Сортик. У него была синяя шапка, большая корзина и очень доброе сердце.

Однажды утром Сортик вышел на поляну и ахнул: повсюду валялись бутылки, бумажки и консервные банки. Это туристы устроили пикник и забыли за собой убрать.

— Ой-ой-ой, — пискнул ёжик, уколовшись о жестяную банку.
— Кхе-кхе, — закашлялась белка, запутавшись в пакете.

## Сортик берётся за дело

Гном не растерялся. Он собрал всех зверей и сказал:
— Друзья, мусор — это не беда, если знать, что с ним делать! Давайте разложим его по корзинкам.

- **Зайцы** собирали бумагу — из неё сделают новые тетради.
- **Белки** носили пластиковые бутылки — из них сошьют тёплые кофты.
- **Ежи** катили стеклянные банки — их переплавят в новую посуду.
- **Медвежонок** складывал жестяные банки — из них получатся велосипеды.

## Чистый лес

К вечеру поляна засияла чистотой. Птицы снова запели, а цветы подняли головки к солнцу. Звери так обрадовались, что устроили праздник с чаем из лесных трав.

— Запомните, — сказал на прощание Сортик, — мусор, сданный правильно, становится новой полезной вещью. А лучше всего — вовсе не мусорить!

**А ты знаешь, в какую корзинку положить старую газету?** Попробуй сыграть в игру «Сортировочный конвейер» и проверь себя!`,
      be: `У адным зялёным лесе, пад старым імшыстым пнём, жыў маленькі гном па імені Сорцік. У яго была сіняя шапка, вялікі кош і вельмі добрае сэрца.

Аднойчы раніцай Сорцік выйшаў на паляну і ахнуў: усюды валяліся бутэлькі, паперкі і бляшанкі. Гэта турысты зладзілі пікнік і забыліся прыбраць за сабой.

— Ой-ой-ой, — піскнуў вожык, укалоўшыся аб бляшанку.
— Кхе-кхе, — закашлялася вавёрка, заблытаўшыся ў пакеце.

## Сорцік бярэцца за справу

Гном не разгубіўся. Ён сабраў усіх звяроў і сказаў:
— Сябры, смецце — гэта не бяда, калі ведаць, што з ім рабіць! Давайце раскладзём яго па кошыках.

- **Зайцы** збіралі паперу — з яе зробяць новыя сшыткі.
- **Вавёркі** насілі пластыкавыя бутэлькі — з іх пашыюць цёплыя кофты.
- **Вожыкі** каталі шкляныя слоікі — іх пераплавяць у новы посуд.
- **Мядзведзік** складваў бляшанкі — з іх атрымаюцца ровары.

## Чысты лес

Да вечара паляна заззяла чысцінёй. Птушкі зноў заспявалі, а кветкі паднялі галоўкі да сонца. Звяры так узрадаваліся, што наладзілі свята з гарбатай з лясных траў.

— Запомніце, — сказаў на развітанне Сорцік, — смецце, зданае правільна, становіцца новай карыснай рэччу. А лепш за ўсё — зусім не смеціць!

**А ты ведаеш, у які кошык пакласці старую газету?** Паспрабуй згуляць у гульню «Сартавальны канвеер» і правер сябе!`,
      en: `In a green forest, under an old mossy stump, lived a little gnome called Sorty. He had a blue hat, a big basket and a very kind heart.

One morning Sorty walked out into the clearing and gasped: bottles, scraps of paper and tin cans were scattered everywhere. Some campers had had a picnic and forgotten to tidy up.

"Ouch, ouch, ouch," squeaked the hedgehog, pricking himself on a tin can.
"Cough, cough," spluttered the squirrel, tangled in a plastic bag.

## Sorty gets to work

The gnome did not panic. He gathered all the animals and said:
"Friends, rubbish is no trouble if you know what to do with it! Let's sort it into baskets."

- **The hares** gathered paper — it will become new notebooks.
- **The squirrels** carried plastic bottles — they will be made into warm jumpers.
- **The hedgehogs** rolled glass jars — they will be melted into new dishes.
- **The little bear** stacked tin cans — they will turn into bicycles.

## A clean forest

By evening the clearing was sparkling clean. The birds sang again and the flowers lifted their heads to the sun. The animals were so happy that they threw a party with forest-herb tea.

"Remember," said Sorty as he waved goodbye, "rubbish that is sorted properly becomes something new and useful. And best of all — don't drop litter at all!"

**Do you know which basket an old newspaper goes in?** Play "Sorting line" and test yourself!`,
    },
  },
  {
    id: 'fast_fashion',
    category: 'ARTICLE',
    isDailyTip: false,
    rewardPoints: 15,
    rewardXp: 15,
    mediaUrl: null,
    titles: {
      ru: 'Одежда и планета: что такое быстрая мода',
      be: 'Адзенне і планета: што такое хуткая мода',
      en: 'Clothes and the planet: what is fast fashion',
    },
    bodies: {
      ru: `Быстрая мода — это дешёвая одежда, которую выпускают новыми коллекциями почти каждую неделю. Её покупают часто, носят мало и быстро выбрасывают.

## Почему это проблема

- На одну хлопковую футболку уходит около 2 700 литров воды.
- Синтетические ткани при стирке теряют микроволокна, которые попадают в реки.
- Большая часть старой одежды оказывается на свалке, хотя её можно носить или переработать.

## Что можно сделать

- **Покупайте меньше, но лучше.** Перед покупкой спросите себя, будете ли вы носить вещь хотя бы 30 раз.
- **Чините.** Пришить пуговицу или поменять молнию дешевле, чем купить новую вещь.
- **Меняйтесь.** Своп-вечеринки и обмен с друзьями обновляют гардероб бесплатно.
- **Отдавайте.** Вещи в хорошем состоянии принимают благотворительные магазины и пункты сбора одежды.

**Самая экологичная вещь — та, что уже висит в вашем шкафу.**`,
      be: `Хуткая мода — гэта танная вопратка, якую выпускаюць новымі калекцыямі амаль кожны тыдзень. Яе купляюць часта, носяць мала і хутка выкідваюць.

## Чаму гэта праблема

- На адну баваўняную футболку ідзе каля 2 700 літраў вады.
- Сінтэтычныя тканіны пры мыцці губляюць мікравалокны, якія трапляюць у рэкі.
- Большая частка старога адзення апынаецца на звалцы, хоць яго можна насіць або перапрацаваць.

## Што можна зрабіць

- **Купляйце менш, але лепш.** Перад пакупкай спытайце сябе, ці будзеце вы насіць рэч хоць бы 30 разоў.
- **Рамантуйце.** Прышыць гузік або памяняць маланку танней, чым купіць новую рэч.
- **Мяняйцеся.** Своп-вечарыны і абмен з сябрамі абнаўляюць гардэроб бясплатна.
- **Аддавайце.** Рэчы ў добрым стане прымаюць дабрачынныя крамы і пункты збору адзення.

**Самая экалагічная рэч — тая, што ўжо вісіць у вашай шафе.**`,
      en: `Fast fashion is cheap clothing released in new collections almost every week. People buy it often, wear it little and throw it away quickly.

## Why it is a problem

- One cotton T-shirt takes about 2,700 litres of water to make.
- Synthetic fabrics shed microfibres in the wash, and they end up in rivers.
- Most old clothes go to landfill, even though they could be worn again or recycled.

## What you can do

- **Buy less, but better.** Before buying, ask yourself whether you will wear it at least 30 times.
- **Repair.** Sewing on a button or replacing a zip costs less than something new.
- **Swap.** Swap parties and trading with friends refresh your wardrobe for free.
- **Donate.** Charity shops and clothing banks take items in good condition.

**The greenest piece of clothing is the one already in your wardrobe.**`,
    },
  },
  {
    id: 'home_energy',
    category: 'ARTICLE',
    isDailyTip: false,
    rewardPoints: 15,
    rewardXp: 15,
    mediaUrl: null,
    titles: {
      ru: '10 способов сэкономить электричество дома',
      be: '10 спосабаў зэканоміць электрычнасць дома',
      en: '10 ways to save electricity at home',
    },
    bodies: {
      ru: `Экономия энергии — это меньше выбросов CO₂ и меньше счета за свет. Вот простые шаги, которые не требуют ремонта.

## Свет

- Замените лампы накаливания на светодиодные: они тратят в 4–5 раз меньше энергии.
- Выключайте свет, выходя из комнаты.
- Днём пользуйтесь естественным освещением.

## Техника

- Выключайте зарядки и телевизор из розетки: в режиме ожидания они тоже потребляют ток.
- Загружайте стиральную и посудомоечную машину полностью.
- Стирайте при 30–40 °C — большинство вещей отстирывается так же хорошо.
- Не ставьте холодильник рядом с плитой или батареей.

## Тепло и вода

- Кипятите в чайнике ровно столько воды, сколько нужно.
- Закрывайте кастрюлю крышкой — вода закипит быстрее.
- Зимой проветривайте коротко и интенсивно, а не держите форточку открытой весь день.

**Совет:** выберите два пункта из списка и попробуйте их неделю — вы удивитесь, как быстро это войдёт в привычку.`,
      be: `Эканомія энергіі — гэта менш выкідаў CO₂ і меншыя рахункі за святло. Вось простыя крокі, якія не патрабуюць рамонту.

## Святло

- Замяніце лямпы напальвання на святлодыёдныя: яны марнуюць у 4–5 разоў менш энергіі.
- Выключайце святло, выходзячы з пакоя.
- Удзень карыстайцеся натуральным асвятленнем.

## Тэхніка

- Выключайце зарадкі і тэлевізар з разеткі: у рэжыме чакання яны таксама спажываюць ток.
- Загружайце пральную і пасудамыйную машыну цалкам.
- Мыйце пры 30–40 °C — большасць рэчаў адмываецца гэтак жа добра.
- Не стаўце халадзільнік побач з плітой або батарэяй.

## Цяпло і вада

- Кіпяціце ў чайніку роўна столькі вады, колькі трэба.
- Накрывайце рондаль накрыўкай — вада закіпіць хутчэй.
- Зімой праветрывайце коратка і інтэнсіўна, а не трымайце фортку адчыненай увесь дзень.

**Парада:** выберыце два пункты са спіса і паспрабуйце іх тыдзень — вы здзівіцеся, як хутка гэта ўвойдзе ў звычку.`,
      en: `Saving energy means fewer CO₂ emissions and smaller electricity bills. Here are simple steps that need no renovation.

## Lighting

- Replace incandescent bulbs with LEDs: they use 4–5 times less energy.
- Switch off the light when you leave a room.
- Use daylight during the day.

## Appliances

- Unplug chargers and the TV: on standby they still draw power.
- Run the washing machine and dishwasher only when full.
- Wash at 30–40 °C — most clothes come out just as clean.
- Keep the fridge away from the cooker and radiators.

## Heat and water

- Boil only as much water in the kettle as you need.
- Put a lid on the pot — water boils faster.
- In winter, air rooms briefly and fully instead of leaving a window open all day.

**Tip:** pick two items from this list and try them for a week — you will be surprised how fast they become habits.`,
    },
  },
  {
    id: 'food_waste',
    category: 'ARTICLE',
    isDailyTip: false,
    rewardPoints: 15,
    rewardXp: 15,
    mediaUrl: null,
    titles: {
      ru: 'Как не выбрасывать еду',
      be: 'Як не выкідваць ежу',
      en: 'How to stop wasting food',
    },
    bodies: {
      ru: `Около трети всей еды в мире выбрасывается. Вместе с ней пропадают вода, энергия и труд, которые ушли на её выращивание и доставку.

## Перед магазином

- Загляните в холодильник и составьте список.
- Планируйте меню на несколько дней вперёд.
- Не ходите за продуктами голодными — так проще удержаться от лишних покупок.

## Дома

- Ставьте новые продукты назад, а старые — вперёд.
- «Годен до» и «Лучше употребить до» — не одно и то же: после второй даты продукт часто ещё съедобен.
- Замораживайте хлеб, зелень и остатки блюд, которые не успеваете съесть.

## Остатки — в дело

- Из вчерашних овощей получится суп или запеканка.
- Подсохший хлеб — это гренки и сухари.
- Переспелые бананы отлично подходят для смузи и выпечки.

**То, что всё же не съели,** можно отправить в компост: очистки станут удобрением, а не мусором.`,
      be: `Каля трэці ўсёй ежы ў свеце выкідваецца. Разам з ёй прападаюць вада, энергія і праца, якія пайшлі на яе вырошчванне і дастаўку.

## Перад крамай

- Зазірніце ў халадзільнік і складзіце спіс.
- Плануйце меню на некалькі дзён наперад.
- Не хадзіце па прадукты галоднымі — так прасцей устрымацца ад лішніх пакупак.

## Дома

- Стаўце новыя прадукты назад, а старыя — наперад.
- «Прыдатны да» і «Лепш спажыць да» — не адно і тое ж: пасля другой даты прадукт часта яшчэ ядомы.
- Замарожвайце хлеб, зеляніну і рэшткі страў, якія не паспяваеце з’есці.

## Рэшткі — у справу

- З учорашняй гародніны атрымаецца суп або запяканка.
- Падсохлы хлеб — гэта грэнкі і сухары.
- Перасталыя бананы выдатна падыходзяць для смузі і выпечкі.

**Тое, што ўсё ж не з’елі,** можна адправіць у кампост: ачысткі стануць угнаеннем, а не смеццем.`,
      en: `About a third of all food in the world is thrown away. Along with it go the water, energy and work it took to grow and deliver it.

## Before you shop

- Check the fridge and write a list.
- Plan meals a few days ahead.
- Don’t shop hungry — it makes it easier to skip extra buys.

## At home

- Put new food at the back and older food at the front.
- “Use by” and “Best before” are not the same: after the second date, food is often still fine.
- Freeze bread, herbs and leftovers you won’t finish in time.

## Put leftovers to work

- Yesterday’s vegetables make a soup or a bake.
- Stale bread becomes croutons and breadcrumbs.
- Overripe bananas are perfect for smoothies and baking.

**Whatever you don’t eat** can go into compost: peels become fertiliser instead of rubbish.`,
    },
  },
  {
    id: 'hazardous_waste',
    category: 'ARTICLE',
    isDailyTip: false,
    rewardPoints: 20,
    rewardXp: 20,
    mediaUrl: null,
    titles: {
      ru: 'Опасные отходы: батарейки, лампы и градусники',
      be: 'Небяспечныя адходы: батарэйкі, лямпы і градуснікі',
      en: 'Hazardous waste: batteries, lamps and thermometers',
    },
    bodies: {
      ru: `Некоторые вещи нельзя выбрасывать в обычный контейнер: они содержат вещества, опасные для почвы, воды и здоровья.

## Что относится к опасным отходам

- **Батарейки и аккумуляторы** — в них есть тяжёлые металлы.
- **Энергосберегающие и люминесцентные лампы** — внутри ртуть.
- **Ртутные градусники.**
- **Старые лекарства, краски, растворители и бытовая химия.**

## Куда их сдавать

- Батарейки принимают специальные боксы во многих магазинах и торговых центрах.
- Лампы и градусники — в пункты приёма ртутьсодержащих отходов; адреса подскажет местное ЖКХ.
- Технику с аккумуляторами — в пункты приёма электроники.

## Как хранить до сдачи

- Соберите батарейки в отдельную коробку или банку.
- Лампы храните целыми, в упаковке, чтобы они не разбились.
- Если градусник разбился, проветрите комнату, соберите ртуть в банку с водой и обратитесь в МЧС.

**Даже одна батарейка** на свалке со временем отдаёт тяжёлые металлы почве и воде, поэтому каждая сданная батарейка действительно важна.`,
      be: `Некаторыя рэчы нельга выкідваць у звычайны кантэйнер: яны змяшчаюць рэчывы, небяспечныя для глебы, вады і здароўя.

## Што адносіцца да небяспечных адходаў

- **Батарэйкі і акумулятары** — у іх ёсць цяжкія металы.
- **Энергазберагальныя і люмінесцэнтныя лямпы** — унутры ртуць.
- **Ртутныя градуснікі.**
- **Старыя лекі, фарбы, растваральнікі і бытавая хімія.**

## Куды іх здаваць

- Батарэйкі прымаюць спецыяльныя боксы ў многіх крамах і гандлёвых цэнтрах.
- Лямпы і градуснікі — у пункты прыёму ртуцьзмяшчальных адходаў; адрасы падкажа мясцовая ЖКГ.
- Тэхніку з акумулятарамі — у пункты прыёму электронікі.

## Як захоўваць да здачы

- Збярыце батарэйкі ў асобную каробку або слоік.
- Лямпы захоўвайце цэлымі, ва ўпакоўцы, каб яны не разбіліся.
- Калі градуснік разбіўся, праветрыце пакой, збярыце ртуць у слоік з вадой і звярніцеся ў МНС.

**Нават адна батарэйка** на звалцы з часам аддае цяжкія металы глебе і вадзе, таму кожная зданая батарэйка сапраўды важная.`,
      en: `Some things must never go into a regular bin: they contain substances that harm soil, water and health.

## What counts as hazardous waste

- **Batteries** — they contain heavy metals.
- **Energy-saving and fluorescent lamps** — they contain mercury.
- **Mercury thermometers.**
- **Old medicines, paints, solvents and household chemicals.**

## Where to take them

- Many shops and malls have special battery collection boxes.
- Lamps and thermometers go to collection points for mercury waste; your local utility can tell you where.
- Devices with built-in batteries go to electronics collection points.

## How to store them until then

- Keep batteries in a separate box or jar.
- Keep lamps whole and in their packaging so they don’t break.
- If a thermometer breaks, air the room, collect the mercury in a jar of water and call the emergency services.

**Even one battery** in a landfill slowly releases heavy metals into soil and water, so every battery you recycle really matters.`,
    },
  },
  {
    id: 'city_cycling',
    category: 'ARTICLE',
    isDailyTip: false,
    rewardPoints: 15,
    rewardXp: 15,
    mediaUrl: null,
    titles: {
      ru: 'Велосипед в городе: с чего начать',
      be: 'Ровар у горадзе: з чаго пачаць',
      en: 'Cycling in the city: how to start',
    },
    bodies: {
      ru: `Велосипед не выбрасывает CO₂, экономит деньги на проезд и заменяет тренировку. Вот что поможет пересесть на него без стресса.

## Подготовка

- Проверьте тормоза, давление в шинах и цепь перед первой поездкой.
- Купите шлем и фонари: белый спереди, красный сзади.
- Носите яркую одежду или светоотражающие элементы.

## Маршрут

- Выбирайте велодорожки и тихие улицы, даже если путь немного длиннее.
- Проедьте маршрут в выходной день, чтобы узнать его заранее.
- Если дорога длинная, комбинируйте велосипед с общественным транспортом.

## Безопасность

- Показывайте рукой, куда поворачиваете.
- Держитесь правого края и не обгоняйте справа.
- Будьте внимательны у припаркованных машин: дверь может открыться внезапно.

**Начните с одной поездки в неделю** — например, в магазин или к друзьям. Через месяц велосипед станет привычным транспортом.`,
      be: `Ровар не выкідае CO₂, эканоміць грошы на праезд і замяняе трэніроўку. Вось што дапаможа перасесці на яго без стрэсу.

## Падрыхтоўка

- Праверце тармазы, ціск у шынах і ланцуг перад першай паездкай.
- Купіце шлем і ліхтары: белы спераду, чырвоны ззаду.
- Насіце яркае адзенне або святлоадбівальныя элементы.

## Маршрут

- Выбірайце веласцежкі і ціхія вуліцы, нават калі шлях крыху даўжэйшы.
- Праедзьце маршрут у выхадны дзень, каб даведацца яго загадзя.
- Калі дарога доўгая, камбінуйце ровар з грамадскім транспартам.

## Бяспека

- Паказвайце рукой, куды паварочваеце.
- Трымайцеся правага краю і не абганяйце справа.
- Будзьце ўважлівыя каля прыпаркаваных машын: дзверы могуць адчыніцца раптоўна.

**Пачніце з адной паездкі на тыдзень** — напрыклад, у краму або да сяброў. Праз месяц ровар стане звыклым транспартам.`,
      en: `A bike emits no CO₂, saves money on fares and doubles as exercise. Here is how to switch without stress.

## Getting ready

- Check the brakes, tyre pressure and chain before your first ride.
- Get a helmet and lights: white at the front, red at the back.
- Wear bright clothes or reflective details.

## Your route

- Choose bike lanes and quiet streets, even if the way is a little longer.
- Ride the route on a weekend first to get to know it.
- For long trips, combine the bike with public transport.

## Staying safe

- Signal turns with your arm.
- Keep to the right edge and don’t overtake on the right.
- Watch out near parked cars: a door can open suddenly.

**Start with one ride a week** — to the shop or to see friends. In a month, the bike will feel like normal transport.`,
    },
  },
  {
    id: 'microplastics',
    category: 'ARTICLE',
    isDailyTip: false,
    rewardPoints: 20,
    rewardXp: 20,
    mediaUrl: null,
    titles: {
      ru: 'Микропластик: невидимый мусор',
      be: 'Мікрапластык: нябачнае смецце',
      en: 'Microplastics: invisible litter',
    },
    bodies: {
      ru: `Микропластик — это частицы пластика меньше 5 мм. Их находят в реках, морях, почве, воздухе и даже в питьевой воде.

## Откуда он берётся

- Пакеты и бутылки на солнце и в воде распадаются на крошечные кусочки.
- Синтетическая одежда теряет волокна при каждой стирке.
- Шины автомобилей стираются об асфальт.
- В некоторой косметике есть пластиковые микрогранулы.

## Чем он опасен

Рыбы, птицы и другие животные принимают частицы за еду. Пластик накапливается в пищевых цепочках, а учёные продолжают изучать, как он влияет на здоровье человека.

## Что можно сделать

- Отказывайтесь от одноразового пластика: пакетов, трубочек, стаканчиков.
- Стирайте синтетику реже и при низкой температуре, используйте специальные мешки для стирки.
- Выбирайте одежду из натуральных тканей.
- Сдавайте пластик на переработку, чтобы он не попал в природу.

**Чем меньше пластика выбрасывается сегодня,** тем меньше микропластика будет в природе завтра.`,
      be: `Мікрапластык — гэта часціцы пластыку меншыя за 5 мм. Іх знаходзяць у рэках, морах, глебе, паветры і нават у пітной вадзе.

## Адкуль ён бярэцца

- Пакеты і бутэлькі на сонцы і ў вадзе распадаюцца на малюсенькія кавалачкі.
- Сінтэтычнае адзенне губляе валокны пры кожным мыцці.
- Шыны аўтамабіляў сціраюцца аб асфальт.
- У некаторай касметыцы ёсць пластыкавыя мікрагранулы.

## Чым ён небяспечны

Рыбы, птушкі і іншыя жывёлы прымаюць часціцы за ежу. Пластык назапашваецца ў харчовых ланцужках, а навукоўцы працягваюць вывучаць, як ён уплывае на здароўе чалавека.

## Што можна зрабіць

- Адмаўляйцеся ад аднаразовага пластыку: пакетаў, трубачак, шкляначак.
- Мыйце сінтэтыку радзей і пры нізкай тэмпературы, выкарыстоўвайце спецыяльныя мяшкі для мыцця.
- Выбірайце адзенне з натуральных тканін.
- Здавайце пластык на перапрацоўку, каб ён не трапіў у прыроду.

**Чым менш пластыку выкідваецца сёння,** тым менш мікрапластыку будзе ў прыродзе заўтра.`,
      en: `Microplastics are plastic particles smaller than 5 mm. They turn up in rivers, seas, soil, air and even drinking water.

## Where they come from

- Bags and bottles break down into tiny pieces in sunlight and water.
- Synthetic clothes shed fibres with every wash.
- Car tyres wear down on asphalt.
- Some cosmetics contain plastic microbeads.

## Why they are harmful

Fish, birds and other animals mistake the particles for food. Plastic builds up in food chains, and scientists are still studying how it affects human health.

## What you can do

- Refuse single-use plastic: bags, straws, cups.
- Wash synthetics less often and at low temperatures, using special laundry bags.
- Choose clothes made from natural fabrics.
- Recycle plastic so it doesn’t end up in nature.

**The less plastic we throw away today,** the less microplastic there will be in nature tomorrow.`,
    },
  },
  {
    id: 'paper_recycling',
    category: 'VIDEO',
    isDailyTip: false,
    rewardPoints: 20,
    rewardXp: 20,
    mediaUrl: 'https://www.youtube.com/watch?v=7cI8fT-9Koo',
    titles: {
      ru: 'Как перерабатывают бумагу',
      be: 'Як перапрацоўваюць паперу',
      en: 'How paper is recycled',
    },
    bodies: {
      ru: `Бумагу можно перерабатывать 5–7 раз, прежде чем волокна станут слишком короткими. В видео из передачи «Как это сделано» показан весь путь макулатуры на фабрике.

## Этапы

- **Роспуск.** Макулатуру смешивают с водой в огромном баке и превращают в кашу из волокон.
- **Очистка.** Из массы убирают скрепки, скотч, пластик и песок.
- **Удаление краски.** Пузырьки воздуха поднимают частицы типографской краски на поверхность, где их снимают.
- **Формование.** Чистую массу раскатывают тонким слоем, отжимают и сушат на горячих валах.
- **Новая продукция.** Получается бумага, картон, салфетки и упаковка.

## Как сдавать бумагу правильно

- Подходят газеты, журналы, тетради, книги без обложек и картонные коробки.
- Не подходят чеки, бумажные стаканчики, жирная упаковка от пиццы и ламинированная бумага.
- Коробки сложите плоско, чтобы они занимали меньше места.

Посмотрите видео по кнопке ниже, а потом отметьте материал прочитанным.`,
      be: `Паперу можна перапрацоўваць 5–7 разоў, перш чым валокны стануць занадта кароткімі. У відэа з перадачы «Як гэта зроблена» паказаны ўвесь шлях макулатуры на фабрыцы.

## Этапы

- **Роспуск.** Макулатуру змешваюць з вадой у велізарным баку і ператвараюць у кашу з валокнаў.
- **Ачыстка.** З масы прыбіраюць сашчэпкі, скотч, пластык і пясок.
- **Выдаленне фарбы.** Бурбалкі паветра падымаюць часціцы друкарскай фарбы на паверхню, адкуль іх здымаюць.
- **Фармаванне.** Чыстую масу раскатваюць тонкім слоем, адціскаюць і сушаць на гарачых валах.
- **Новая прадукцыя.** Атрымліваецца папера, кардон, сурвэткі і ўпакоўка.

## Як здаваць паперу правільна

- Падыходзяць газеты, часопісы, сшыткі, кнігі без вокладак і кардонныя скрыні.
- Не падыходзяць чэкі, папяровыя шклянкі, тлустая ўпакоўка ад піцы і ламінаваная папера.
- Скрыні складзіце плоска, каб яны займалі менш месца.

Паглядзіце відэа па кнопцы ніжэй, а потым адзначце матэрыял прачытаным.`,
      en: `Paper can be recycled 5–7 times before its fibres become too short. This episode of “How It’s Made” shows the whole journey of waste paper through a mill.

## The stages

- **Pulping.** Waste paper is mixed with water in a huge vat and turned into a fibre slurry.
- **Cleaning.** Staples, tape, plastic and sand are removed.
- **De-inking.** Air bubbles carry printing ink to the surface, where it is skimmed off.
- **Forming.** The clean pulp is spread thin, pressed and dried on hot rollers.
- **New products.** It becomes paper, cardboard, tissues and packaging.

## How to recycle paper properly

- Yes: newspapers, magazines, notebooks, books without covers and cardboard boxes.
- No: receipts, paper cups, greasy pizza boxes and laminated paper.
- Flatten boxes so they take up less space.

Watch the video with the button below, then mark the material as read.`,
    },
  },
  {
    id: 'home_composting',
    category: 'VIDEO',
    isDailyTip: false,
    rewardPoints: 20,
    rewardXp: 20,
    mediaUrl: 'https://www.youtube.com/watch?v=Hm20_hUVrLs',
    titles: {
      ru: 'Компост своими руками',
      be: 'Кампост сваімі рукамі',
      en: 'Composting made simple',
    },
    bodies: {
      ru: `Около трети домашнего мусора — это органика: очистки, листья, кофейная гуща. В компосте она превращается в удобрение для растений. В видео простыми словами показано, как начать.

## Что нужно

- Компостер или ящик с отверстиями для воздуха.
- «Зелёное»: очистки овощей и фруктов, трава, кофейная гуща, чайные листья.
- «Коричневое»: сухие листья, веточки, картон без краски, опилки.

## Правила

- Чередуйте зелёные и коричневые слои примерно поровну.
- Поддерживайте влажность, как у отжатой губки.
- Перемешивайте кучу раз в 1–2 недели, чтобы внутрь попадал воздух.
- Не кладите мясо, рыбу, молочные продукты и жир — они привлекают животных и плохо пахнут.

## Когда компост готов

Через 3–6 месяцев масса станет тёмной, рассыпчатой и будет пахнуть лесной землёй. Добавляйте её в грядки, клумбы и горшки с цветами.

Посмотрите видео по кнопке ниже, а потом отметьте материал прочитанным.`,
      be: `Каля трэці хатняга смецця — гэта арганіка: ачысткі, лісце, кававая гушча. У кампосце яна ператвараецца ва ўгнаенне для раслін. У відэа простымі словамі паказана, як пачаць.

## Што трэба

- Кампосцер або скрыня з адтулінамі для паветра.
- «Зялёнае»: ачысткі гародніны і садавіны, трава, кававая гушча, чайнае лісце.
- «Карычневае»: сухое лісце, галінкі, кардон без фарбы, пілавінне.

## Правілы

- Чаргуйце зялёныя і карычневыя пласты прыкладна пароўну.
- Падтрымлівайце вільготнасць, як у адціснутай губкі.
- Перамешвайце кучу раз на 1–2 тыдні, каб унутр трапляла паветра.
- Не кладзіце мяса, рыбу, малочныя прадукты і тлушч — яны прывабліваюць жывёл і дрэнна пахнуць.

## Калі кампост гатовы

Праз 3–6 месяцаў маса стане цёмнай, рассыпістай і будзе пахнуць ляснай зямлёй. Дадавайце яе ў градкі, клумбы і вазоны з кветкамі.

Паглядзіце відэа па кнопцы ніжэй, а потым адзначце матэрыял прачытаным.`,
      en: `About a third of household rubbish is organic: peels, leaves, coffee grounds. In a compost heap it turns into fertiliser for plants. The video explains how to start in plain words.

## What you need

- A compost bin or a box with air holes.
- “Greens”: fruit and vegetable peels, grass, coffee grounds, tea leaves.
- “Browns”: dry leaves, twigs, unprinted cardboard, sawdust.

## The rules

- Alternate green and brown layers in roughly equal amounts.
- Keep it as damp as a wrung-out sponge.
- Turn the heap every 1–2 weeks to let air in.
- Leave out meat, fish, dairy and fat — they attract animals and smell bad.

## When it is ready

After 3–6 months the mix turns dark and crumbly and smells like forest soil. Add it to vegetable beds, flower beds and pots.

Watch the video with the button below, then mark the material as read.`,
    },
  },
  {
    id: 'ocean_plastic',
    category: 'VIDEO',
    isDailyTip: false,
    rewardPoints: 20,
    rewardXp: 20,
    mediaUrl: 'https://www.youtube.com/watch?v=Lq108re03O8',
    titles: {
      ru: 'Как пластик попадает в океан',
      be: 'Як пластык трапляе ў акіян',
      en: 'How plastic gets into the ocean',
    },
    bodies: {
      ru: `Каждый год в океан попадают миллионы тонн пластика. Беларусь далеко от моря, но наш мусор тоже может туда доплыть. В видео на карте показано, как это происходит.

## Путь пластика

- Мусор, брошенный на улице или у реки, смывает дождь.
- Ручьи и реки уносят его всё дальше — Днепр, например, впадает в Чёрное море.
- В океане течения собирают пластик в огромные «мусорные пятна».
- На солнце и в солёной воде он распадается на микропластик.

## Почему это важно

Черепахи, птицы и киты принимают пакеты и крышки за еду. Рыболовные сети, потерянные в море, продолжают ловить животных годами.

## Что можно сделать даже вдали от моря

- Никогда не оставляйте мусор у рек, озёр и в лесу.
- Участвуйте в уборках берегов — в приложении для этого есть задания.
- Сдавайте пластик на переработку и пользуйтесь многоразовыми вещами.

Посмотрите видео по кнопке ниже, а потом отметьте материал прочитанным.`,
      be: `Кожны год у акіян трапляюць мільёны тон пластыку. Беларусь далёка ад мора, але наша смецце таксама можа туды даплыць. У відэа на карце паказана, як гэта адбываецца.

## Шлях пластыку

- Смецце, кінутае на вуліцы або каля ракі, змывае дождж.
- Ручаі і рэкі нясуць яго ўсё далей — Дняпро, напрыклад, упадае ў Чорнае мора.
- У акіяне плыні збіраюць пластык у велізарныя «смеццевыя плямы».
- На сонцы і ў салёнай вадзе ён распадаецца на мікрапластык.

## Чаму гэта важна

Чарапахі, птушкі і кіты прымаюць пакеты і накрыўкі за ежу. Рыбацкія сеткі, згубленыя ў моры, працягваюць лавіць жывёл гадамі.

## Што можна зрабіць нават удалечыні ад мора

- Ніколі не пакідайце смецце каля рэк, азёр і ў лесе.
- Удзельнічайце ў прыбіранні берагоў — у праграме для гэтага ёсць заданні.
- Здавайце пластык на перапрацоўку і карыстайцеся шматразовымі рэчамі.

Паглядзіце відэа па кнопцы ніжэй, а потым адзначце матэрыял прачытаным.`,
      en: `Millions of tonnes of plastic reach the ocean every year. Belarus is far from the sea, but our rubbish can still float there. The video uses a map to show how.

## Plastic’s journey

- Litter dropped in the street or by a river is washed away by rain.
- Streams and rivers carry it further — the Dnieper, for example, flows into the Black Sea.
- In the ocean, currents gather plastic into huge “garbage patches”.
- Sunlight and salt water break it down into microplastics.

## Why it matters

Turtles, birds and whales mistake bags and caps for food. Fishing nets lost at sea keep catching animals for years.

## What you can do far from the sea

- Never leave litter by rivers, lakes or in the forest.
- Join riverbank clean-ups — the app has tasks for that.
- Recycle plastic and use reusable things.

Watch the video with the button below, then mark the material as read.`,
    },
  },
  {
    id: 'solar_energy',
    category: 'VIDEO',
    isDailyTip: false,
    rewardPoints: 20,
    rewardXp: 20,
    mediaUrl: 'https://www.youtube.com/watch?v=xKxrkht7CpY',
    titles: {
      ru: 'Как работают солнечные панели',
      be: 'Як працуюць сонечныя панэлі',
      en: 'How solar panels work',
    },
    bodies: {
      ru: `Солнце — неисчерпаемый источник энергии. Короткий урок TED-Ed объясняет, как солнечная панель превращает свет в электричество.

## Коротко о главном

- Панель состоит из ячеек, сделанных из кремния.
- Свет выбивает электроны из атомов кремния.
- Внутреннее электрическое поле направляет их движение — так возникает ток.
- Инвертор превращает этот ток в такой, какой течёт в розетке.

## Плюсы и сложности

- Солнечная энергия не даёт выбросов CO₂ во время работы.
- Панели служат 25–30 лет.
- Ночью и в пасмурную погоду энергии меньше, поэтому нужны аккумуляторы или другие источники.
- Старые панели нужно правильно перерабатывать.

## В Беларуси

В стране уже работают солнечные электростанции, а частные дома всё чаще ставят панели на крыши.

Посмотрите видео по кнопке ниже, а потом отметьте материал прочитанным.`,
      be: `Сонца — невычэрпная крыніца энергіі. Кароткі ўрок TED-Ed тлумачыць, як сонечная панэль ператварае святло ў электрычнасць.

## Коратка пра галоўнае

- Панэль складаецца з ячэек, зробленых з крэмнію.
- Святло выбівае электроны з атамаў крэмнію.
- Унутранае электрычнае поле накіроўвае іх рух — так узнікае ток.
- Інвертар ператварае гэты ток у такі, які цячэ ў разетцы.

## Плюсы і складанасці

- Сонечная энергія не дае выкідаў CO₂ падчас працы.
- Панэлі служаць 25–30 гадоў.
- Ноччу і ў пахмурнае надвор’е энергіі менш, таму патрэбны акумулятары або іншыя крыніцы.
- Старыя панэлі трэба правільна перапрацоўваць.

## У Беларусі

У краіне ўжо працуюць сонечныя электрастанцыі, а прыватныя дамы ўсё часцей ставяць панэлі на дахі.

Паглядзіце відэа па кнопцы ніжэй, а потым адзначце матэрыял прачытаным.`,
      en: `The sun is an endless source of energy. This short TED-Ed lesson explains how a solar panel turns light into electricity.

## The essentials

- A panel is made of cells built from silicon.
- Light knocks electrons out of silicon atoms.
- An internal electric field steers them — and that creates a current.
- An inverter converts that current into the kind that flows from a socket.

## Pros and challenges

- Solar power produces no CO₂ while running.
- Panels last 25–30 years.
- At night and on cloudy days there is less energy, so batteries or other sources are needed.
- Old panels must be recycled properly.

## In Belarus

The country already has solar power plants, and more and more private homes put panels on their roofs.

Watch the video with the button below, then mark the material as read.`,
    },
  },
  {
    id: 'bee_friends',
    category: 'KIDS',
    isDailyTip: false,
    rewardPoints: 10,
    rewardXp: 10,
    mediaUrl: null,
    titles: {
      ru: 'Зачем нам нужны пчёлы',
      be: 'Навошта нам патрэбны пчолы',
      en: 'Why we need bees',
    },
    bodies: {
      ru: `Пчёлы делают не только мёд. Они — главные помощники растений!

## Что делают пчёлы

Когда пчела садится на цветок, к её пушистому телу прилипает пыльца. Перелетая на другой цветок, она оставляет там немного пыльцы. Это называется **опыление**. Без него у яблонь не будет яблок, у клубники — ягод, а у огурцов — огурцов.

## Интересные факты

- Чтобы собрать одну ложку мёда, пчёлам нужно облететь тысячи цветков.
- Пчёлы рассказывают друг другу, где цветы, с помощью особого танца.
- Кроме медоносных пчёл, есть шмели и сотни видов диких пчёл.

## Как помочь пчёлам

- Посади цветы на балконе или во дворе: лаванду, ромашку, календулу.
- Поставь блюдце с водой и камешками, чтобы пчёлы могли попить.
- Не трогай пчёл и не размахивай руками — пчела ужалит, только если испугается.

**А ещё ты можешь сыграть в игру «Опылитель»** и помочь пчеле спасти цветы!`,
      be: `Пчолы робяць не толькі мёд. Яны — галоўныя памочнікі раслін!

## Што робяць пчолы

Калі пчала садзіцца на кветку, да яе пухнатага цела прыліпае пылок. Пераляцеўшы на іншую кветку, яна пакідае там крыху пылку. Гэта называецца **апыленне**. Без яго ў яблынь не будзе яблыкаў, у клубніцы — ягад, а ў агуркоў — агуркоў.

## Цікавыя факты

- Каб сабраць адну лыжку мёду, пчолам трэба абляцець тысячы кветак.
- Пчолы расказваюць адна адной, дзе кветкі, з дапамогай асаблівага танца.
- Акрамя меданосных пчол, ёсць чмялі і сотні відаў дзікіх пчол.

## Як дапамагчы пчолам

- Пасадзі кветкі на балконе або ў двары: лаванду, рамонак, календулу.
- Пастаў сподак з вадой і каменьчыкамі, каб пчолы маглі напіцца.
- Не чапай пчол і не махай рукамі — пчала ўджаліць, толькі калі спалохаецца.

**А яшчэ ты можаш згуляць у гульню «Апыляльнік»** і дапамагчы пчале выратаваць кветкі!`,
      en: `Bees don’t just make honey. They are plants’ best helpers!

## What bees do

When a bee lands on a flower, pollen sticks to its fuzzy body. When it flies to the next flower, it leaves some pollen there. This is called **pollination**. Without it, apple trees would have no apples, strawberries no berries and cucumber plants no cucumbers.

## Fun facts

- To make one spoonful of honey, bees have to visit thousands of flowers.
- Bees tell each other where the flowers are with a special dance.
- Besides honeybees, there are bumblebees and hundreds of kinds of wild bees.

## How to help bees

- Plant flowers on a balcony or in the yard: lavender, daisies, marigolds.
- Put out a saucer of water with pebbles so bees can drink.
- Don’t touch bees or wave your arms — a bee only stings when it is scared.

**You can also play “Pollinator”** and help a bee save the flowers!`,
    },
  },
  {
    id: 'winter_birds',
    category: 'KIDS',
    isDailyTip: false,
    rewardPoints: 10,
    rewardXp: 10,
    mediaUrl: null,
    titles: {
      ru: 'Как помочь птицам зимой',
      be: 'Як дапамагчы птушкам зімой',
      en: 'How to help birds in winter',
    },
    bodies: {
      ru: `Зимой птицам трудно: насекомых нет, а семена прячутся под снегом. Поэтому многие птицы прилетают поближе к людям.

## Кто остаётся с нами на зиму

- **Синицы** — жёлтые с чёрной «шапочкой».
- **Снегири** — с ярко-красной грудкой.
- **Воробьи, голуби, дятлы и сороки.**

## Чем можно кормить

- Несолёные семечки подсолнуха.
- Пшено, овёс и другие крупы.
- Кусочек несолёного сала для синиц.

## Чем кормить нельзя

- Чёрным хлебом и булкой — от них у птиц болит живот.
- Солёной, жареной и острой едой.

## Правила кормушки

- Повесь кормушку повыше, чтобы до неё не добрались кошки.
- Подкармливай птиц каждый день: они привыкают и прилетают в одно и то же место.
- Зимой не забывай про воду — её тоже бывает трудно найти.

**Сделай кормушку вместе со взрослыми** — из коробки от сока или деревянных дощечек — и понаблюдай, кто к тебе прилетит!`,
      be: `Зімой птушкам цяжка: насякомых няма, а насенне хаваецца пад снегам. Таму многія птушкі прылятаюць бліжэй да людзей.

## Хто застаецца з намі на зіму

- **Сініцы** — жоўтыя з чорнай «шапачкай».
- **Гілі** — з ярка-чырвонымі грудкамі.
- **Вераб’і, галубы, дзятлы і сарокі.**

## Чым можна карміць

- Несалёным насеннем сланечніку.
- Пшонам, аўсом і іншымі крупамі.
- Кавалачкам несалёнага сала для сініц.

## Чым карміць нельга

- Чорным хлебам і булкай — ад іх у птушак баліць жывот.
- Салёнай, смажанай і вострай ежай.

## Правілы кармушкі

- Павесь кармушку вышэй, каб да яе не дабраліся каты.
- Падкормлівай птушак кожны дзень: яны прывыкаюць і прылятаюць у адно і тое ж месца.
- Зімой не забывай пра ваду — яе таксама бывае цяжка знайсці.

**Зрабі кармушку разам з дарослымі** — з каробкі ад соку або драўляных дошчачак — і паназірай, хто да цябе прыляціць!`,
      en: `Winter is hard for birds: there are no insects, and seeds hide under the snow. That is why many birds come closer to people.

## Who stays with us for the winter

- **Tits** — yellow with a black “cap”.
- **Bullfinches** — with a bright red chest.
- **Sparrows, pigeons, woodpeckers and magpies.**

## What you can feed them

- Unsalted sunflower seeds.
- Millet, oats and other grains.
- A small piece of unsalted fat for the tits.

## What not to feed them

- Brown bread or white rolls — they give birds a tummy ache.
- Salty, fried or spicy food.

## Feeder rules

- Hang the feeder high so cats can’t reach it.
- Feed the birds every day: they get used to it and come back to the same spot.
- In winter, remember water too — it can be hard to find.

**Make a feeder with a grown-up** — from a juice carton or wooden slats — and watch who comes to visit!`,
    },
  },
  {
    id: 'family_sorting',
    category: 'KIDS',
    isDailyTip: false,
    rewardPoints: 10,
    rewardXp: 10,
    mediaUrl: null,
    titles: {
      ru: 'Сортируем мусор всей семьёй',
      be: 'Сартуем смецце ўсёй сям’ёй',
      en: 'Sorting waste as a family',
    },
    bodies: {
      ru: `Сортировать мусор — это как играть в игру, где у каждой вещи есть свой домик. Давай разберёмся вместе!

## Домики для мусора

- **Синий контейнер — бумага:** газеты, тетради, коробки.
- **Жёлтый контейнер — пластик:** бутылки, баночки от йогурта, пакеты.
- **Зелёный контейнер — стекло:** банки и бутылки.
- **Отдельный бокс — батарейки.** Их нельзя бросать в обычный мусор!

## Игра «Найди домик»

1. Поставьте дома три коробки и нарисуйте на них значки: лист бумаги, бутылку и банку.
2. Каждый вечер вместе с родителями раскладывайте мусор по коробкам.
3. Кто правильно разложит больше вещей за неделю — тот получает звание «Эко-героя»!

## Помни

- Перед тем как положить баночку, её нужно сполоснуть.
- Бутылки лучше смять, чтобы в коробку поместилось больше.
- Если не знаешь, куда положить вещь, — спроси у взрослых.

**Потренироваться можно в игре «Сортировочный конвейер»** — там отходы едут по ленте, а ты выбираешь правильный бак!`,
      be: `Сартаваць смецце — гэта як гуляць у гульню, дзе ў кожнай рэчы ёсць свой домік. Давай разбярэмся разам!

## Домікі для смецця

- **Сіні кантэйнер — папера:** газеты, сшыткі, скрыні.
- **Жоўты кантэйнер — пластык:** бутэлькі, слоічкі ад ёгурту, пакеты.
- **Зялёны кантэйнер — шкло:** слоікі і бутэлькі.
- **Асобны бокс — батарэйкі.** Іх нельга кідаць у звычайнае смецце!

## Гульня «Знайдзі домік»

1. Пастаўце дома тры скрыні і намалюйце на іх значкі: аркуш паперы, бутэльку і слоік.
2. Кожны вечар разам з бацькамі раскладвайце смецце па скрынях.
3. Хто правільна раскладзе больш рэчаў за тыдзень — той атрымлівае званне «Эка-героя»!

## Памятай

- Перш чым пакласці слоічак, яго трэба спаласнуць.
- Бутэлькі лепш сціснуць, каб у скрыню змясцілася больш.
- Калі не ведаеш, куды пакласці рэч, — спытай у дарослых.

**Патрэніравацца можна ў гульні «Сартавальны канвеер»** — там адходы едуць па стужцы, а ты выбіраеш правільны бак!`,
      en: `Sorting waste is like a game where every item has its own little house. Let’s figure it out together!

## Houses for rubbish

- **Blue bin — paper:** newspapers, notebooks, boxes.
- **Yellow bin — plastic:** bottles, yoghurt pots, bags.
- **Green bin — glass:** jars and bottles.
- **A special box — batteries.** Never put them in the regular bin!

## The “Find the house” game

1. Put three boxes at home and draw a sign on each: a sheet of paper, a bottle and a jar.
2. Every evening, sort the rubbish into the boxes together with your parents.
3. Whoever sorts the most things correctly in a week earns the title of “Eco Hero”!

## Remember

- Rinse a pot before you put it in.
- Squash bottles so more fit in the box.
- If you don’t know where something goes, ask a grown-up.

**You can practise in the game “Sorting line”** — waste rides along a belt and you pick the right bin!`,
    },
  },
];

function plainText(markdown) {
  return markdown
    .split('\n')
    .map((line) => line.replace(/^## /, '').replace(/^- /, '• ').replace(/\*\*/g, ''))
    .join('\n');
}

module.exports = { ecoTips, plainText, COVERS_BASE_URL };
