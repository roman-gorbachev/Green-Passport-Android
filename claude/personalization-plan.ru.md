# План 1. Вход через Google, пошаговая регистрация, персонализация, шапка по центру, фильтр мата

## Контекст

Пользователь просит:
- **Шапку экранов:** заголовок меньше и по центру, круглая кнопка «назад» остаётся слева.
- **Вход через Google.**
- **Нормальную регистрацию**, где формы сменяют друг друга: сначала почта, потом имя и фамилия и так далее. То есть персонализацию.
- **Анализ фич:** что ещё добавить, как проверять выполнение заданий, модерация. Обязательно — проверка на непристойный контент.

Решения пользователя:
- **Проверка заданий:** комбинация по типу задания — фото с модерацией, QR-код или честное слово с лимитом.
- **Модерация:** экран модератора прямо в приложении, роль admin.
- **Тариф:** Blaze подключить можно, поэтому возможна серверная проверка текста и фото.
- **Порядок работ:** два плана.
  - **План 1 (этот):** шапка, Google, регистрация по шагам, профиль и персонализация, фильтр мата в тексте (на устройстве и в правилах Firestore).
  - **План 2:** проверка заданий, роль модератора, жалобы, очередь модерации, Cloud Functions с проверкой текста и фото (Cloud Vision SafeSearch). Его набросок — в конце этого файла.

Что сейчас в коде:
- **Профиля нет.** Приветствие берёт `AuthSession.displayName`, а у почтовых аккаунтов оно всегда пустое. Поэтому «Привет, Роман» никогда не показывается.
- **Регистрация.** Это одна форма «почта + пароль» с тремя кнопками.
- **Форум.** Показывает только дату поста, без автора.
- **`User`** (`core/model/User.kt`) нигде не используется.
- **Пользовательский текст пишется в Firestore без каких-либо проверок:** посты форума, сообщения, названия групп, отзывы.
- **`app/google-services.json` без OAuth-клиентов.** Провайдер Google в Firebase не включён, и строки `default_web_client_id` нет.

Первым действием план сохраняется в `claude/personalization-plan.ru.md`. Сборка — у пользователя (в облаке нет Android SDK); я прогоняю detekt CLI. Коммит — в `claude/zealous-keller-iygmdf`.

---

## Шаг 1. Шапка: заголовок меньше и по центру

### Почему

`headlineLarge` (26sp) рядом с кнопкой 44dp выглядит тяжело. Заголовок, прижатый влево, съезжает, когда у экрана нет кнопки «назад». Центрированный `titleLarge` (20sp) — привычная мобильная шапка. Справа ставится пустое место шириной с кнопку, чтобы центр был настоящим.

### Код

`ScreenHeaderRow` в `core/designsystem/component/ScreenHeader.kt`:

```kotlin
Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
) {
    Box(modifier = Modifier.size(Dimens.BackButtonSize)) {
        if (onNavigateBack != null) {
            GpBackButton(onClick = onNavigateBack)
        }
    }
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = Dimens.SpacingSmall),
    )
    Spacer(modifier = Modifier.size(Dimens.BackButtonSize))
}
```

Сворачивание шапки при скролле остаётся как есть.

---

## Шаг 2. Профиль пользователя в `:core`

### Почему

Персонализации нужен источник правды, который переживёт смену устройства: имя, фамилия, город, интересы, аватар. `displayName` из Firebase Auth для этого не подходит: `AuthStateListener` не сообщает об изменении профиля, а город и интересы туда не положить. Профиль хранится в том же документе `apps/greenpassport/users/{uid}`, где уже лежат очки.

### Код

`core/model/profile/UserProfile.kt`:

```kotlin
data class UserProfile(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val city: String,
    val interests: Set<TaskCategory>,
    val avatar: AvatarStyle,
) {
    val displayName: String get() = firstName
}
```

`core/model/profile/AvatarStyle.kt` — это фон маскота, выбор из цветов разделов:

```kotlin
enum class AvatarStyle { LIME, FOREST, SKY, SUNSET, BERRY, VIOLET }
```

`core/model/profile/UserProfileRepository.kt`:

```kotlin
interface UserProfileRepository {
    fun observeProfile(userId: String): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
}
```

`core/datasource/remote/repository/FirestoreUserProfileRepository.kt` — ручной маппинг через `FIELD_*`, как в соседних репозиториях. Запись идёт через `set(…, SetOptions.merge())`, чтобы не затереть `availablePoints`/`lifetimeXp`:

```kotlin
override suspend fun saveProfile(profile: UserProfile) {
    FirestoreCollections.users(firestore).document(profile.userId).set(
        mapOf(
            FIELD_FIRST_NAME to profile.firstName,
            FIELD_LAST_NAME to profile.lastName,
            FIELD_CITY to profile.city,
            FIELD_INTERESTS to profile.interests.map { it.name },
            FIELD_AVATAR to profile.avatar.name,
            FIELD_PROFILE_COMPLETED_AT to System.currentTimeMillis(),
        ),
        SetOptions.merge(),
    ).await()
}
```

`observeProfile` — `snapshots()`, возвращает `null`, пока нет `profileCompletedAt`. Привязка — в `RemoteDataSourceModule`. Доступные города — `core/model/profile/SupportedCities.kt` (`listOf("Москва", "Санкт-Петербург")`), те же, что в `seed-firestore.js`.

---

## Шаг 3. Вход через Google

### Почему

Регистрация по почте и паролю — самый медленный путь для молодёжи. Google даёт вход в одно касание и сразу имя и фамилию для профиля. Для Android рекомендован Credential Manager: старый `GoogleSignInClient` устарел.

### Что нужно от пользователя в Firebase Console

1. **Authentication → Sign-in method → Google →** включить.
2. **Project settings → Android-приложение `com.smartcity.greenpassport` →** добавить SHA-1 и SHA-256. Ключи берутся из `./gradlew signingReport`: debug и release.
3. Скачать обновлённый `google-services.json` в `app/`. После этого плагин google-services сам создаёт строку `R.string.default_web_client_id`.

### Код

`libs.versions.toml`:

```toml
credentials = "1.5.0"
googleId = "1.1.1"

androidx-credentials = { group = "androidx.credentials", name = "credentials", version.ref = "credentials" }
androidx-credentials-play-services = { group = "androidx.credentials", name = "credentials-play-services-auth", version.ref = "credentials" }
googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleId" }
```

`AuthRepository` получает новый метод, а `FirebaseAuthRepository` его реализует через уже существующий `mapAuthFailures`:

```kotlin
override suspend fun signInWithGoogle(idToken: String): AuthSession = mapAuthFailures {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    firebaseAuth.signInWithCredential(credential).await().requireUser().toAuthSession()
}
```

`AuthSession` получает `suggestedFirstName`/`suggestedLastName` из `AdditionalUserInfo`/`FirebaseUser.displayName`, чтобы предзаполнить шаг «Имя».

`core/auth/GoogleIdTokenRequester.kt` — Credential Manager требует контекст Activity, поэтому вызывается из UI:

```kotlin
class GoogleIdTokenRequester @Inject constructor() {
    suspend fun requestIdToken(activityContext: Context, webClientId: String): String {
        val option = GetSignInWithGoogleOption.Builder(webClientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val credential = CredentialManager.create(activityContext).getCredential(activityContext, request).credential
        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }
}
```

`AuthFailure` получает `GOOGLE_CANCELLED` (закрыл окно — ошибку не показываем) и `GOOGLE_UNAVAILABLE` (нет аккаунта Google, не настроен SHA-1). Строки — `google_sign_in_unavailable_msg`.

---

## Шаг 4. Регистрация по шагам

### Почему

Одна форма «почта + пароль» ничего не узнаёт о человеке. Нужен мастер, где формы сменяют друг друга. Он же используется после первого входа через Google (начиная с шага «Имя») и для редактирования профиля.

### Поток

```
Вход ──«Создать аккаунт»──► Шаг 1: почта + пароль + повтор пароля (или «Продолжить с Google»)
                                   │ аккаунт создан
                                   ▼
                            Шаг 2: имя, фамилия
                                   ▼
                            Шаг 3: город (чипы из SupportedCities)
                                   ▼
                            Шаг 4: интересы (чипы TaskCategory, минимум 1)
                                   ▼
                            Шаг 5: аватар (маскот на цветном фоне) → «Готово»
```

Анонимный вход мастер пропускает. У вошедшего по почте или через Google, но без профиля, `MainViewModel` показывает мастер со шага 2.

### Код

`app/.../AppStartupState.kt` получает состояние `NeedsProfile`. `MainViewModel` комбинирует ещё и профиль:

```kotlin
val startupState = combine(onboardingSeen, sessionWithProfile) { seen, (session, profile) ->
    when {
        !seen -> AppStartupState.NeedsOnboarding
        session == null -> AppStartupState.NeedsAuth
        !session.isAnonymous && profile == null -> AppStartupState.NeedsProfile
        else -> AppStartupState.Ready
    }
}
```

