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
];

function plainText(markdown) {
  return markdown
    .split('\n')
    .map((line) => line.replace(/^## /, '').replace(/^- /, '• ').replace(/\*\*/g, ''))
    .join('\n');
}

module.exports = { ecoTips, plainText, COVERS_BASE_URL };
