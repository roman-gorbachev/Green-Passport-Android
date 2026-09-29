# Редизайн Green Passport под новые макеты

## Контекст

Сейчас UI — сетка ярко-зелёных квадратных плиток на фоновой картинке (`HomeScreen` в `:app`), стандартные Material-карточки и `TopAppBar`, заголовки шрифтом Great Vibes. Выглядит как «калькулятор». Макеты (`~/Downloads/green pasport/`: `home`, два экрана с шторками, карта) задают другой язык:

- мятный фон, мягкие светло-зелёные карточки с большими скруглениями, без обводок;
- коричневый акцент (прогресс-бар уровня, основные кнопки, активная вкладка, «See more»);
- главная: приветствие + дата + аватар, карточка уровня `lvl 3 · 500 / 700 pt.`, hero-карточка ближайшего события с фото, три быстрые плитки (Group-chat, Mini-games, Eco-tips), «Your tasks» со строками «маскот + название + очки + шеврон»;
- детали задачи/события — нижняя шторка поверх размытого предыдущего экрана, с коричневой pill-кнопкой;
- плавающий нижний бар из 4 вкладок: Главная · Магазин · Карта · Избранное; профиль открывается по аватару;
- карта — настоящая карта с коричневыми пинами.

Решения, принятые с пользователем: bottom bar из 4 вкладок, карта на **Yandex MapKit**, **системный шрифт**, маскот пока остаётся Canvas-заглушкой (`MascotWidget`).

Первым действием после одобрения этот план сохраняется в `claude/redesign-plan.ru.md` (по правилам CLAUDE.md). Работа делится на фазы; каждая фаза — отдельный коммит, и после каждой приложение собирается и работает.

---

## Фаза 1. Тема: цвета, типографика, формы

### Почему

Каждый экран, который мы перекрашиваем, берёт цвета и скругления из `MaterialTheme`. Пока в теме `primary = #2E7D32` и Great Vibes в `headline*`, любая переделка экрана будет «торчать». Сначала меняется фундамент, и уже от этого все экраны частично перекрашиваются сами.

### Код

`core/.../designsystem/theme/Color.kt` — палитра по макету (значения сняты пипеткой со скриншотов, доводим на устройстве):

```kotlin
private val MintBackgroundLight = Color(0xFFEAF6EC)
private val LeafSurfaceLight = Color(0xFFD5EDDA)
private val LeafSurfaceVariantLight = Color(0xFFC6E3CD)
private val InkLight = Color(0xFF1B1F1C)
private val MossLight = Color(0xFF4E6A61)
private val BarkLight = Color(0xFF93674A)
private val OnBarkLight = Color(0xFFFFFFFF)
private val TrackLight = Color(0xFFDCE3DE)

val GreenPassportLightColorScheme = lightColorScheme(
    primary = BarkLight,
    onPrimary = OnBarkLight,
    primaryContainer = LeafSurfaceVariantLight,
    onPrimaryContainer = InkLight,
    secondary = MossLight,
    onSecondary = OnBarkLight,
    secondaryContainer = LeafSurfaceLight,
    onSecondaryContainer = MossLight,
    background = MintBackgroundLight,
    onBackground = InkLight,
    surface = MintBackgroundLight,
    onSurface = InkLight,
    surfaceContainer = LeafSurfaceLight,
    surfaceContainerHigh = LeafSurfaceVariantLight,
    onSurfaceVariant = MossLight,
    outlineVariant = TrackLight,
)
```

Тёмная схема собирается по тому же принципу: глубокий зелёно-серый фон `#121815`, карточки `#1E2A24`, коричневый акцент осветляется до `#C39A7C`. `RewardTierColors` остаются.

`Type.kt` — Great Vibes убираем, везде `FontFamily.Default`, иерархия по макету:

```kotlin
val GreenPassportTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 34.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 28.sp, lineHeight = 34.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontSize = 17.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 22.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 18.sp),
)
```

Новый `Shape.kt` и подключение в `GreenPassportTheme(shapes = GreenPassportShapes)`:

```kotlin
val GreenPassportShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)
```