`sessionWithProfile` = `observeSession().flatMapLatest { session -> … observeProfile(session.userId).map { session to it } }`.

`feature/auth/.../presentation/state/RegistrationStep.kt`:

```kotlin
enum class RegistrationStep { ACCOUNT, NAME, CITY, INTERESTS, AVATAR }
```

`RegistrationUiState` хранит текущий шаг, поля всех шагов и ошибки по полям. `RegistrationViewModel`:
- `onNext()` проверяет текущий шаг;
- на шаге `ACCOUNT` создаёт аккаунт через `RegisterWithEmailUseCase`;
- на шаге `AVATAR` вызывает `SaveUserProfileUseCase`.

`onBack()` возвращает на предыдущий шаг, но не раньше `NAME`, если аккаунт уже создан.

`RegistrationScreen`:
- сверху индикатор шагов: 5 точек, текущая тёмно-зелёная и шире;
- посередине `AnimatedContent` по шагу со сдвигом влево/вправо;
- внизу `GpPrimaryButton` «Далее» / «Готово».

```kotlin
AnimatedContent(
    targetState = uiState.step,
    transitionSpec = {
        val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
        (slideInHorizontally { width -> width * direction } + fadeIn()) togetherWith
            (slideOutHorizontally { width -> -width * direction } + fadeOut())
    },
    label = "registrationStep",
) { step ->
    when (step) {
        RegistrationStep.ACCOUNT -> AccountStep(…)
        RegistrationStep.NAME -> NameStep(…)
        RegistrationStep.CITY -> CityStep(…)
        RegistrationStep.INTERESTS -> InterestsStep(…)
        RegistrationStep.AVATAR -> AvatarStep(…)
    }
}
```

`AuthScreen` (вход) и мастер переключаются внутри `:feature:auth` через `AuthMode { SIGN_IN, REGISTER }` с тем же `AnimatedContent`. Это «переключаются формы» без отдельного навигационного графа до входа. На экране входа добавляется кнопка «Продолжить с Google» (обводка, логотип G из `drawable/ic_google.xml`).

Проверки на шагах:
- почта — `Patterns.EMAIL_ADDRESS`;
- пароль — не меньше 6 символов и совпадает с повтором;
- имя и фамилия — от 2 до 30 символов, только буквы, пробел и дефис, без мата (шаг 6);
- интересы — хотя бы один.

Все сообщения лежат в строковых ресурсах `:feature:auth`.

---

## Шаг 5. Персонализация в приложении

### Почему

Профиль имеет смысл, только если приложение его использует. Сейчас на главной одинаковые задания для всех городов, а форум безымянный.

### Код

- **Главная:** `HomeViewModel` берёт профиль через `ObserveUserProfileUseCase`.
  - Приветствие: «Привет, {firstName} 👋».
  - Аватар в шапке — `ProfileAvatar(style)` в `:core`: маскот на круге цвета стиля. Этот же компонент используется в профиле и на форуме.
  - Задания сортируются: сначала город пользователя, потом его интересы.

  ```kotlin
  class GetPendingTasksUseCase @Inject constructor(…) {
      suspend operator fun invoke(userId: String, profile: UserProfile?): List<Task> {
          val completedIds = tasksRepository.getCompletedTaskIds(userId)
          return tasksRepository.getTasks()
              .filterNot { it.id in completedIds }
              .sortedWith(
                  compareByDescending<Task> { it.city == profile?.city }
                      .thenByDescending { profile != null && it.category in profile.interests },
              )
              .take(HOME_TASKS_LIMIT)
      }
  }
  ```

- **Задания:** первый чип фильтра — «Для тебя» (город + интересы), он выбран по умолчанию.
- **Профиль:** в шапке `ProfileAvatar`, имя и фамилия, город. Новый пункт меню «Редактировать профиль» открывает `Destination.EditProfile`: тот же мастер с шагами 2–5 и кнопкой «Сохранить», в `FeatureScaffold`.
- **Форум:** при публикации в пост записываются `authorName` и `authorAvatar` (денормализация, чтобы не читать профиль на каждый пост). Карточка поста показывает аватар, имя и дату.

---

## Шаг 6. Фильтр непристойного текста

### Почему

Требование пользователя — обязательно проверять контент. Уровень 1 (этот план):
- проверка на устройстве, с понятной ошибкой ещё до отправки;
- та же проверка по основным корням в правилах Firestore, чтобы обойти её в обход приложения было нельзя.

