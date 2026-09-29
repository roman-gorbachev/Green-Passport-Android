# Яркий молодёжный дизайн, экраны без topbar, loading на главной

## Контекст

Прогон на устройстве пользователя (Galaxy A54, 1080×2340, сборка `dafad7a`) показал:

- **Скучно и бледно.** Бледно-зелёные карточки на бледно-мятном фоне, одинаковые плитки, из акцентов только коричневый. Единственный яркий элемент макета — фото события — не показывается: все тестовые события в `scripts/seed-firestore.js` датированы августом 2026, для приложения они в прошлом. Пользователь: «куча плиток, всё бледное, нет акцентов, скучно и сердито — мы нацелены на молодёжь».
- **Topbar.** На всех вложенных экранах и вкладках `LargeTopAppBar` — большая пустая полоса с кнопкой «назад», заголовок уехал вниз. Нужно: без topbar, edge-to-edge, заголовок — часть контента, с отступами.
- **iOS-эффекты не нужны.** Пользователь имел в виду форму (скруглённая кнопка «назад» и т.п.), а не блюр/стекло/проседание/вибрацию. Всё это откатываем.
- **Главная «прыгает».** Сначала спиннер, потом контент выпрыгивает целиком, при холодном старте ещё и сплэш со старым Canvas-маскотом.
- **Карта не отображается** — в `local.properties` пустой `YANDEX_MAPKIT_API_KEY`. Ключ пользователь впишет сам.

Решение пользователя: направление **«Яркий акцент»** — мятная база макета остаётся, добавляются тёмно-зелёный блок прогресса с салатовым акцентом, маскот с репликой, цветные круглые быстрые действия вместо плиток, белые карточки, салатовые бейджи очков.

Первым действием план сохраняется в `claude/youth-redesign-plan.ru.md`. Сборку и установку на устройство делаю сам (`./gradlew installDebug`, устройство подключено), проверяю скриншотами через `adb`. Коммит — в `main` в конце.

---

## Шаг 1. Откат iOS-эффектов

### Почему

Пользователь просил переносить форму, а не эффекты. Блюр, матовое окно-шторка, проседание и вибрация добавляют зависимость (Haze), нестабильное API окна и риск на разных устройствах, при этом в дизайне они не нужны.

### Что делаем

- Удалить Haze: `GlassSurface.kt`, зависимости `haze`/`haze-blur` в `libs.versions.toml`, `core/build.gradle.kts`, `app/build.gradle.kts`, `hazeSource` в `GreenPassportAppShell`.
- `GpBottomBar`: сплошная белая плашка с мягкой тенью, выбранная вкладка — тёмно-зелёный круг с белой иконкой (шаг 3). Анимацию переезда плашки и хаптику убрать.
- `GpSheetScaffold`: обычная шторка на весь низ экрана со скруглённым верхом, полупрозрачный скрим сверху; без `setBackgroundBlurRadius`, без изменения размера окна. Перетаскивание — через `rawY` (оставляем, оно работает).
- `GpSurfaceCard`/`GpPrimaryButton`: вернуть обычный ripple, удалить `PressScale.kt`.
- Удалить `ConfirmHapticOnSuccess.kt` и его вызовы.
- `GpBackButton` (круглая кнопка с шевроном) — оставляем, это та самая «скруглённая кнопка назад».

---

## Шаг 2. Экраны без topbar, edge-to-edge

### Почему

Заголовок должен быть частью страницы, как «Привет, Роман» на главной, а не отдельной полосой. `FeatureScaffold` используется всеми вложенными экранами и вкладками (Магазин, Избранное), поэтому переделка в одном месте меняет все 20 экранов.

### Код

