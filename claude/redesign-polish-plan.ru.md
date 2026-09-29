# Доводка редизайна до макета + iOS-фишки

## Контекст

Коммит `22e3151` перенёс структуру макетов (вкладки, главная, шторки, карта, компоненты), но визуально приложение на макет не похоже. Пользователь: «дизайн ты перенёс только по структуре». Замеры по пикселям `~/Downloads/green pasport/home` (590px = 393pt, 1pt ≈ 1dp) показали причины:

| Элемент | Макет | Сейчас |
|---|---|---|
| «Hello, Roman» | ~26sp | 34sp |
| «Your tasks» | ~22sp | 28sp |
| Карточка уровня | 47dp высота, радиус ~12 | ~68dp, радиус 20 |
| Быстрая плитка | 75dp, радиус ~14 | 96dp, радиус 20 |
| Строка задачи | 67dp, маскот 34dp, радиус ~12 | 80dp, маскот 48dp |
| Hero-карточка | 200dp, радиус ~20 | 240dp, радиус 36 |
| Аватар | 44dp, пустой серый круг | 56dp с иконкой |
| Поля экрана | 22dp | 20dp |
| Фон / карточка | `#F1FEF5` / `#D6F1E0` | `#EAF6EC` / `#D5EDDA` |

Плюс нет трёх вещей, на которых держится макет: иллюстрации маскота (была Canvas-заглушка), фото в hero-карточке (у событий `imageUrl = null`) и iOS-материалов: стеклянного бара и матовой плавающей шторки.

Решения пользователя: шрифт везде системный; маскот — `~/Downloads/green pasport/mascot.png` (2400×2481, прозрачный фон); iOS-фишки переносим, где возможно.

Первым действием после одобрения план сохраняется в `claude/redesign-polish-plan.ru.md`. Коммит — прямо в `main`, одним коммитом в конце. Сборку запускает пользователь.

---

## Шаг 1. Токены по замерам

### Почему

Все экраны берут размеры, радиусы и цвета из `Dimens`, `GreenPassportShapes`, `Color.kt` и `Type.kt`. Сейчас там значения «на глаз», крупнее макета на 25–40%, отсюда «калькуляторная» пухлость. Если исправить токены, подтянутся все 20+ экранов, а не только главная.

### Код

`Color.kt` — значения сняты пипеткой с макета:

```kotlin
private val MintBackgroundLight = Color(0xFFF1FEF5)
private val LeafSurfaceLight = Color(0xFFD6F1E0)
private val LeafSurfaceVariantLight = Color(0xFFC9E6D3)
private val InkLight = Color(0xFF0B0F10)
private val MossLight = Color(0xFF385247)
private val MossSecondaryLight = Color(0xFF526959)
private val BarkLight = Color(0xFF947259)
private val TrackLight = Color(0xFFC5DCD2)
private val PebbleLight = Color(0xFFD1DCD6)
```

`onSurfaceVariant = MossLight` (заголовки строк), новый `outline = MossSecondaryLight` (подписи, шевроны), `outlineVariant = TrackLight`. `PebbleLight` — цвет пустого аватара, отдаётся через `surfaceDim`.

`Type.kt` (системный шрифт, вес Medium для заголовков, как в макете):

```kotlin
headlineLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 26.sp, lineHeight = 32.sp),
headlineMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 28.sp),
titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
titleMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 21.sp),
bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 22.sp),
labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 18.sp),
```

`Dimens.kt` / `Shape.kt`:

```kotlin
val ScreenHorizontalPadding = 22.dp
val ItemSpacing = 14.dp
val LevelCardHeight = 47.dp
val QuickActionHeight = 75.dp
val ListRowHeight = 67.dp
val ListRowMascotSize = 34.dp
val HeroCardHeight = 200.dp
val AvatarSize = 44.dp
val CornerRadiusSmall = 12.dp
val CornerRadiusMedium = 14.dp
val CornerRadiusLarge = 20.dp
val CornerRadiusSheet = 32.dp
```

