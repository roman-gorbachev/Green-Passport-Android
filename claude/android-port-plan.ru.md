# План: перенос обновлений iOS на Android

После одобрения план сохраняется в `claude/android-port-plan.ru.md` в Android-репозитории первым действием (по CLAUDE.md обоих репозиториев). По мере выполнения пункты «Android backlog» в `claude/ux-spec.ru.md` отмечаются `[x]` (обе копии).

## Контекст

На iOS сделаны:
- правки UX: кнопки, плитки, профиль, достижения, календарь, советы, тема;
- перевод контента;
- новые игры и сетка игр с PNG-иконками.

Общие части уже работают на обеих платформах: веб-игры, seed, Cloud Functions, обложки и иконки на Hosting. Всё остальное записано в спецификации как 12 открытых пунктов Android backlog. Сейчас Android:
- показывает весь контент по-русски;
- игнорирует `imageUrl`, `titles`, `bodies` у советов, а у игр — `iconPath`, `iconEmoji`, `iconColors`;
- не знает иконок новых игр (`GameIcon.kt` содержит только старые имена, у всех 10 игр — запасная иконка);
- в профиле держит дубли («Мои карты», «Обмен», «Избранное», «Закладки»);
- в календаре показывает просто ленту.

Цель — паритет сценариев с iOS. Контролы остаются родными для Material 3.

**Ограничения проекта** (Android `CLAUDE.md`):
- граф модулей `:app → :core + :feature:*`, фичи друг от друга не зависят;
- Coil подключён только в `:core`, доступен через `NetworkImage`;
- строки — в `values`, `values-be`, `values-en` каждого модуля;
- проверка — `./gradlew build` (lint) и `./gradlew detektAll` (`maxIssues: 0`);
- эмулятора нет, визуальная проверка — у пользователя.

---

## 1. Кнопки с загрузкой (раздел 4)

**Почему:** сейчас спиннер заменяет текст (`GpPrimaryButton`) или всю кнопку (группы, магазин, отзывы), и размер прыгает. Решение как на iOS: подпись остаётся в раскладке прозрачной, поверх — маленький спиннер.

`core/designsystem/component/LoadingLabel.kt`:

```kotlin
@Composable
fun LoadingLabel(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    content: @Composable () -> Unit,
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Box(modifier = Modifier.alpha(if (isLoading) 0f else 1f)) { content() }
        if (isLoading) {
            CircularProgressIndicator(
                color = color,
                strokeWidth = Dimens.ProgressStrokeWidth,
                modifier = Modifier.size(Dimens.LoadingIndicatorSize),
            )
        }
    }
}
```

`Dimens.LoadingIndicatorSize = 16.dp`.

**`GpPrimaryButton`.** Во время загрузки кнопка не выключается визуально: она остаётся `enabled`, а `onClick` игнорируется. Иначе Material красит её в серый.

```kotlin
Button(onClick = { if (!isLoading) onClick() }, enabled = enabled, ...) {
    LoadingLabel(isLoading = isLoading, color = MaterialTheme.colorScheme.onPrimary) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
```

**Остальные места:**
- `MessageComposer`: стрелка внутри `LoadingLabel`.
- `GroupsScreen`: «Создать» и «Вступить» — кнопки остаются, подпись в `LoadingLabel`.
- `JoinByCodeDialog`: в подтверждении `LoadingLabel` — сейчас там нет индикатора совсем.
- `ShopScreen.RewardRow`: «Купить» — то же.
- `FeedbackScreen`:
  - отзыв и предложение — `LoadingLabel` в кнопке;
  - опрос — варианты остаются на месте, выключаются, у выбранного спиннер.

## 2. Плитки иконок: символ 42 % (раздел 4)

**Почему:** у широких Material-иконок (`Groups`, `Forum`) символ 50 % плитки выглядит крупно. На Android `Icon` и так вписывает вектор в квадрат, поэтому достаточно уменьшить долю.

