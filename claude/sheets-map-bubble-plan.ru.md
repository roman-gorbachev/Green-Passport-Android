# План: стандартные шторки на Android, непрозрачная панель на карте, пузырь маскота в одну строку

После одобрения план сохраняется в `claude/sheets-map-bubble-plan.ru.md` (Android-репо) первым действием. Изменения UX сначала вносятся в `claude/ux-spec.ru.md` (обе копии). Сборку запускает пользователь.

## Контекст

Замечания пользователя по Android:
1. **Шторки** (задание, событие, купон, точка карты) — собственный `GpSheetScaffold`:
   - тянуть шторку можно только за самодельный маркер вверху (`SheetDragHandle` с `pointerInteropFilter`);
   - нужна стандартная механика Material: закрытие свайпом вниз из любого места, без маркера.
2. **Панель вкладок на карте.** Яндекс-карта рисуется отдельной поверхностью (SurfaceView), Haze её не размывает, и под полупрозрачной панелью карта просвечивает сквозь подписи вкладок.
3. **Пузырь маскота в карточке прогресса.** «Ещё 400 оч. до ур. 3» не помещается в `SpeechBubbleMaxWidth = 132.dp` и переносится на вторую строку, ломая вёрстку.

На iOS у пузыря то же ограничение ширины (`bubbleMaxWidth = 132`) и разрешён перенос, поэтому пункт 3 делается на обеих платформах.

---

## 1. Шторки на `ModalBottomSheet` (Android, `:core`)

**Почему:** `ModalBottomSheet` из Material 3 сам умеет то, что нужно:
- закрывается свайпом вниз из любого места;
- если контент прокручивается — свайпом, начатым у верха прокрутки (nested scroll);
- закрывается тапом по затемнению и кнопкой «назад»;
- анимирует появление и скрытие.

Замена внутренностей `GpSheetScaffold` чинит все четыре шторки сразу, сигнатура (`onDismiss`, `modifier`, `content`) не меняется.

`core/designsystem/component/GpSheetScaffold.kt`:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpSheetScaffold(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window
    if (window != null) {
        DisposableEffect(window) {
            window.setDimAmount(0f)
            onDispose {}
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        shape = RoundedCornerShape(topStart = Dimens.CornerRadiusSheet, topEnd = Dimens.CornerRadiusSheet),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier.padding(
                start = Dimens.SheetContentPadding,
                top = Dimens.SheetContentPadding,
                end = Dimens.SheetContentPadding,
                bottom = Dimens.CardPadding,
            ),
            content = content,
        )
    }
}
```

**Затемнение окна.** Шторки задания, события и купона открываются как `dialog<>` назначения навигации. Окно диалога остаётся (оно держит предыдущий экран под шторкой), но его собственное затемнение обнуляется: затемнение теперь рисует `ModalBottomSheet`. Иначе было бы двойное. Нижний отступ под жесты `ModalBottomSheet` добавляет сам (`contentWindowInsets` по умолчанию).

**Удаляется:**
- `SheetDragHandle`, `DragTracker`, константы `DISMISS_*` и `SHEET_DIM_AMOUNT`;
- `Dimens.SheetHandleWidth`, `SheetHandleHeight`, `SheetDragZoneHeight` (если нигде больше не используются — grep).

**Остальные шторки.** У `TaskFiltersSheet` и `StreakSheet`, которые уже на `ModalBottomSheet`, тоже `dragHandle = null`, чтобы все шторки выглядели одинаково.

## 2. Непрозрачная панель вкладок на карте (Android, `:core` + `:app`)

**Почему:** на SurfaceView стекла не получится, остаётся только полупрозрачная подкраска, сквозь которую видна карта. На этой вкладке панель делается сплошной; на остальных стекло остаётся.

`GlassBottomBarLayout(…, isOpaque: Boolean = false)` → `GpBottomBar(…, hazeState = if (isOpaque) null else hazeState)`. Без `hazeState` панель уже рисует сплошной фон `surfaceContainer`.

`GreenPassportAppShell`: `isOpaque = visibleTab == TopLevelDestination.MAP`.

## 3. Пузырь маскота в одну строку (обе платформы)

**Почему:** фраза почти влезает, перенос из-за нескольких символов. Одна строка с автоматическим уменьшением шрифта в небольших пределах сохраняет вёрстку и не обрезает число.

**Android**, `ProgressHeroCard.SpeechBubble`: `Text` с `maxLines = 1` и `autoSize = TextAutoSize.StepBased(minFontSize = BUBBLE_MIN_FONT_SIZE, maxFontSize = labelSmall.fontSize)`, где `BUBBLE_MIN_FONT_SIZE = 9.sp`. Ширина остаётся ограниченной `SpeechBubbleMaxWidth`.

```kotlin
Text(
    text = text,
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.onSurface,
    textAlign = TextAlign.Center,
    maxLines = 1,
    autoSize = TextAutoSize.StepBased(
        minFontSize = BUBBLE_MIN_FONT_SIZE,
        maxFontSize = MaterialTheme.typography.labelSmall.fontSize,
    ),
    modifier = modifier
        .widthIn(max = Dimens.SpeechBubbleMaxWidth)
        .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(Dimens.CornerRadiusSmall))
        .padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingExtraSmall),
)
```

**iOS**, `ProgressHeroCard.swift`: у текста пузыря `.lineLimit(1)` и `.minimumScaleFactor(Self.bubbleMinimumScale)`, где `bubbleMinimumScale = 0.8`.

## 4. Спецификация (обе копии)

- **Раздел 1:** шторки на Android — стандартный `ModalBottomSheet` без маркера: закрываются свайпом вниз из любого места, тапом по затемнению и кнопкой «назад». На iOS — системные sheet.
- **Раздел 2:** на вкладке «Карта» панель непрозрачная: карта рисуется отдельной поверхностью, и стекло её не размывает.
- **Раздел 4:** реплика маскота в карточке прогресса — всегда одна строка, шрифт при нехватке места уменьшается.

---

## Порядок работ

1. План → Android `claude/sheets-map-bubble-plan.ru.md`.
2. Спецификация (обе копии).
3. Android: `GpSheetScaffold`, `dragHandle = null` у двух шторок, `GlassBottomBarLayout` и оболочка, `SpeechBubble`.
4. iOS: пузырь.
5. Коммита нет, пока пользователь не соберёт и не попросит.

## Проверка (у пользователя)

```bash
./gradlew build && ./gradlew detektAll
```

и сборка iOS в Xcode.

1. **Шторки** задания, события, купона и точки карты, а также фильтров и серии:
   - маркера нет;
   - свайп вниз из любого места закрывает;
   - в длинной шторке свайп вниз от верха прокрутки закрывает, а в середине — прокручивает;
   - тап по затемнению и «назад» закрывают;
   - нет двойного затемнения.
2. **Вкладка «Карта»:** панель сплошная, подписи читаются. На остальных вкладках стекло как раньше.
3. **Карточка прогресса:** реплика «Ещё 400 оч. до ур. 3» в одну строку на ru/be/en (Android и iOS).