Уровень 2 (план 2, Blaze): Cloud Function перепроверяет всё на сервере, включая фото.

Нормализация обязательна: «х у й», «xyй», «хуууй» и «ё/е» должны ловиться.

### Код

`core/moderation/TextModerator.kt`:

```kotlin
interface TextModerator {
    fun isAllowed(text: String): Boolean
}
```

`core/moderation/WordListTextModerator.kt` — словарь корней из `core/src/main/res/raw/banned_roots.txt` (русские и английские корни, по одному в строке) плюс белый список ложных срабатываний (`raw/allowed_words.txt`: «оскорблять», «застрахуй»… — формируем при наполнении):

```kotlin
class WordListTextModerator @Inject constructor(
    @ApplicationContext context: Context,
) : TextModerator {

    private val bannedRoots by lazy { context.readLines(R.raw.banned_roots) }
    private val allowedWords by lazy { context.readLines(R.raw.allowed_words).toSet() }

    override fun isAllowed(text: String): Boolean {
        val words = normalize(text).split(WORD_SEPARATOR).filter { it.isNotBlank() }
        val joined = words.joinToString(separator = "")
        return words.none { word -> word !in allowedWords && bannedRoots.any(word::contains) } &&
            bannedRoots.none(joined::contains)
    }

    private fun normalize(text: String): String = text.lowercase()
        .replace('ё', 'е')
        .map { char -> LOOKALIKES[char] ?: char }
        .joinToString(separator = "")
        .replace(REPEATED_LETTERS, "$1")

    companion object {
        private val WORD_SEPARATOR = Regex("[^a-zа-я]+")
        private val REPEATED_LETTERS = Regex("(.)\\1+")
        private val LOOKALIKES = mapOf('a' to 'а', 'e' to 'е', 'o' to 'о', 'p' to 'р', 'c' to 'с', 'x' to 'х', 'y' to 'у', 'k' to 'к', 'm' to 'м', 'h' to 'н', 'b' to 'в', 't' to 'т', '0' to 'о', '3' to 'з', '@' to 'а')
    }
}
```

Проверка по склеенному тексту ловит «х у й» ценой редких ложных срабатываний на стыке слов. Если они окажутся заметны на реальных постах, склейку оставим только для однобуквенных фрагментов.

Проверка встраивается в use case перед записью. Результат — `ContentRejectedException`, UI показывает «Текст содержит недопустимые слова»:

```kotlin
class PostToForumUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val textModerator: TextModerator,
) {
    suspend operator fun invoke(author: UserProfile, text: String) {
        if (!textModerator.isAllowed(text)) throw ContentRejectedException()
        communityRepository.postToForum(author, text.trim())
    }
}
```

Где проверяем:
- посты форума;
- сообщения чата;
- названия групп;
- текст отзыва;
- имя и фамилия в мастере.

Правила Firestore (`firestore.rules`) — общая функция и лимиты длины:

```
function isClean(text) {
  return text is string && text.size() <= 2000 &&
    !text.lower().matches('.*(хуй|хуе|хуё|пизд|ебат|ебан|ебал|ебу|бляд|блят|сука|мудак|мудил|пидор|пидар|залуп|шлюх|fuck|shit|cunt|bitch).*');
}
```

Эта функция применяется:
- к `posts.create` (`isClean(request.resource.data.text)`);
- к `messages.create`;
- к `groups.create` (`name`);
- к `feedback.create` (`message`);
- к `users` — к `firstName` и `lastName`, если поля есть в записи.

`groups.update` сужается до изменения одного поля `memberIds` (`request.resource.data.diff(resource.data).affectedKeys().hasOnly(['memberIds'])`): сейчас любой может переименовать чужую группу во что угодно.

---

## Анализ фич: что ещё стоит добавить (после планов 1 и 2)

| Фича | Зачем | Оценка |
|---|---|---|
| Серия дней (streak) и ежедневный челлендж | Главный механизм возврата в приложение для молодёжи | Средне |
| Рейтинг по городу и среди друзей | Соревновательность; опирается на город из профиля | Средне |
| Поделиться достижением / карточкой (картинка в соцсети) | Бесплатный рост аудитории | Мало |
| Push «ты давно не заходил» и «новое событие в твоём городе» | Возвраты, есть FCM и WorkManager | Мало |
| Реферальный код (очки за приглашённого) | Рост, завязан на профиль | Средне |
| Командные задания для групп сообщества | Даёт группам смысл, сейчас они пустые | Много |

Предлагаю начать со «Серии дней + ежедневного челленджа» и «Рейтинга по городу» — отдельным планом после плана 2.