`SymbolTile.kt`:

```kotlin
const val SYMBOL_TILE_DEFAULT_ICON_FRACTION = 0.42f
```

Быстрые действия на главной оставляют свои 0.38.

## 3. Профиль (6.8)

**Почему:** «Мои карты» дублируют достижения, «Обмен» — заглушка про карты, «Избранное» и «Закладки» повторяют вкладку «Избранное» (в ней уже есть переключатель «Задания / Советы» и встроенный `BookmarksScreen`).

`ProfileScreen.profileMenuEntries` — только достижения, купоны, история и уведомления:

```kotlin
private val profileMenuEntries = listOf(
    ProfileMenuEntry(R.string.profile_achievements, Icons.Filled.EmojiEvents, Destination.Achievements) { it.tips },
    ProfileMenuEntry(R.string.my_coupons, Icons.Filled.ConfirmationNumber, Destination.Coupons) { it.community },
    ProfileMenuEntry(R.string.profile_history, Icons.Filled.History, Destination.History) { it.calendar },
    ProfileMenuEntry(R.string.profile_notifications_label, Icons.Filled.Notifications, Destination.Notifications) { it.feedback },
)
```

**Удаляется:**
- `Destination.Cards`, `Destination.Exchange`, `Destination.Bookmarks` (маршрут; сам `BookmarksScreen` остаётся во вкладке) и их `composable` в `AppNavHost`;
- `CardsScreen.kt`, `ExchangeScreen.kt`;
- строки `profile_cards`, `cards_screen_title`, `cards_locked_label`, `profile_exchange`, `exchange_screen_title`, `exchange_unavailable_message`, `bookmarks_screen_title` — если на них больше нет ссылок, проверить grep. `profile_favorites` / `profile_bookmarks` остаются, если используются как подписи кнопок.

**Тема:** у `DropdownMenuItem` убирается `leadingIcon`, плитка строки остаётся.

## 4. Достижения с прогрессом (6.9)

**Почему:** у модели есть только `isUnlocked`; экрану нужен прогресс «2 из 5».

`core/model/Achievement.kt`:

```kotlin
data class Achievement(val id: AchievementId, val progress: Int, val target: Int) {
    val isUnlocked: Boolean get() = progress >= target
    val clampedProgress: Int get() = progress.coerceAtMost(target)
}
```

`AchievementsRepositoryImpl` возвращает прогресс с теми же порогами, что на iOS (1, 5, 1, 3, 1, 5):

```kotlin
return listOf(
    Achievement(AchievementId.FIRST_TASK, completedTaskCount, FIRST_TASK_THRESHOLD),
    Achievement(AchievementId.TASK_MASTER, completedTaskCount, TASK_MASTER_THRESHOLD),
    Achievement(AchievementId.EVENT_GOER, registeredEventCount, EVENT_GOER_THRESHOLD),
    Achievement(AchievementId.ECO_READER, readTipCount, ECO_READER_THRESHOLD),
    Achievement(AchievementId.COMMUNITY_MEMBER, if (isCommunityMember) 1 else 0, COMMUNITY_MEMBER_THRESHOLD),
    Achievement(AchievementId.LEVEL_FIVE, level, LEVEL_FIVE_THRESHOLD),
)
```

**`AchievementsScreen`:**
- сверху `achievements_unlocked_format` («Открыто 2 из 6»);
- ниже `LazyVerticalGrid(GridCells.Fixed(2))` карточек `GpSurfaceCard`:
  - `SymbolTile` со своей иконкой: `Eco`, `Verified`, `EventAvailable`, `MenuBook`, `Groups`, `Star`;
  - название и условие;
  - у открытого — `achievement_unlocked_label` с галочкой и фон `surfaceContainerHigh`;
  - у закрытого — `LinearProgressIndicator` и `achievement_progress_format`.