`FeatureScaffold`: шапка (кнопка «назад» + заголовок) уезжает вверх вместе с контентом при скролле и возвращается у верха списка. Высоту шапки уменьшаем через `TopAppBarState` из M3 — это просто состояние nested scroll, сам topbar не рисуется:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureScaffold(
    title: String,
    onNavigateBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable (innerPadding: PaddingValues) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val bottomInset = if (onNavigateBack == null) Dimens.BottomBarReservedHeight else Dimens.SpacingNone

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        ScreenHeader(
            title = title,
            onNavigateBack = onNavigateBack,
            heightOffsetState = scrollBehavior.state,
        )
        Box(modifier = Modifier.weight(1f)) {
            content(
                PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + bottomInset,
                ),
            )
        }
    }
}
```

`ScreenHeader` (новый, `core/designsystem/component`) — `Layout`, который измеряет строку `[GpBackButton] Заголовок` со `statusBarsPadding()`, пишет её высоту в `state.heightOffsetLimit = -headerHeight` и занимает по вертикали `headerHeight + state.heightOffset`, сдвигая содержимое на `heightOffset`. Фон — цвет экрана, без тени и без границы, т.е. визуально это продолжение страницы. Заголовок — `headlineLarge` (26sp Medium), кнопка «назад» слева в одну строку с ним.

Экраны уже применяют `Modifier.padding(innerPadding)` — менять их не нужно. Карта (`MapScreen`) остаётся без шапки вовсе: карта на весь экран, поиск парит сверху.

---

## Шаг 3. Палитра «Яркий акцент»

### Почему

Бледные карточки на бледном фоне не дают контраста. Для молодёжной аудитории нужен один сильный акцентный блок, насыщенный CTA-цвет и цветовое кодирование разделов. Мятный фон из макета остаётся — он и есть «эко»-база.

### Код

`Color.kt` (светлая тема):

```kotlin
private val MintBackgroundLight = Color(0xFFF1FEF5)
private val CardLight = Color(0xFFFFFFFF)
private val ForestLight = Color(0xFF1F6B47)
private val OnForestLight = Color(0xFFFFFFFF)
private val LimeLight = Color(0xFFC3EE5A)
private val OnLimeLight = Color(0xFF17331F)
private val InkLight = Color(0xFF0B0F10)
private val MossLight = Color(0xFF385247)
private val MossSecondaryLight = Color(0xFF6A7F75)
private val MintSurfaceLight = Color(0xFFD6F1E0)
```

Роли: `primary = ForestLight` (кнопки, выбранная вкладка, ссылки), `secondary = LimeLight`/`onSecondary = OnLimeLight` (прогресс, бейджи очков), `surfaceContainer = CardLight` (белые карточки), `surfaceContainerHigh = MintSurfaceLight` (вторичные плашки, чипы). Коричневый уходит.

Цвета разделов — новый `SectionColors.kt` (через `CompositionLocal` в `GreenPassportTheme`, как `RewardTierColors`):

```kotlin
data class SectionColors(
    val community: Color,
    val games: Color,
    val tips: Color,
    val calendar: Color,
    val feedback: Color,
)

val LightSectionColors = SectionColors(
    community = Color(0xFF34C77B),
    games = Color(0xFF8E7CF0),
    tips = Color(0xFFFF9F43),
    calendar = Color(0xFF4DA3FF),
    feedback = Color(0xFFFF6B8A),
)
```

Тёмная тема — те же роли, `ForestDark = #2E8C5E`, `LimeDark = #B5E04C`, карточки `#1E2A24`.

---

## Шаг 4. Новые компоненты

### Почему

Акценты должны повторяться на всех экранах одинаково: бейдж очков, цветной кружок-иконка, блок прогресса. Плитки `QuickActionTile` и бледная `LevelProgressCard` заменяются.

### Код

`PointsChip` — салатовый бейдж «+50 ⚡»:

```kotlin
@Composable
fun PointsChip(
    points: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.CornerRadiusPill))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = Dimens.SpacingSmall + Dimens.SpacingExtraSmall, vertical = Dimens.SpacingExtraSmall),
    ) {
        Text(
            text = stringResource(R.string.points_reward, points),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondary,
        )
        Icon(
            imageVector = Icons.Filled.Bolt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(Dimens.IconSizeExtraSmall),
        )
    }
}
```

- `IconCircle(icon, color)` — круг 52dp цвета раздела с белой иконкой; используется в быстрых действиях главной, в меню профиля, в хабах «Сообщество» и «Игры».
- `QuickActionButton(label, icon, color, onClick)` — `IconCircle` + подпись под ним, без плитки.
- `ProgressHeroCard(level, currentXp, xpForNextLevel, points)` — тёмно-зелёный блок (`primary`, радиус 24dp): «Ур. 3», баланс «★ 500 оч.», салатовый прогресс-бар 8dp, справа маскот 96dp с облачком «Ещё 200 оч. до ур. 4» (белый пузырь). Заменяет `LevelProgressCard`.
- `GpListRow`: белый фон, справа по умолчанию шеврон; для заданий в `trailing` — `PointsChip`.
- `GpBottomBar`: белая плашка, тень 8dp, выбранная вкладка — `IconCircle`-подобный тёмно-зелёный круг 44dp с белой иконкой, остальные иконки `MossLight`.

`QuickActionTile.kt` и `LevelProgressCard.kt` удаляются.

---

## Шаг 5. Главная: без прыжков, с акцентами

### Почему

Сейчас: спиннер → контент выпрыгивает целиком, при `LifecycleResumeEffect`-обновлении список может перестраиваться. Нужно: пока грузится — показывать загрузку на месте будущих блоков (без смены раскладки), после загрузки — плавное появление; обновление при возврате — без сброса в loading и с анимацией элементов.

### Код

`HomeUiState` получает баланс очков, `HomeViewModel` — `GetPointsBalanceUseCase` (`PointsRepository.getBalance`). Загрузка всех блоков параллельно через `coroutineScope { async … }`, один emit.

`HomeContent`: шапка (дата, «Привет, Роман 👋», аватар-маскот в круге) рендерится сразу — она не зависит от сети. Остальное:

```kotlin
AnimatedContent(
    targetState = uiState.isLoading,
    transitionSpec = { fadeIn() togetherWith fadeOut() },
    label = "homeContent",
) { isLoading ->
    if (isLoading) {
        HomeLoadingPlaceholder()
    } else {
        HomeSections(uiState = uiState, …)
    }
}
```

`HomeLoadingPlaceholder` — те же блоки (прогресс-карта 140dp, ряд из 4 кругов, 3 строки по 67dp) в виде плашек `surfaceContainerHigh` без текста, с `CircularProgressIndicator` в прогресс-карте. Раскладка при появлении данных не меняется.

`items(uiState.tasks, key = { it.id }) { … Modifier.animateItem() }` — при обновлении после выполнения задания строка плавно уезжает, а не прыгает.

Порядок блоков — как в превью: шапка → `ProgressHeroCard` → ряд `QuickActionButton` (Сообщество, Игры, Советы, Календарь, Отзывы — горизонтальный скролл) → карточка ближайшего события (если есть) → «Твои задания / Все →» → строки с маскотом и `PointsChip`.

Тестовые данные: `scripts/seed-firestore.js` — даты событий сдвигаем на октябрь–ноябрь 2026, чтобы на главной появилась карточка события. Уже загруженные в Firestore события пользователь пересоздаёт скриптом (описано в `scripts/README.md`).

---

## Шаг 6. Остальные экраны в новом стиле

### Почему

После шагов 2–4 экраны получают белые карточки, насыщенные кнопки и заголовок без topbar автоматически. Вручную — только то, что раньше было «плитками» и бледными иконками.