## План 2 (следующий, кратко)

- **Проверка заданий.** Задание в Firestore получает `verification: PHOTO | QR | SELF`:
  - PHOTO — фото в Storage → `taskSubmissions/{id}` со статусом `PENDING` → модератор одобряет или отклоняет → очки.
  - QR — сканер (CameraX + ML Kit Barcode) на мероприятии или в пункте приёма; код подписан (`taskId` + секрет), и Cloud Function проверяет его и начисляет очки.
  - SELF — сразу, но мало очков и не больше N в день.
- **Очки только на сервере.** Начисление переезжает в Cloud Functions, а клиенту запрещается писать `availablePoints`/`lifetimeXp`. Это закрывает накрутку.
- **Роль admin.** Документ `admins/{uid}` и экран «Модерация» в профиле: очередь фото, жалобы, кнопки «Одобрить / Отклонить / Скрыть».
- **Жалобы.** Кнопка «Пожаловаться» на постах и сообщениях → `reports`; три жалобы автоматически скрывают контент до решения модератора.
- **Cloud Functions (Blaze).** Серверная перепроверка текста тем же словарём; Cloud Vision SafeSearch для загруженных фото — «LIKELY»/«VERY_LIKELY» adult/violence/racy отклоняются автоматически.

---

## Критичные файлы (план 1)

- `core/.../designsystem/component/ScreenHeader.kt`, новый `ProfileAvatar.kt`
- `core/.../model/profile/*`, `core/.../datasource/remote/repository/FirestoreUserProfileRepository.kt`, `core/.../di/RemoteDataSourceModule.kt`
- `core/.../auth/{AuthRepository,FirebaseAuthRepository,AuthFailure,GoogleIdTokenRequester}.kt`
- `core/.../moderation/*`, `core/src/main/res/raw/{banned_roots,allowed_words}.txt`
- `app/.../{MainViewModel,AppStartupState,GreenPassportApp}.kt`, `app/.../navigation/AppNavHost.kt`, `core/.../navigation/Destination.kt`
- `feature/auth/.../presentation/{ui,state,viewmodels}/*` (вход, мастер регистрации)
- `feature/home/...`, `feature/tasks/...`, `feature/profile/...`, `feature/community/...` (персонализация, фильтр)
- `firestore.rules`, `gradle/libs.versions.toml`, `core/build.gradle.kts`, `feature/auth/build.gradle.kts`, `README.md`

Переиспользуем:
- `mapAuthFailures`/`AuthFailure` (`FirebaseAuthRepository`);
- `GpTextField`, `GpPrimaryButton(isLoading)`, `GpFilterChip`, `MascotWidget`, `IconCircle`, `SectionColors`;
- `FeatureScaffold`, `FirestoreCollections.users`, `SetOptions.merge()`-паттерн из `FirestorePointsRepository`.

## Проверка

```bash
./gradlew build
./gradlew detektAll
./gradlew installDebug
firebase deploy --only firestore:rules --project chatroom-85fb8
```

Сценарии:
1. **Шапка:** «Задания», «Профиль», «Магазин» — заголовок 20sp по центру, кнопка «назад» слева, на вкладках без кнопки заголовок тоже по центру.
2. **Регистрация по почте:** почта и пароль (несовпадение повтора → ошибка) → имя «Роман» / фамилия → город → интересы (без выбора кнопка «Далее» не проходит) → аватар → главная с «Привет, Роман 👋» и выбранным аватаром.
3. **Назад в мастере:** стрелка возвращает на предыдущий шаг анимацией вправо; после создания аккаунта назад на шаг «Почта» не пускает.
4. **Первый вход через Google:** мастер со второго шага с предзаполненными именем и фамилией. **Повторный вход** — сразу главная. **Отмена окна Google** — без ошибки.
5. **Анонимный вход:** мастер не показывается.
6. **Персонализация:** на главной первыми идут задания своего города и интересов; «Для тебя» выбран в заданиях по умолчанию; «Редактировать профиль» меняет город, и порядок заданий обновляется.
7. **Фильтр:**
   - имя с матом → ошибка на шаге «Имя»;
   - пост «х у й», «xyй», «хуууй» → «Текст содержит недопустимые слова», пост не создан;
   - обычный пост «оскорблять нельзя» проходит;
   - запись поста с матом в обход приложения (Rules Playground в консоли) → отказ правил.
8. **Форум:** у новых постов видны аватар и имя автора.
9. **Тёмная тема:** мастер и чипы читаемы.