Радиусы в макете — iOS-«squircle»; для них используем `RoundedCornerShape`. Значения уточняем сравнением скриншотов (см. «Проверка»).

---

## Шаг 2. Маскот-иллюстрация

### Почему

Маскот стоит в каждой строке задания, в шторке, в пустых состояниях и на онбординге. Canvas-круг с рожками делает экраны «самодельными». Исходник 2400px весит 4.4 MB — в APK его класть нельзя.

### Код

Подготовка (разово, скриптом): обрезать по альфе > 8, уменьшить до 768px по большей стороне, сохранить WebP lossless → `core/src/main/res/drawable-nodpi/mascot.webp` (~100 KB).

`MascotWidget.kt` — Canvas и все его константы удаляются:

```kotlin
@Composable
fun MascotWidget(
    modifier: Modifier = Modifier,
    size: Dp = Dimens.ListRowMascotSize,
) {
    Image(
        painter = painterResource(R.drawable.mascot),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size),
    )
}
```

Размеры: строка — 34dp, шторка задания — 64dp, пустые состояния — 120dp, онбординг и вход — 200dp.

---

## Шаг 3. Фото события по умолчанию

### Почему

Hero-карточка без фото — плоский зелёный прямоугольник, а это главный визуальный акцент главной. Пока у событий в Firestore нет картинок, нужен дефолт. Фото парка из макета `11.02.56` (518×345px) подходит как временная заглушка.

### Код

Вырезать фото из `2026-09-29 11.02.56.jpg` (область шторки, x 36–554, y 447–792) → `core/src/main/res/drawable-nodpi/event_placeholder.webp`.

`NetworkImage` получает фолбэк (у coil `AsyncImage` есть `placeholder`/`error`/`fallback`):

```kotlin
@Composable
fun NetworkImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallback: Painter? = null,
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        placeholder = fallback,
        error = fallback,
        fallback = fallback,
        modifier = modifier,
        contentScale = contentScale,
    )
}
```

`HeroImageCard` и `EventDetailSheet` передают `painterResource(R.drawable.event_placeholder)`.

---

## Шаг 4. iOS-фишки

### 4.1. «Liquid glass» таб-бар

**Почему.** В макете бар — полупрозрачное стекло: контент под ним размыт, по краю тонкий светлый ободок, выбранная вкладка — плашка, которая «переезжает». Сейчас бар — просто полупрозрачная заливка с тенью.

**Код.** Библиотека Haze (`dev.chrisbanes.haze:haze:2.0.0`) — стандартное решение для backdrop-blur в Compose: на API 31+ через `RenderEffect`, ниже — автоматически полупрозрачная заливка. API сверяем с 2.0 при реализации.

```kotlin
@Composable
fun GreenPassportAppShell(modifier: Modifier = Modifier) {
    val hazeState = rememberHazeState()
    Box(modifier = modifier.fillMaxSize()) {
        AppNavHost(navController = navController, modifier = Modifier.hazeSource(hazeState))
        GpBottomBar(
            hazeState = hazeState,
            selected = visibleTab,
            onSelect = { tab -> navController.navigateToTopLevel(tab) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
```

В `GpBottomBar`: `Modifier.hazeEffect(hazeState, GlassStyle)` + `border(1.dp, Color.White.copy(alpha = RIM_ALPHA), CircleShape)`. Плашка выбора — один `Box`, смещение которого анимируется `animateDpAsState(spring(dampingRatio = Spring.DampingRatioMediumBouncy))`. Иконки: невыбранные — `onBackground` (чёрные, как в макете), выбранная — `primary`. Тот же `hazeEffect` — у плавающей панели поиска на карте.

### 4.2. Матовая плавающая шторка

**Почему.** В iOS-шторке сама шторка — матовое стекло (сквозь неё размыт контент), она отступает от краёв экрана и скруглена со всех сторон, а верх экрана лишь слегка затемнён. Сейчас размывается весь экран целиком (`FLAG_BLUR_BEHIND`), а шторка прибита к низу.