`Dimens.kt` дополняется: `ScreenHorizontalPadding = 20.dp`, `CardPadding = 20.dp`, `BottomBarHeight = 72.dp`, `QuickActionHeight = 96.dp`, `HeroCardHeight = 240.dp`, `ProgressBarHeight = 4.dp`, `AvatarSize = 56.dp`, `ListRowHeight = 80.dp`.

Удаляются `res/font/greatvibes_regular.ttf` и `ScreenTitleFontFamily`.

---

## Фаза 2. Компоненты дизайн-системы (`core/designsystem/component/`)

### Почему

Одни и те же элементы (зелёная карточка, строка с маскотом, коричневая кнопка, шторка) повторяются на всех экранах. Если собирать их на месте из `Card`/`Button`, 28 экранов разойдутся по отступам и цветам уже через неделю. Каждый компонент — отдельный файл (правило «один тип на файл»).

| Компонент | Где в макете |
|---|---|
| `GpSurfaceCard` | фон всех карточек: `surfaceContainer`, `shapes.medium`, без обводки и тени |
| `LevelProgressCard` | `lvl 3 ··· 500 / 700 pt.` + коричневый прогресс |
| `HeroImageCard` | событие с фото, градиент снизу, заголовок + подзаголовок |
| `QuickActionTile` | Group-chat / Mini-games / Eco-tips |
| `GpListRow` | строка задачи: маскот/иконка, заголовок, подзаголовок, шеврон |
| `SectionHeader` | «Your tasks ··· See more» |
| `GpPrimaryButton` | коричневая pill-кнопка на всю ширину |
| `GpTopBar` | заголовок вложенных экранов + круглая кнопка «назад» |
| `GpBottomBar` | плавающий бар с 4 вкладками, активная — плашка + коричневая иконка |
| `GpSheetScaffold` | контейнер шторки для dialog-destination (фаза 4) |
| `GpFilterChip` | фильтры (задачи, карта, магазин): коричневый выбранный |

Существующие `PillListItem` (обводка pill) и `PointsBadge` заменяются на `GpListRow` и `LevelProgressCard` и удаляются в конце фазы 7. `UiStateContent` (Loading/Empty/Error) перекрашивается: пустое состояние и ошибка — маскот + текст + `GpPrimaryButton`.

### Код

```kotlin
@Composable
fun LevelProgressCard(
    level: Int,
    currentXp: Int,
    xpForNextLevel: Int,
    modifier: Modifier = Modifier,
) {
    GpSurfaceCard(modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = Dimens.CardPadding, vertical = Dimens.SpacingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Eco,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.level_number, level),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Dimens.SpacingSmall),
                )
                Text(
                    text = stringResource(R.string.xp_progress, currentXp, xpForNextLevel),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LinearProgressIndicator(
                progress = { currentXp.toFloat() / xpForNextLevel },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
                strokeCap = StrokeCap.Round,
                drawStopIndicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingSmall)
                    .height(Dimens.ProgressBarHeight),
            )
        }
    }
}
```

`core/res/values/strings.xml` (и `values-en`):

```xml
<string name="level_number">ур. %1$d</string>
<string name="xp_progress">%1$d / %2$d оч.</string>
```

---

## Фаза 3. Каркас приложения: нижний бар и вложенные экраны

### Почему

Сейчас `MainActivity` оборачивает всё в `Scaffold` с `innerPadding`, а каждый раздел открывается из сетки на главной. По макету 4 раздела — вкладки, у карты контент уходит под статус-бар и под плавающий бар. Бар должен быть виден только на верхнеуровневых вкладках, а на вложенных экранах (Задачи, Профиль, Игры…) — скрыт; там остаётся `FeatureScaffold` с новым `GpTopBar`.

### Код

`core/navigation/TopLevelDestination.kt`:

```kotlin
enum class TopLevelDestination(val destination: Destination) {
    HOME(Destination.Home),
    SHOP(Destination.Shop),
    MAP(Destination.Map),
    FAVORITES(Destination.Favorites),
}
```