- Профиль: шапка как на главной (маскот, имя, баланс), `ProgressHeroCard`, пункты меню — `GpListRow` с `IconCircle` 36dp своего цвета.
- Сообщество, Игры: вместо плиток — список `GpListRow` с `IconCircle` и подписью (игры — с рекордом справа).
- Задания: чипы фильтров — выбранный тёмно-зелёный, строки с `PointsChip`.
- Магазин: баланс — `ProgressHeroCard` без прогресса (тёмно-зелёный блок с «★ 500 оч.»), купоны — белые карточки, кнопка «Обменять» — `primary`.
- Сплэш и иконка приложения: маскот вместо Canvas-круга (`values-v31/themes.xml` `windowSplashScreenAnimatedIcon` + adaptive icon foreground из `mascot.webp`).

---

## Шаг 7. Карта

### Почему

Карта не отображается только из-за пустого ключа. Пока ключа нет — запасной список должен выглядеть так же, как остальные экраны.

- После того как пользователь впишет `YANDEX_MAPKIT_API_KEY`, пересобираю, открываю вкладку «Карта» на устройстве и проверяю пины и шторку точки.
- Запасной список: шапка без topbar (как шаг 2), строки с `IconCircle` цвета типа точки.

---

## Критичные файлы

- `core/.../designsystem/theme/{Color,Theme,Dimens}.kt`, новый `SectionColors.kt`
- `core/.../designsystem/component/`: новые `ScreenHeader`, `PointsChip`, `IconCircle`, `QuickActionButton`, `ProgressHeroCard`; правки `GpBottomBar`, `GpSheetScaffold`, `GpSurfaceCard`, `GpPrimaryButton`, `GpListRow`; удалить `GlassSurface`, `PressScale`, `ConfirmHapticOnSuccess`, `QuickActionTile`, `LevelProgressCard`
- `app/.../navigation/{FeatureScaffold,GreenPassportAppShell}.kt`, `app/src/main/res/values-v31/themes.xml`, `mipmap-*`
- `feature/home/...` (state, viewmodel, domain `GetPointsBalanceUseCase`, `HomeScreen`)
- `feature/profile/.../ProfileScreen.kt`, `feature/community/.../CommunityHubScreen.kt`, `feature/games/.../GamesHubScreen.kt`, `feature/shop/.../ShopScreen.kt`, `feature/tasks/.../TasksListScreen.kt`, `feature/map/.../MapScreen.kt`
- `gradle/libs.versions.toml`, `core/build.gradle.kts`, `app/build.gradle.kts`
- `scripts/seed-firestore.js`

Переиспользуем: `MascotWidget` (`mascot.webp`), `NetworkImage` с `event_placeholder`, `GpBackButton`, `HeroImageCard`, `TopLevelDestination`, `formatEventDate/Time`, `LifecycleResumeEffect`-обновление главной.

## Проверка

Сам собираю и ставлю на подключённый A54:

```bash
./gradlew installDebug
./gradlew build detektAll
adb shell am force-stop com.smartcity.greenpassport
adb shell monkey -p com.smartcity.greenpassport -c android.intent.category.LAUNCHER 1
adb exec-out screencap -p > home.png
```

Сценарии со скриншотами:
1. Холодный старт: сплэш с маскотом → главная с плейсхолдерами той же формы → плавное появление, без прыжков (серия кадров каждые 300 мс).
2. Главная: тёмно-зелёный блок прогресса с салатовым баром и маскотом, цветные круги быстрых действий, карточка события с фото, строки с салатовыми бейджами.
3. Вложенные экраны (Задания, Профиль, Игры) и вкладки (Магазин, Избранное): нет topbar, кнопка «назад» — круг в одной строке с заголовком, шапка уезжает при скролле.
4. Тап по заданию — шторка со скруглённым верхом, без блюра; «Отметить выполненной» — строка плавно исчезает из списка на главной.
5. Карта с ключом — пины и шторка точки; без ключа — список в новом стиле.
6. Тёмная тема — контраст текста на карточках и в зелёном блоке.