**Код.** Окно диалога сжимается до размера шторки, и размывается только область под ним — `Window.setBackgroundBlurRadius` (API 31+) размывает ровно под фоном окна по его форме:

```kotlin
@Composable
private fun ConfigureSheetWindow(cornerRadiusPx: Float, color: Int) {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window ?: return
    DisposableEffect(window) {
        window.setGravity(Gravity.BOTTOM)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setDimAmount(SHEET_DIM_AMOUNT)
        window.setBackgroundDrawable(
            GradientDrawable().apply {
                cornerRadius = cornerRadiusPx
                setColor(color)
            },
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setBackgroundBlurRadius(SHEET_BLUR_RADIUS)
        }
        onDispose {}
    }
}
```

Отступ от краёв — `InsetDrawable` (8dp) вокруг `GradientDrawable`. Ниже API 31 цвет фона непрозрачный. Появление — `slideInVertically` со spring, свайп вниз с «резинкой» — оставляем текущий `draggable`.

### 4.3. Сжатие при нажатии вместо ripple

**Почему.** На iOS карточки и кнопки при нажатии слегка «проседают», а ripple — чисто андроидный жест. Одно место — `GpSurfaceCard` — покрывает строки, плитки, hero и карточки.

**Код.** `core/designsystem/component/PressScale.kt`:

```kotlin
fun Modifier.pressScale(interactionSource: InteractionSource): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) PRESSED_SCALE else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
```

`GpSurfaceCard` при `onClick != null` создаёт `MutableInteractionSource`, передаёт его в `Surface(interactionSource = …)` и в `pressScale`, а ripple отключает через `LocalRippleConfiguration provides null`. `PRESSED_SCALE = 0.97f`.

### 4.4. Хаптика

**Почему.** iOS отвечает лёгким «тиком» на переключение вкладки и подтверждением на успешное действие — это ощутимо «дороже».

**Код.** `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.SegmentTick)` при смене вкладки в `GpBottomBar`; `HapticFeedbackType.Confirm` — при успехе «Отметить выполненной» и «Записаться» (через `LaunchedEffect(uiState.isCompleted)`/`isRegistered`).

### 4.5. Большой заголовок, который схлопывается

**Почему.** Вложенные экраны (Задания, Профиль, Игры…) в iOS-стиле открываются большим заголовком слева, который при скролле уезжает в компактную стеклянную шапку. Сейчас там статичный `GpTopBar`.

**Код.** `FeatureScaffold` переходит на `LargeTopAppBar` с `exitUntilCollapsed`:

```kotlin
val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
        LargeTopAppBar(
            title = { Text(title) },
            navigationIcon = { GpBackButton(onClick = onNavigateBack) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = SCROLLED_BAR_ALPHA),
            ),
            scrollBehavior = scrollBehavior,
        )
    },
)
```

Кнопка «назад» — круглая стеклянная, из текущего `GpTopBar` (выносится в `GpBackButton`). `GpTopBar` остаётся для карты без ключа.

---

## Шаг 5. Экраны с макетами — один в один

### Почему

Токены дают масштаб, но расположение и мелочи задаются в самих экранах: в макете аватар — пустой серый круг, у плиток иконка и текст одного цвета мха, у строки заголовок мхом и подпись светлее, у шторки события иконки контурные, а описание обрезается многоточием.

- **Главная** (`HomeScreen`): дата 14sp Medium, приветствие `headlineLarge`, аватар 44dp `surfaceDim` без иконки (если нет фото), отступы между блоками `ItemSpacing`, `LevelProgressCard` высотой `LevelCardHeight` (одна строка + бар 4dp внизу), плитки `QuickActionHeight`, строки `ListRowHeight` с маскотом 34dp, заголовок секции `headlineMedium`, «Ещё» `primary` 15sp.
- **Шторка задания**: маскот 64dp, заголовок `titleLarge` (SemiBold, как SF Bold в макете), «+50 оч.» `bodyLarge` цвета `outline`, описание `bodyLarge` `outline`, кнопка 52dp на всю ширину, шторка минимум 55% высоты экрана (в макете под текстом воздух).
- **Шторка события**: фото с радиусом `CornerRadiusLarge`, заголовок `headlineLarge`, строки даты и места с `Icons.Outlined.CalendarMonth` / `Icons.Outlined.Place`, описание `maxLines = 2` + `TextOverflow.Ellipsis`, кнопка «Записаться · +50 оч.».
- **Карта**: убираем сплошную шапку. Сверху плавающая стеклянная капсула поиска (Haze) и чипы фильтров, пины — как сейчас (коричневый круг + подпись), логотип Яндекса над баром.
- **Бар**: высота 60dp, иконки 24dp, плашка выбора ~92dp шириной.