`app/.../navigation/GreenPassportAppShell.kt` — бар поверх `NavHost`, показывается по текущему маршруту:

```kotlin
@Composable
fun GreenPassportAppShell(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = TopLevelDestination.entries.firstOrNull { tab ->
        backStackEntry?.destination?.hasRoute(tab.destination::class) == true
    }

    Box(modifier = modifier.fillMaxSize()) {
        AppNavHost(navController = navController)
        if (currentTab != null) {
            GpBottomBar(
                selected = currentTab,
                onSelect = { tab -> navController.navigateToTopLevel(tab) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
            )
        }
    }
}
```

`navigateToTopLevel` — стандартно: `popUpTo(graph.findStartDestination().id) { saveState = true }`, `launchSingleTop = true`, `restoreState = true`.

- `MainActivity` — убираем `Scaffold`; каждый экран сам обрабатывает инсеты (`statusBarsPadding()`, для вкладок — нижний `contentPadding` под бар).
- `FeatureScaffold` переходит на `GpTopBar` и `containerColor = background`.
- `homeMenuDestinations`, `HomeMenuItem`, `ic_home_*.png`, `background.png` удаляются после фазы 4.
- `Favorites` в `:feature:profile` становится вкладкой «Избранное»: сегменты «Задачи / Советы» объединяют текущие `FavoritesScreen` и `BookmarksScreen`, чтобы вкладка не была полупустой.

---

## Фаза 4. Главный экран в модуле `:feature:home`

### Почему

Новой главной нужны данные: сессия, уровень, ближайшее событие, незавершённые задачи. В `:app` нет слоя use case, и класть туда `HomeViewModel` значит нарушить архитектуру «`domain` + `presentation` в feature-модуле». Выносим главную в `:feature:home`, который зависит только от `:core`: репозитории задач, событий и очков уже там.

### Код

`settings.gradle.kts` — `"home"` в списке фич; `app/build.gradle.kts` — `implementation(project(":feature:home"))`.

`AuthSession` дополняется `displayName: String?` (берётся из `FirebaseUser.displayName` в `FirebaseAuthRepository`), чтобы показать «Привет, Роман». Без имени — просто «Привет!».

Use cases в `feature/home/.../domain/`: `ObserveHomeSessionUseCase`, `GetLevelUseCase` (`PointsRepository.getExperience` + `LevelProgression.levelFor`), `GetUpcomingEventUseCase` (ближайшее событие с `startAtEpochMillis > now`), `GetPendingTasksUseCase` (задачи минус выполненные, первые `HOME_TASKS_LIMIT`).