Иконки — в `AchievementLabel.kt` (`achievementIcon(id)`). Новые строки ru/be/en совпадают с iOS: `achievement_unlocked_label`, `achievement_progress_format`, `achievements_unlocked_format`.

## 5. Календарь (6.11)

**Почему:** `DatePicker` Material 3 не умеет рисовать числа на днях. Нужна своя месячная сетка на `java.time` (kotlinx-datetime в проекте нет, остальной код уже на `java.time`).

**`feature/calendar/presentation/ui/MonthCalendar.kt`:**
- шапка «Октябрь 2026» со стрелками;
- ряд дней недели, где первый день — по локали (`WeekFields.of(locale).firstDayOfWeek`);
- сетка 7×N `YearMonth`;
- выбранный день — круг `primary`;
- сегодня — обводка;
- под числом — капсула `primary` с количеством событий.

```kotlin
@Composable
fun MonthCalendar(
    month: YearMonth,
    selectedDay: LocalDate,
    eventCounts: Map<LocalDate, Int>,
    onMonthChange: (YearMonth) -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
)

@Composable
private fun DayCell(day: LocalDate, isSelected: Boolean, isToday: Boolean, count: Int?, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).padding(vertical = Dimens.SpacingExtraSmall)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(Dimens.CalendarDaySize).clip(CircleShape).background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)) {
            Text(day.dayOfMonth.toString(), color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
        }
        EventCountBadge(count)
    }
}
```

**`CalendarUiState`:** добавляются `selectedDay: LocalDate` и `visibleMonth: YearMonth`. `CalendarViewModel` при первом успешном ответе выбирает ближайший день с событием начиная с сегодня, а если таких нет — сегодня. Методы:

```kotlin
fun onDaySelected(day: LocalDate) { selection.update { it.copy(selectedDay = day, visibleMonth = YearMonth.from(day)) } }
fun onMonthChange(month: YearMonth) { selection.update { it.copy(visibleMonth = month) } }
```

**`CalendarScreen` (`LazyColumn` с `contentPadding`):**
- `MonthCalendar` в `GpSurfaceCard` с отступом `Dimens.CardPadding`;
- заголовок с выбранной датой (`EEEE, d MMMM`);
- `HeroImageCard` событий дня или `calendar_no_events_on_day`.

День события — `Instant.ofEpochMilli(...).atZone(ZoneId.systemDefault()).toLocalDate()`, в `core/common/EventDateTimeFormat.kt` (`eventDay(epochMillis)`).

## 6. Перевод контента (раздел 1)

**Почему не в мапперах, как на iOS:**
- На iOS смена языка перезапускает приложение.
- На Android `AppCompatDelegate.setApplicationLocales` пересоздаёт Activity, но ViewModel и уже смапленные данные переживают это (`WhileSubscribed(5000)`). Строки, выбранные в маппере, остались бы на старом языке до следующего снимка Firestore.

Поэтому модели несут все переводы, а язык выбирается при отображении, как уже сделано у игр (`Game.title(languageCode)` + `LocalLocale`).

`core/model/LocalizedText.kt`:

```kotlin
data class LocalizedText(val fallback: String, val translations: Map<String, String> = emptyMap()) {
    fun resolve(languageCode: String): String = translations[languageCode] ?: fallback
}
```

`core/designsystem/text/LocalizedTextResolve.kt`:

```kotlin
@Composable
fun LocalizedText.localized(): String = resolve(LocalLocale.current.platformLocale.language)
```

`core/datasource/remote/DocumentSnapshotLocalized.kt`:

```kotlin
fun DocumentSnapshot.localizedText(field: String, translationsField: String): LocalizedText? {
    val fallback = getString(field) ?: return null
    val translations = (get(translationsField) as? Map<*, *>)
        ?.mapNotNull { (key, value) -> (key as? String)?.let { language -> (value as? String)?.let { language to it } } }
        ?.toMap()
        .orEmpty()
    return LocalizedText(fallback, translations)
}
```