---

## Шаг 6. Остальные экраны

### Почему

У них нет макетов, но после шагов 1–4 они автоматически получают новые размеры, маскот, сжатие при нажатии и большой заголовок. Руками правим только то, что не выражается токенами.

- Все `LazyColumn`/`LazyRow`: `verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing)`, поля `ScreenHorizontalPadding`.
- Строки с иконками (Профиль, Сообщество, Эко-советы): иконка 22dp цвета `onSurfaceVariant` в том же месте, где маскот.
- Пустые состояния и ошибки: маскот 120dp.
- `CalendarScreen`: карточки событий с фото-заглушкой, высота 160dp.

---

## Критичные файлы

- `core/.../designsystem/theme/{Color,Type,Dimens,Shape}.kt`
- `core/.../designsystem/component/{MascotWidget,NetworkImage,GpSurfaceCard,GpBottomBar,GpSheetScaffold,HeroImageCard,LevelProgressCard,QuickActionTile,GpListRow}.kt`, новые `PressScale.kt`, `GpBackButton.kt`
- `core/src/main/res/drawable-nodpi/{mascot,event_placeholder}.webp`
- `app/.../navigation/{GreenPassportAppShell,FeatureScaffold}.kt`
- `feature/home/.../HomeScreen.kt`, `feature/tasks/.../TaskDetailSheet.kt`, `feature/calendar/.../{EventDetailSheet,CalendarScreen}.kt`, `feature/map/.../MapScreen.kt`
- `gradle/libs.versions.toml` (+ `haze`), `core/build.gradle.kts`, `app/build.gradle.kts`, `feature/map/build.gradle.kts`

Уже существующее и переиспользуемое: `GpSheetScaffold` (drag-to-dismiss), `SheetDialogProperties`, `TopLevelDestination`, `LifecycleResumeEffect`-обновление главной, `formatEventDate/Time`.

## Проверка

Пользователь собирает:

```bash
./gradlew build
./gradlew detektAll
./gradlew installDebug
```

Сравнение с макетом один к одному: эмулятор с геометрией iPhone из макета —

```bash
adb shell wm size 1179x2556
adb shell wm density 480
```

(393×852dp, как 393×852pt в макете). Скриншот главной (`adb exec-out screencap -p > home.png`), уменьшить до 590px и наложить на `home` с прозрачностью 50% — я делаю это скриптом и правлю расхождения больше 2dp. `adb shell wm size reset && adb shell wm density reset` — вернуть.

Ручные сценарии:
1. Главная совпадает с `home`: размеры, радиусы, маскоты в строках, фото в hero, чёрные иконки бара и коричневая выбранная.
2. Скролл главной — под баром виден размытый контент (API 31+); на API 29 — полупрозрачная заливка.
3. Переключение вкладок — плашка переезжает с пружинкой, лёгкий тик вибрации.
4. Тап по задаче — плавающая матовая шторка со скруглением со всех сторон, контент под ней размыт, верх слегка затемнён; свайп вниз закрывает; «Отметить выполненной» — вибрация-подтверждение.
5. Нажатие на карточку — карточка «проседает» без ripple.
6. Вложенный экран (Задания) — большой заголовок схлопывается при скролле.
7. Карта — стеклянная капсула поиска поверх карты, логотип Яндекса не перекрыт баром.