ViewModel по правилам CLAUDE.md — без `init`, через `stateIn`:

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    observeSession: ObserveHomeSessionUseCase,
    private val getLevel: GetLevelUseCase,
    private val getUpcomingEvent: GetUpcomingEventUseCase,
    private val getPendingTasks: GetPendingTasksUseCase,
) : ViewModel() {

    val uiState = observeHomeUiState(observeSession()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        HomeUiState(),
    )

    private fun observeHomeUiState(sessions: Flow<AuthSession?>): Flow<HomeUiState> {
        return sessions.filterNotNull().mapLatest { session ->
            HomeUiState(
                displayName = session.displayName,
                level = getLevel(session.userId),
                upcomingEvent = getUpcomingEvent(),
                tasks = getPendingTasks(session.userId),
                isLoading = false,
            )
        }.catch { emit(HomeUiState(isLoading = false, hasError = true)) }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
```

`HomeScreen(onAvatarClick, onEventSelected, onTaskSelected, onSeeAllTasks, onQuickAction)` — `LazyColumn`: шапка (дата `EEEE, d MMM` + `headlineLarge` приветствие + аватар `AvatarSize`), `LevelProgressCard`, `HeroImageCard` события, `LazyRow` из `QuickActionTile` (Сообщество, Мини-игры, Эко-советы, Календарь, Отзывы — первые три как в макете, остальные прокручиваются), `SectionHeader` «Твои задачи / Ещё» и `GpListRow` задач с `MascotWidget`. Нижний `contentPadding` = `BottomBarHeight` + `SpacingMedium`.

---

## Фаза 5. Детали задачи и события — шторки

### Почему

По макету детали открываются шторкой поверх размытой главной. Задачу и событие открывают разные модули (главная, задачи, избранное, календарь), а фичи не могут зависеть друг от друга, поэтому шторка не может быть встроена в конкретный экран. Она должна остаться destination'ом навигации в `:app`. Используем `dialog<>`-destination: предыдущий экран остаётся видимым под ним, `SavedStateHandle` в `TaskDetailViewModel` продолжает работать без изменений, а окно диалога на API 31+ умеет размывать фон (`blurBehindRadius`), как в макете. На API < 31 — полупрозрачный мятный скрим.

### Код

`core/designsystem/component/GpSheetScaffold.kt`:

```kotlin
@Composable
fun GpSheetScaffold(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ConfigureSheetWindow()
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(interactionSource = null, indication = null, onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = Dimens.SheetCornerRadius, topEnd = Dimens.SheetCornerRadius))
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = SHEET_SURFACE_ALPHA))
                .clickable(interactionSource = null, indication = null, onClick = {})
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState {},
                    onDragStopped = { velocity -> if (velocity > DISMISS_VELOCITY) onDismiss() },
                )
                .navigationBarsPadding()
                .padding(Dimens.CardPadding),
            content = {
                SheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
                content()
            },
        )
    }
}
```

`ConfigureSheetWindow` — отдельный private composable: через `(LocalView.current.parent as DialogWindowProvider).window` выставляет `setDimAmount(0f)`, а на `Build.VERSION_CODES.S+` — `FLAG_BLUR_BEHIND` и `attributes.blurBehindRadius = SHEET_BLUR_RADIUS`. Константы `SHEET_SURFACE_ALPHA`, `DISMISS_VELOCITY`, `SHEET_BLUR_RADIUS` — в том же файле, `private const val`.

`AppNavHost`:

```kotlin
dialog<Destination.TaskDetail>(dialogProperties = SheetDialogProperties) {
    TaskDetailSheet(onDismiss = navController::popBackStack)
}
dialog<Destination.EventDetail>(dialogProperties = SheetDialogProperties) {
    EventDetailSheet(onDismiss = navController::popBackStack)
}
```

где `SheetDialogProperties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)`.

- `TaskDetailScreen` → `TaskDetailSheet`: маскот + заголовок + `+50 оч.`, описание, `GpPrimaryButton` «Отметить выполненной». После выполнения — состояние «Выполнено» и закрытие.
- Новый `Destination.EventDetail(eventId: String)` и `EventDetailSheet`/`EventDetailViewModel` в `:feature:calendar` (фото, заголовок, дата с иконкой календаря, место с иконкой пина, описание, кнопка «Записаться · +N оч.»). Регистрация — существующий `RegisterForEventUseCase`.
- `EcoEvent` получает `imageUrl: String?` и `rewardPoints: Int` (поля `imageUrl`, `rewardPoints` в `FirestoreEventsRepository` и в `scripts/seed-firestore.js`). Очки на кнопке пока только отображаются: начисление за посещение (`PointsEarnReason.EVENT_ATTENDED`) — отдельная задача, регистрация ≠ посещение.
- Пакет `feature/calendar/presentation` сейчас плоский — при добавлении файлов раскладываем в `ui/`, `viewmodels/`, `state/`.

---

## Фаза 6. Карта на Yandex MapKit

### Почему

Макет — полноэкранная карта с пинами, текущий экран — список. У нас уже есть `MapPoint.latitude/longitude`, `GetMapPointsUseCase` и сохранение точек, не хватает только самой карты. Yandex MapKit (lite-вариант: карта + метки, без навигатора) работает по бесплатному ключу из кабинета разработчика Яндекса.

### Код

`gradle/libs.versions.toml` (актуальную 4.x-lite сверяем при реализации):

```toml
yandexMapkit = "4.22.0-lite"
yandex-mapkit = { group = "com.yandex.android", name = "maps.mobile", version.ref = "yandexMapkit" }
```

Ключ — `YANDEX_MAPKIT_API_KEY` в `local.properties` (не коммитится); `feature/map/build.gradle.kts` читает его в `buildConfigField`, включает `buildFeatures.buildConfig = true`. Без ключа сборка не падает: экран карты показывает `EmptyContent` с подсказкой.

`feature/map/.../MapKitInitializer.kt` — вызывается из `GreenPassportApplication.onCreate()` (ключ обязан быть выставлен до первого `MapView`):

```kotlin
object MapKitInitializer {
    fun initialize(context: Context) {
        if (BuildConfig.YANDEX_MAPKIT_API_KEY.isBlank()) return
        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPKIT_API_KEY)
        MapKitFactory.initialize(context)
    }
}
```

`YandexMap` composable через `AndroidView` и жизненный цикл:

```kotlin
@Composable
fun YandexMap(
    points: List<MapPoint>,
    onPointClick: (MapPoint) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val pinIcon = remember { ImageProvider.fromResource(context, R.drawable.map_pin) }
    val tapListeners = remember { mutableListOf<MapObjectTapListener>() }

    LifecycleStartEffect(mapView) {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
        onStopOrDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            val mapObjects = view.mapWindow.map.mapObjects
            mapObjects.clear()
            tapListeners.clear()
            points.forEach { point ->
                val listener = MapObjectTapListener { _, _ ->
                    onPointClick(point)
                    true
                }
                tapListeners += listener
                mapObjects.addPlacemark().apply {
                    geometry = Point(point.latitude, point.longitude)
                    setIcon(pinIcon)
                    setText(point.name)
                    addTapListener(listener)
                }
            }
        },
    )
}
```

`tapListeners` держит слушатели сильными ссылками: MapKit хранит их через weak reference, и без списка тапы перестают работать после GC. `map_pin` — растровый PNG коричневого пина (`ImageProvider.fromResource` не понимает vector drawable).

`MapScreen`: карта на весь экран под статус-бар и бар вкладок; сверху — поле поиска и `GpFilterChip` по типам в полупрозрачной плашке; тап по пину → `GpSheetScaffold` внутри экрана (название, адрес, тип, «Сохранить»). Камера стартует по центру точек (`CameraPosition` по среднему). Пакет `feature/map/presentation` раскладываем в `ui/`, `viewmodels/`, `state/`. `MapViewModel` при переписывании переводим на `stateIn` без `init`.

CLAUDE.md и README: раздел «Known limitations» про карту заменяется на инструкцию по ключу MapKit.

---

## Фаза 7. Остальные экраны

### Почему

После фаз 1–2 экраны уже получают новые цвета и шрифт, но остаются на `Card`, `OutlinedCard`, `Button`, `FilterChip` и `PillListItem`, то есть сохраняют прежнюю геометрию. Их переводим на компоненты фазы 2 по одному шаблону, без изменения логики и ViewModel.

Шаблон для каждого экрана:

```kotlin
FeatureScaffold(title = …, onNavigateBack = …) { innerPadding ->
    LazyColumn(
        contentPadding = PaddingValues(
            start = Dimens.ScreenHorizontalPadding,
            end = Dimens.ScreenHorizontalPadding,
            top = innerPadding.calculateTopPadding(),
            bottom = Dimens.SpacingLarge,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
    ) {
        items(items) { item ->
            GpListRow(
                title = item.title,
                subtitle = stringResource(R.string.points_reward, item.points),
                leading = { MascotWidget(size = Dimens.IconSizeExtraLarge) },
                onClick = { onItemSelected(item.id) },
            )
        }
    }
}
```

| Группа | Экраны | Что меняется |
|---|---|---|
| Задачи | `TasksListScreen` | `GpFilterChip` по категориям, `GpListRow` с маскотом; тап → шторка |
| Магазин (вкладка) | `ShopScreen` | заголовок-вкладка вместо top bar, баланс сверху, награды в `GpSurfaceCard` 2 колонки |
| Избранное (вкладка) | `FavoritesScreen`, `BookmarksScreen` | сегменты «Задачи / Советы», `GpListRow` |
| Профиль | `ProfileScreen`, `AchievementsScreen`, `CardsScreen`, `HistoryScreen`, `NotificationsScreen`, `ExchangeScreen` | шапка как на главной (аватар + имя + `LevelProgressCard`), пункты меню — `GpListRow` |
| Календарь | `CalendarScreen` | события — `HeroImageCard` уменьшенной высоты; тап → шторка события |
| Сообщество | `CommunityHubScreen`, `ForumScreen`, `GroupsScreen` | `GpListRow`/`GpSurfaceCard` |
| Эко-советы | `EcoTipsListScreen`, `EcoTipDetailScreen` | «совет дня» — `HeroImageCard`, список — `GpListRow` |
| Игры | `GamesHubScreen`, `WasteSortingScreen`, `QuizScreen`, `PuzzleScreen`, `MazeScreen` | хаб — сетка `QuickActionTile`; в играх — только цвета, кнопки, карточки |
| Прочее | `FeedbackScreen`, `AuthScreen`, `OnboardingScreen`, `PlaceholderScreen` | поля ввода с `shapes.medium` и `surfaceContainer`, `GpPrimaryButton` |

Плоские пакеты `presentation/` (shop, map, calendar, games, profile) раскладываются в `ui/viewmodels/state` в том же коммите, где трогаем экран. Строки с новыми текстами — в `values/` (RU) и `values-en/`, ключи по содержимому.

В конце удаляются `PillListItem`, `PointsBadge`, `homeMenuDestinations`, `HomeMenuItem`, `ic_home_*.png`, `background.png`.

---

## Критичные файлы

- `core/.../designsystem/theme/{Color,Type,Theme,Dimens}.kt`, новый `Shape.kt`
- `core/.../designsystem/component/*` — новые компоненты
- `core/.../navigation/Destination.kt` (+ `EventDetail`), новый `TopLevelDestination.kt`
- `core/.../auth/{AuthSession,FirebaseAuthRepository}.kt`, `core/.../model/EcoEvent.kt`, `FirestoreEventsRepository.kt`
- `app/.../MainActivity.kt`, `GreenPassportApp.kt`, `GreenPassportApplication.kt`, `navigation/{AppNavHost,FeatureScaffold}.kt`, новый `GreenPassportAppShell.kt`
- новый модуль `feature/home/`
- `feature/map/*`, `feature/calendar/*`, `feature/tasks/.../TaskDetailScreen.kt`
- `settings.gradle.kts`, `app/build.gradle.kts`, `feature/map/build.gradle.kts`, `gradle/libs.versions.toml`
- `scripts/seed-firestore.js`

## Проверка

После каждой фазы:

```bash
./gradlew assembleDebug
./gradlew build
./gradlew detektAll
./gradlew installDebug
```

Ручные сценарии на эмуляторе (API 34, плюс API 29 для fallback без blur):

1. Главная совпадает с макетом `home`: приветствие с датой, аватар, уровень с коричневым прогрессом, hero-событие, 3 плитки, «Твои задачи», плавающий бар с активной «Главной».
2. Тап по задаче → шторка поверх размытой главной (на API 29 — поверх скрима); «Отметить выполненной» начисляет очки, прогресс уровня обновляется после закрытия; свайп вниз и тап по фону закрывают шторку.
3. Тап по hero-событию → шторка события с фото; «Записаться» регистрирует и планирует напоминание (видно в «Уведомлениях»).
4. Вкладки Магазин / Карта / Избранное переключаются с сохранением состояния; на вложенных экранах бар скрыт, «назад» работает.
5. Карта: с ключом — пины точек из Firestore, тап по пину → шторка точки, «Сохранить» появляется в избранном; без ключа — понятное пустое состояние, без крэша.
6. Аватар → профиль в новом стиле; пройти все разделы из таблицы фазы 7 — нет старых обведённых pill-карточек и зелёных плиток.
7. Тёмная тема системы — все экраны читаемы, контраст текста на карточках достаточный.
8. Английская локаль — нет захардкоженных строк.