`localizedTextList` — то же для `optionLists` (`Map<String, List<String>>`).

**Поля моделей становятся `LocalizedText`:**

| Модель | Поля |
|---|---|
| `Task` | `title`, `description` |
| `EcoEvent` | `title`, `description`, `location` |
| `Reward` | `title`, `partnerName` |
| `MapPoint` | `name`, `address` |
| `EcoTip` | `title`, `body` |
| `SurveyQuestion` | `question`; `options` — `LocalizedTextList` с `resolve(language): List<String>` |

Экраны вызывают `.localized()`. Логика во ViewModel (поиск по названию точки на карте) получает язык из `AppLanguageRepository` / `Locale.getDefault().language` через use case. Места вызова находит компилятор.

**Города:**
- `core/designsystem/text/CityName.kt` — `@Composable fun cityName(city: String): String` по 12 ключам `city_minsk`… в `core/src/main/res/values*/strings.xml` (те же ключи, что на iOS);
- вызывается в профиле (`city_and_points`), анкете, фильтрах и чипах заданий, строках заданий на главной и в избранном.

Спецификация, раздел 1: правило «язык выбирается при чтении документа» уточняется до поведения. Текст контента — на языке приложения, при смене языка переключается сразу, русское поле — запасной вариант. В «Сознательных различиях платформ» — строка: iOS выбирает язык в маппере (смена языка перезапускает приложение), Android — при отображении (Activity пересоздаётся, а данные во ViewModel остаются).

## 7. Советы (6.15)

**`EcoTip`:**
- `title`, `body` — `LocalizedText`;
- `imageUrl: String?`;
- маппер читает `titles`, `bodies`, `imageUrl`.

**Markdown** — без библиотеки, тот же формат, что на iOS:
- `core/common/ArticleBlock.kt` (`Heading` / `Paragraph` / `Bullet`, `parse()`);
- `core/designsystem/component/ArticleText.kt` — блоки в `Column`, строчные `**жирный**` и ссылки через `AnnotatedString`. Билдер `buildAnnotatedString` с `SpanStyle(fontWeight = Bold)` по парам `**`; ссылки `LinkAnnotation.Url`.

`EcoTip+Reading` → `core/model/EcoTipReading.kt`:

```kotlin
private const val WORDS_PER_MINUTE = 180
fun EcoTip.readMinutes(body: String): Int = max(1, ceil(body.split(Regex("\\s+")).size / WORDS_PER_MINUTE.toDouble()).toInt())
fun articlePreview(body: String): String = ArticleBlock.parse(body).firstOrNull { it is ArticleBlock.Paragraph }?.plainText.orEmpty()
```

**Список:** строка-карточка с миниатюрой 64 dp (`NetworkImage`, запасной вариант — `SymbolTile` категории), заголовком, превью в 2 строки, меткой «категория · N мин» и галочкой прочитанного. «Совет дня» — превью вместо пустого места под заголовком.

**Экран совета:**
- обложка 220 dp (`NetworkImage`, без обложки — градиент `primary` с иконкой категории);
- мета «категория · `ecotip_read_minutes_format`»;
- заголовок, `ArticleText`;
- у видео — кнопка `watch_video` (`GpPrimaryButton`-тональная) вместо голого URL;
- строки ru/be/en: `ecotip_read_minutes_format`, `watch_video`.

## 8. Игры: иконки и плитки (6.17)

**`Game`:** `iconPath: String?`, `iconEmoji: String?`, `iconColors: List<String>`; маппер читает их.

**URL иконки** строится в `:feature:games`, рядом с `gameUrl()`, из `BuildConfig.GAMES_BASE_URL`:

```kotlin
fun gameIconUrl(game: Game): String? = game.iconPath?.let {
    Uri.parse(BuildConfig.GAMES_BASE_URL).buildUpon().appendEncodedPath(it).build().toString()
}
```

