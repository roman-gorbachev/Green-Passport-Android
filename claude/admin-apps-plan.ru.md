# Приложения под админку: архив, новые ошибки, картинка акции

Этап 8 плана `greenpassport-admin/claude/admin-panel-plan.ru.md` (§4). Бэкенд уже выкачен в коде (`claude/admin-backend-plan.ru.md`): админка архивирует контент (`isActive: false`), сервер отвечает `qr_not_active`, `qr_limit_reached` и `reward_sold_out`, у акции появилась `imageUrl`. Порядок работ: спецификация `claude/ux-spec.ru.md`, затем Android, затем iOS.

---

## 1. Архив скрывается из каталогов, но не из ссылок

### Почему

В админке удаление — это «архивировать» (`isActive: false`), потому что на документ ссылаются прогресс, покупки и избранное. Сейчас приложение не читает `isActive`, и архивное задание остаётся в списке.

План §4 предлагал фильтр в репозиториях. Так делать нельзя: те же списки служат справочником по id.
- `CouponsViewModel` и `CouponDetailViewModel` ищут название акции в `observeRewards()`. Купон архивной акции остался бы без названия.
- `FirestoreHistoryRepository` берёт названия заданий, событий и акций из `getTasks()`, `getEvents()`, `getRewards()`.
- `TaskDetailViewModel`, `EventDetailViewModel`, `EcoTipDetailViewModel` и очередь модерации находят документ по id в полном списке.

Поэтому репозитории отдают всё, модели несут флаг, а фильтруют только каталоги.

### Код

```kotlin
data class Task(
    val id: String,
    val title: LocalizedText,
    val description: LocalizedText,
    val category: TaskCategory,
    val city: String,
    val rewardPoints: Int,
    val rewardXp: Int,
    val imageUrl: String?,
    val verification: TaskVerification,
    val isActive: Boolean = true,
)
```

```kotlin
private const val FIELD_IS_ACTIVE = "isActive"

isActive = getBoolean(FIELD_IS_ACTIVE) ?: true,
```

Каталоги, где архив скрыт: список заданий, задания на главной, ближайшее событие на главной, календарь, эко-советы, карта, каталог магазина, «Избранное» и «Закладки».

```kotlin
class ObserveTasksUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    operator fun invoke(): Flow<List<Task>> = tasksRepository.observeTasks().map { tasks -> tasks.filter { it.isActive } }
}
```

Календарь и шторка события используют один `ObserveEventsUseCase`, а шторке нужен и архивный документ. Поэтому для календаря добавляется отдельный use case. Так же сделано с магазином: `ObserveRewardsUseCase` остаётся справочником для купонов.

```kotlin
class ObserveRewardCatalogUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    operator fun invoke(): Flow<List<Reward>> = shopRepository.observeRewards().map { rewards -> rewards.filter { it.isActive } }
}
```

---

## 2. Новые ошибки сервера

### Почему

`toRewardFailure()` раскладывает ошибки только по коду. `resource-exhausted` сейчас означает только дневной лимит, поэтому лимит QR-кода показался бы как `daily_limit_reached_msg`. Окно QR (`failed-precondition`) показалось бы как «нужен другой способ подтверждения». А магазин на любую ошибку покупки пишет «недостаточно баллов», значит «акция закончилась» тоже прочиталась бы как нехватка баллов.

### Код

Сообщения сервера проверяются раньше кодов:

```kotlin
enum class RewardFailure {
    DAILY_LIMIT_REACHED,
    ALREADY_COMPLETED,
    INVALID_CODE,
    NOT_ENOUGH_POINTS,
    WRONG_VERIFICATION,
    QR_CODE_NOT_ACTIVE,
    QR_CODE_LIMIT_REACHED,
    REWARD_SOLD_OUT,
    NETWORK,
    UNKNOWN,
}
```

```kotlin
internal fun Throwable.toRewardFailure(): RewardFailure = when {
    this is FirebaseNetworkException -> RewardFailure.NETWORK
    this !is FirebaseFunctionsException -> RewardFailure.UNKNOWN
    message == QR_NOT_ACTIVE -> RewardFailure.QR_CODE_NOT_ACTIVE
    message == QR_LIMIT_REACHED -> RewardFailure.QR_CODE_LIMIT_REACHED
    message == REWARD_SOLD_OUT -> RewardFailure.REWARD_SOLD_OUT
    code == FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED -> RewardFailure.DAILY_LIMIT_REACHED
    else -> RewardFailure.UNKNOWN
}
```

Строки (ru / be / en) в модулях, которые их показывают:

| Ключ | Модуль | ru |
|---|---|---|
| `qr_code_not_active_msg` | `:feature:tasks` | QR-код сейчас не действует. Проверь сроки задания |
| `qr_code_limit_reached_msg` | `:feature:tasks` | Этот QR-код уже отсканировали максимальное число раз |
| `reward_sold_out` | `:feature:shop` | Купоны этой акции закончились |

Магазин хранит причину ошибки покупки вместо флага `hasInsufficientPoints`:

```kotlin
@StringRes
fun purchaseFailureMessageRes(failure: RewardFailure): Int = when (failure) {
    RewardFailure.NOT_ENOUGH_POINTS -> R.string.shop_insufficient_points
    RewardFailure.REWARD_SOLD_OUT -> R.string.reward_sold_out
    RewardFailure.NETWORK -> R.string.no_internet_connection
    else -> R.string.something_went_wrong_msg
}
```

Динамический QR события новых ошибок не даёт: старый код приходит как `not-found` и показывается существующей `event_code_does_not_match_msg`.

---

## 3. Картинка акции

### Почему

Админка загружает `imageUrl` акции, но строка каталога её не показывает. Поле необязательное, без картинки строка выглядит как сейчас.

### Код

```kotlin
reward.imageUrl?.let { url ->
    NetworkImage(
        url = url,
        contentDescription = null,
        modifier = Modifier
            .padding(end = Dimens.SpacingCompact)
            .size(Dimens.RewardThumbnailSize)
            .clip(RoundedCornerShape(Dimens.CornerRadiusMedium)),
    )
}
```

---

## 4. Спецификация

В `claude/ux-spec.ru.md` (одинаково в обоих репозиториях):
- 1, общие принципы: архивный контент скрыт из каталогов и остаётся в истории, купонах и шторках по ссылке;
- 6.6: `qr_code_not_active_msg`, `qr_code_limit_reached_msg`;
- 6.7: QR события может быть динамическим (экран организатора), старый снимок — `event_code_does_not_match_msg`;
- 6.10: картинка акции, `reward_sold_out`;
- 6.20: QR купона ведёт в кабинет партнёра, гасит кассир.

---

## Проверка

```bash
./gradlew build
./gradlew detektAll
```

iOS — сборка и тесты в Xcode (`Green Passport.xcodeproj`).

Сценарии (эмулятор или прод с выкаченным бэкендом):
1. Заархивировать задание в админке: оно пропадает из списка, с главной и из избранного. В истории и в шторке по ссылке остаётся.
2. Заархивировать акцию, по которой есть купон: в каталоге её нет, в «Моих купонах» купон с названием.
3. QR-задание с окном в будущем: при скане `qr_code_not_active_msg`. С лимитом 1: второй пользователь видит `qr_code_limit_reached_msg`.
4. Акция с пулом без свободных кодов: при обмене `reward_sold_out`, а не «недостаточно баллов».
5. У акции с картинкой в каталоге миниатюра, без картинки строка не меняется.
