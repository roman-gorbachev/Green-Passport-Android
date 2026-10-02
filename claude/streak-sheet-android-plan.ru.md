# План: карточка прогресса, шторка серии с напоминанием и красный «Выход» — на Android

После одобрения план сохраняется в `claude/streak-sheet-android-plan.ru.md` в Android-репо первым действием. Сборку и lint запускает пользователь (по его просьбе я не собираю); detekt-правила соблюдаются при написании.

## Контекст

На iOS сделаны три изменения, и они записаны в спецификации (`claude/ux-spec.ru.md`, 6.4 и 6.8). В «Android backlog» висят три открытых пункта:
- карточка прогресса без подписи «xp/1000», капсула серии «🔥 N», нажатие открывает шторку серии;
- шторка серии и вечернее напоминание в 20:00 через WorkManager, без записи в журнал;
- «Выход»: текст и иконка красные.

Паритет обязателен (CLAUDE.md обоих репо). Отличия Android от iOS в коде:
- **«Выход».** Иконка уже красная (`tint = colorScheme.error`), а текст обычного цвета — нужно покрасить текст.
- **Напоминания.** Воркеры (`CouponReminderWorker`, `EventReminderWorker`) пишут в журнал и проверяют переключатель уведомлений **в момент срабатывания**, а не при постановке. Поэтому воркеру серии достаточно не писать в журнал. Снимать напоминание при выключении уведомлений не нужно: воркер сам проверит переключатель.
- **Серия.** Модель `Streak(count, lastDay)` уже есть в `:core`, у неё есть `currentCount(nowEpochMillis)` (Europe/Minsk). Главная получает её через `ObserveStreakUseCase`.

---

## 1. Карточка прогресса (`:core`)

`core/designsystem/component/ProgressHeroCard.kt`:
- удаляется `Text(stringResource(R.string.xp_progress, …))` под полосой. Строка `xp_progress` удаляется из `core/res/values*/strings.xml`, если больше не используется (grep);
- `StreakCapsule` показывает только число, а для TalkBack — полную фразу:

```kotlin
val description = pluralStringResource(R.plurals.streak_days_in_row, days, days)
Row(
    modifier = modifier
        .semantics(mergeDescendants = true) { contentDescription = description }
        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(Dimens.CornerRadiusPill))
        .padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingHairline),
) {
    Icon(imageVector = Icons.Filled.LocalFireDepartment, contentDescription = null, ...)
    Text(text = days.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondary)
}
```

- новый параметр `onClick: (() -> Unit)? = null` передаётся в `GpSurfaceCard(onClick = onClick, …)`. Магазин и профиль его не передают.

## 2. Расчёт серии (`:core/model`)

**Почему в модели:** расчёт — чистая логика над `Streak`, как и `currentCount`. Его нужно и шторке, и решению о напоминании.

`core/model/Streak.kt` получает:

```kotlin
fun isCounted(nowEpochMillis: Long): Boolean = lastDay == dayKey(nowEpochMillis)

fun summary(nowEpochMillis: Long, firstDayOfWeek: DayOfWeek): StreakSummary
```

Правила те же, что на iOS (`Streak+Summary.swift`):
- активные дни — `days` дней подряд, заканчивающихся на `lastDay`;
- неделя — от `firstDayOfWeek` (по локали, `WeekFields.of(locale)` в UI);
- дни до бонуса: следующий бонусный день — `(days / 7 + 1) * 7`, отсчёт от `days`, если сегодня засчитан, иначе от `days + 1`. 0 означает «бонус сегодня».

Для `Streak(null)` используется `Streak.summary(null, …)` в companion, чтобы шторка открывалась и без серии.

`core/model/StreakSummary.kt` и `core/model/StreakWeekDay.kt`:

```kotlin
data class StreakSummary(
    val days: Int,
    val isTodayCounted: Boolean,
    val daysUntilBonus: Int,
    val week: List<StreakWeekDay>,
)

data class StreakWeekDay(val date: LocalDate, val isActive: Boolean, val isToday: Boolean)
```

## 3. Шторка серии (`:feature:home`)

**`HomeUiState`:** вместо `streakDays: Int` — `streak: Streak?`, а `streakDays` становится вычисляемым. `HomeProgress` в `HomeViewModel` тоже хранит `Streak?`.

**`presentation/ui/StreakSheet.kt`** — `ModalBottomSheet` (как `TaskFiltersSheet`):
- крупно `pluralStringResource(R.plurals.streak_days_in_row, …)` с огнём `secondary`;
- лента недели: 7 кружков `CalendarDaySize` с буквой дня (`DayOfWeek.getDisplayName(TextStyle.NARROW_STANDALONE, locale)`). Активный — `secondary` с огнём, сегодня — обводка `primary`;
- статус `streak_today_counted` (цвет `primary`) или `streak_today_pending`;
- `streak_bonus_today` или `pluralStringResource(R.plurals.streak_bonus_in_days, …)`;
- мелко `streak_rule`.

```kotlin
@Composable
fun StreakSheet(summary: StreakSummary, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ScreenHorizontalPadding)
                .padding(bottom = Dimens.SpacingLarge),
        ) {
            StreakHeadline(days = summary.days)
            StreakWeekStrip(week = summary.week)
            StreakStatus(summary = summary)
        }
    }
}
```