**`GameIcon.kt`:** все 10 имён из каталога (`DirectionsRun`, `Inventory`, `Waves`, `Forest`, `GridView`, `Psychology`, `Lightbulb`, `EmojiNature`, `PedalBike`, `Style`). Нужны для запасного варианта и доступности.

**`GamesHubScreen`** — `LazyVerticalGrid(GridCells.Fixed(2), verticalArrangement = spacedBy(SpacingLarge), horizontalArrangement = spacedBy(SpacingMedium))`, ячейки прижаты к верху.

**`GameTile` (`hub/ui/GameTile.kt`):**
- квадрат со скруглением `CornerRadiusLarge`;
- `NetworkImage(gameIconUrl(game))` поверх градиента `iconColors` с `iconEmoji` (запасной вариант — `Text` эмодзи, «дыхание» `rememberInfiniteTransition` 1 ↔ 1.06, 1.8 с);
- название `minLines = 2, maxLines = 2`, рекорд.

Нажатие — пружина: `interactionSource.collectIsPressedAsState()` → `animateFloatAsState(if (pressed) 0.92f else 1f, spring(dampingRatio = 0.5f))` + поворот −3° + `HapticFeedbackType.TextHandleMove`.

Hex-цвета разбираются в `GameIconColors.kt` (`Color(android.graphics.Color.parseColor(hex))` в `runCatching`, запасной — `primary`).

---

## Порядок работ

1. План → `claude/android-port-plan.ru.md` (Android-репо).
2. **Основа в `:core`:** `LoadingLabel`, `SymbolTile`, `LocalizedText` и хелперы, `CityName` и строки городов, `ArticleBlock` / `ArticleText`, модели. `./gradlew :core:compileDebugKotlin` после каждого шага.
3. **Мапперы и все места вызова** (компилятор находит их).
4. **Фичи по одной:** profile → calendar → ecotips → games → shop / community / feedback / tasks / home / map (вызовы `.localized()`, `cityName`, `LoadingLabel`).
5. `./gradlew build` и `./gradlew detektAll`; новые находки detekt исправляются, а не заносятся в baseline.
6. Спецификация (обе копии): 12 пунктов backlog → `[x]`. Android `CLAUDE.md`: `LocalizedText`, `LoadingLabel`, `MonthCalendar`, `ArticleText`, `GameTile`.
7. Коммит в Android-репо (iOS — только спецификация).

## Проверка

```bash
./gradlew build          # assemble + lint + unit tests
./gradlew detektAll      # maxIssues: 0
```

Ручные сценарии (у пользователя на эмуляторе или устройстве, светлая и тёмная тема; до этого — `firebase deploy --only hosting,functions` и `node scripts/seed-firestore.js`):
1. **Кнопки:** «Отметить прочитанным», вход, отправка на форуме, «Создать группу», «Купить», отзыв — размер кнопки не меняется, спиннер маленький.
2. **Профиль:**
   - нет «Мои карты», «Обмен», «Избранное», «Закладки»;
   - меню темы — только текст;
   - «Достижения» — сетка с прогрессом.
3. **Календарь:**
   - месячная сетка, на 17 октября «3», на 7 ноября «2»;
   - выбор дня меняет список;
   - листание месяцев;
   - пустой день — «В этот день событий нет».
4. **Язык Профиль → Язык → English / Беларуская:**
   - задания, события, награды, точки карты, опрос, советы и города переключаются сразу, без перезапуска и без ожидания нового снимка.
5. **Советы:**
   - обложки и превью в списке;
   - в статье подзаголовки, списки, жирный текст;
   - у видео кнопка «Смотреть видео».
6. **Игры:**
   - сетка из 10 PNG-иконок, ровные ряды;
   - пружина и вибро-отклик при нажатии;
   - без сети — градиент с эмодзи.