`HomeScreen` держит `var isStreakSheetOpen by rememberSaveable { mutableStateOf(false) }`, передаёт `onClick = { isStreakSheetOpen = true }` в `ProgressHeroCard` и показывает шторку с `Streak.summary(uiState.streak, System.currentTimeMillis(), WeekFields.of(locale).firstDayOfWeek)`.

Строки — в `feature/home/res/values{,-be,-en}/strings.xml`, тексты те же, что на iOS:
- `<plurals>`: `streak_days_in_row`, `streak_bonus_in_days` (ru/be: one/few/many/other; en: one/other);
- `streak_today_counted`, `streak_today_pending`, `streak_bonus_today`, `streak_rule`.

`streak_days_in_row` нужен и карточке в `:core`, поэтому эти plurals живут в `core/res`.

## 4. Напоминание о серии (`:core/messaging` + `:feature:home`)

`ReminderScheduler` (`core/messaging/helpers`):

```kotlin
fun scheduleStreakReminder(streakDays: Int, triggerAtEpochMillis: Long)
fun cancelStreakReminder()
```

**`WorkManagerReminderScheduler`:** уникальная работа `streak_reminder`, `ExistingWorkPolicy.REPLACE`, `StreakReminderWorker` с задержкой до 20:00. `cancelStreakReminder` — `cancelUniqueWork`.

**`core/messaging/worker/StreakReminderWorker.kt`** — как `CouponReminderWorker`, но **без** `notificationLogRepository().log(...)`:
- проверяет `observeNotificationsEnabled()` и разрешение `POST_NOTIFICATIONS`;
- показывает `streak_reminder_title` / `streak_reminder_body` (строки в `core/res`, тексты как на iOS);
- постоянный id уведомления `STREAK_NOTIFICATION_ID`.

**`feature/home/domain/UpdateStreakReminderUseCase.kt`** — те же правила, что на iOS:

```kotlin
class UpdateStreakReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler,
) {
    operator fun invoke(streak: Streak?, nowEpochMillis: Long) {
        val now = Instant.ofEpochMilli(nowEpochMillis).atZone(SERVER_ZONE)
        val fireAt = now.toLocalDate().atTime(REMINDER_HOUR, 0).atZone(SERVER_ZONE).toInstant().toEpochMilli()
        if (streak == null || streak.currentCount(nowEpochMillis) == 0 || streak.isCounted(nowEpochMillis) || fireAt <= nowEpochMillis) {
            reminderScheduler.cancelStreakReminder()
        } else {
            reminderScheduler.scheduleStreakReminder(streak.count, fireAt)
        }
    }

    private companion object {
        const val REMINDER_HOUR = 20
        val SERVER_ZONE: ZoneId = ZoneId.of("Europe/Minsk")
    }
}
```

`HomeViewModel`: поток серии получает `.onEach { updateStreakReminder(it, System.currentTimeMillis()) }`, до `combine`.

## 5. Красный «Выход» (`:core` + `:feature:profile`)

**Почему через параметр:** у `ListSectionRow` / `ListRowContent` нет цвета заголовка, а сырой `Text` вместо компонента нарушил бы дизайн-систему.

- `ListRowContent(…, titleColor: Color = Color.Unspecified)` — `Unspecified` означает текущий `onSurface`;
- `ListSectionRow` передаёт параметр дальше;
- в `ProfileScreen` у строки выхода `titleColor = MaterialTheme.colorScheme.error`. Иконка уже красная.

## 6. Документация

- Спецификация (обе копии): три пункта backlog → `[x]`.
- Android `CLAUDE.md`, раздел про уведомления: `StreakReminderWorker` не пишет в журнал, его ставит и снимает `UpdateStreakReminderUseCase` с главной.
- Коммит в Android-репо. iOS-изменения и правка спецификации в iOS-репо пока не закоммичены: ждут сборки iOS у пользователя.

---

## Порядок работ

1. План → `claude/streak-sheet-android-plan.ru.md`.
2. `:core`:
   - `Streak` + `StreakSummary` / `StreakWeekDay`;
   - plurals и строки;
   - `ProgressHeroCard`;
   - `ListRowContent` / `ListSectionRow`;
   - `ReminderScheduler` + `WorkManagerReminderScheduler` + `StreakReminderWorker`.
3. `:feature:home`: `UpdateStreakReminderUseCase`, `HomeViewModel`, `HomeUiState`, `StreakSheet`, `HomeScreen`, строки.
4. `:feature:profile`: цвет «Выхода».
5. Спецификация, CLAUDE.md, коммит.

## Проверка (у пользователя)

```bash
./gradlew build
./gradlew detektAll
```

Ручные сценарии:
1. **Главная:**
   - в карточке нет «xp/1000», капсула «🔥 5»;
   - TalkBack читает «5 дней подряд»;
   - нажатие открывает шторку: неделя, статус дня и «до бонуса» верные.
2. **Напоминание:**
   - при живой серии и без активности сегодня в `adb shell dumpsys jobscheduler` (или App Inspection → Background Task Inspector) есть работа `streak_reminder`;
   - после выполнения задания её нет;
   - при выключенных уведомлениях уведомление не показывается;
   - в журнал уведомлений не попадает.
3. **Профиль:** «Выход» — красные и текст, и иконка.
4. Светлая и тёмная тема.
