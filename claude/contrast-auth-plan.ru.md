# Белый фон и тени, нижняя панель с равными отступами, доработка авторизации и деплой правил Firebase

## Контекст

После редизайна (`a8973b4`) пользователь отметил:

- **Карточки и кнопки сливаются с фоном.** Фон `#F1FEF5` отличается от белых карточек примерно на 1%, поэтому граница карточки не видна. Решение пользователя: инвертировать палитру — фон белый, карточки мятные. Кнопки остаются насыщенно-тёмно-зелёными, а бледно-зелёные кнопки вернули бы проблему «всё бледное». Дополнительно всем карточкам и кнопкам добавляется мягкая тень.
- **Нижняя панель сливается с фоном**, отступы слева и справа (22dp) больше нижнего (8dp). Нужно сделать одинаковые отступы со всех сторон и добавить заметную тень.
- **Флоу авторизации.** На любую ошибку экран показывает одно и то же «Не получилось войти». Пустой email уходит прямо в Firebase и падает с `IllegalArgumentException`. Если в консоли не включён способ входа, пользователь не узнаёт, в чём дело. После входа пароль остаётся в `AuthViewModel`: она живёт в области Activity, и при выходе из аккаунта поля оказываются уже заполнены.
- **Правила Firebase.** Пользователь спросил про «JSON-ы с правилами». Правила Firestore и Storage — это не JSON, а текстовые `firestore.rules` и `storage.rules`. Они уже лежат в репозитории и совпадают с кодом: коллекции `apps/greenpassport/*`, поля `userId`/`authorId`/`senderId`, составных индексов нет (запросы используют только `whereEqualTo` по одному полю или `orderBy` по одному полю). Решение пользователя: деплоим правила из репозитория.

Первым действием план сохраняется в `claude/contrast-auth-plan.ru.md`. Сборку и детект пользователь запускает у себя (в облаке нет Android SDK); я прогоняю detekt CLI. Коммит — в ветку `claude/zealous-keller-iygmdf`.

---

## Шаг 1. Инверсия палитры: белый фон, мятные карточки

### Почему

Контраст «карточка/фон» должен давать цвет, а не только тень. Белый фон + мятная карточка `#E6F5EC` дают видимую границу. Тёмно-зелёные кнопки, блок прогресса и салатовые бейджи на белом становятся ещё ярче. Шторка и облачко маскота остаются белыми (`surface` / `surfaceContainerLowest`), чтобы не смешиваться с карточками внутри.

### Код

`Color.kt` (светлая тема, тёмная не меняется):

```kotlin
private val WhiteBackgroundLight = Color(0xFFFFFFFF)
private val MintCardLight = Color(0xFFE6F5EC)
private val MintCardLowLight = Color(0xFFF1FAF4)
private val MintSurfaceLight = Color(0xFFD6F1E0)
private val MintSurfaceHighLight = Color(0xFFC9E6D3)

val GreenPassportLightColorScheme = lightColorScheme(
    primary = ForestLight,
    onPrimary = OnForestLight,
    secondary = LimeLight,
    onSecondary = OnLimeLight,
    background = WhiteBackgroundLight,
    surface = WhiteBackgroundLight,
    surfaceVariant = MintCardLight,
    surfaceContainerLowest = WhiteBackgroundLight,
    surfaceContainerLow = MintCardLowLight,
    surfaceContainer = MintCardLight,
    surfaceContainerHigh = MintSurfaceLight,
    surfaceContainerHighest = MintSurfaceHighLight,
)
```

`GpSheetScaffold` — фон шторки `MaterialTheme.colorScheme.surface` вместо `surfaceContainer`, чтобы мятные карточки внутри шторки были видны. `values/colors.xml` `splash_background` → `#FFFFFFFF`.

---

## Шаг 2. Мягкая тень у карточек и кнопок

### Почему

Даже при цветовом контрасте плоские плашки смотрятся «наклеенными». Одна тень на уровне компонента дизайн-системы сразу меняет все экраны: `GpSurfaceCard` лежит в основе `GpListRow`, `GpBackButton`, `ProgressHeroCard`, `HeroImageCard` и карточек магазина.

### Код

`Dimens.kt`:

```kotlin
val CardElevation = 2.dp
val ButtonElevation = 2.dp
val ChipElevation = 1.dp
```

`GpSurfaceCard` получает параметр тени с дефолтом:

```kotlin
@Composable
fun GpSurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    shadowElevation: Dp = Dimens.CardElevation,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick == null) {
        Surface(modifier = modifier, shape = shape, color = color, shadowElevation = shadowElevation) {
            Column(content = content)
        }
    } else {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = color,
            shadowElevation = shadowElevation,
        ) {
            Column(content = content)
        }
    }
}
```

`GpPrimaryButton` — тень в покое, без тени при нажатии:

```kotlin
elevation = ButtonDefaults.buttonElevation(
    defaultElevation = Dimens.ButtonElevation,
    pressedElevation = Dimens.SpacingNone,
),
```

`GpFilterChip` — `elevation = FilterChipDefaults.filterChipElevation(elevation = Dimens.ChipElevation)`.
`IconCircle` — `Modifier.shadow(Dimens.CardElevation, CircleShape)` перед `background`, чтобы цветные кружки быстрых действий не висели плоско на белом.

---

## Шаг 3. Нижняя панель: одинаковые отступы и заметная тень

### Почему

Сейчас снаружи панели 22dp по бокам и 8dp снизу, поэтому она выглядит «уплывшей» от краёв. Одинаковый отступ со всех сторон задаёт один токен. Панель мятная (`surfaceContainer`), как карточки, и получает более сильную тень, чем карточки: она парит над контентом. Зарезервированная под панель высота считается из тех же токенов, чтобы контент не заезжал под панель.

### Код

`Dimens.kt`:

```kotlin
val BottomBarOuterPadding = 12.dp
val BottomBarElevation = 12.dp
val BottomBarReservedHeight = BottomBarHeight + BottomBarOuterPadding + SpacingSmall
```

`GreenPassportAppShell`:

```kotlin
GpBottomBar(
    selected = visibleTab,
    onSelect = { tab -> navController.navigateToTopLevel(tab) },
    modifier = Modifier
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
        .padding(Dimens.BottomBarOuterPadding),
)
```

`GpBottomBar` — `SHADOW_ALPHA = 0.18f`, фон `surfaceContainer`, остальное без изменений.

---

## Шаг 4. Понятные ошибки авторизации

### Почему

`:feature:auth` не зависит от Firebase напрямую, поэтому перевод исключений Firebase в понятную причину делается в `:core` (`FirebaseAuthRepository`). ViewModel получает типизированную причину и показывает свою строку. Отдельно ловится случай, когда в консоли не включён способ входа (`ERROR_OPERATION_NOT_ALLOWED` или `CONFIGURATION_NOT_FOUND`): именно он сейчас выглядит как «ничего не работает».

### Код

`core/auth/AuthFailure.kt`:

```kotlin
enum class AuthFailure {
    INVALID_CREDENTIALS,
    INVALID_EMAIL,
    EMAIL_ALREADY_IN_USE,
    WEAK_PASSWORD,
    NETWORK,
    TOO_MANY_REQUESTS,
    SIGN_IN_METHOD_DISABLED,
    UNKNOWN,
}
```

`core/auth/AuthFailureException.kt`:

```kotlin
class AuthFailureException(
    val failure: AuthFailure,
    cause: Throwable,
) : Exception(cause)
```

`FirebaseAuthRepository` оборачивает каждый вызов:

```kotlin
override suspend fun signInWithEmail(email: String, password: String): AuthSession =
    mapAuthFailures {
        firebaseAuth.signInWithEmailAndPassword(email, password).await().requireUser().toAuthSession()
    }

private suspend fun <T> mapAuthFailures(block: suspend () -> T): T =
    try {
        block()
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        throw AuthFailureException(error.toAuthFailure(), error)
    }

private fun Exception.toAuthFailure(): AuthFailure = when {
    this is FirebaseNetworkException -> AuthFailure.NETWORK
    this is FirebaseTooManyRequestsException -> AuthFailure.TOO_MANY_REQUESTS
    this is FirebaseAuthWeakPasswordException -> AuthFailure.WEAK_PASSWORD
    this is FirebaseAuthUserCollisionException -> AuthFailure.EMAIL_ALREADY_IN_USE
    this is FirebaseAuthInvalidUserException -> AuthFailure.INVALID_CREDENTIALS
    this is FirebaseAuthInvalidCredentialsException && errorCode == ERROR_INVALID_EMAIL -> AuthFailure.INVALID_EMAIL
    this is FirebaseAuthInvalidCredentialsException -> AuthFailure.INVALID_CREDENTIALS
    this is FirebaseAuthException && errorCode == ERROR_OPERATION_NOT_ALLOWED -> AuthFailure.SIGN_IN_METHOD_DISABLED
    message.orEmpty().contains(CONFIGURATION_NOT_FOUND) -> AuthFailure.SIGN_IN_METHOD_DISABLED
    else -> AuthFailure.UNKNOWN
}
```

Константы `ERROR_INVALID_EMAIL`, `ERROR_OPERATION_NOT_ALLOWED`, `CONFIGURATION_NOT_FOUND` — `private const val` в `companion object` репозитория.

---

## Шаг 5. Валидация и состояние экрана входа

### Почему

Пустые или заведомо неверные поля не должны уходить в сеть. Пароль короче 6 символов Firebase всё равно отклонит, и лучше сказать об этом до запроса. После успешного входа пароль нужно стереть из состояния, потому что `AuthViewModel` переживает выход из аккаунта.

### Код

`AuthUiState`:

```kotlin
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isEmailInvalid: Boolean = false,
    val isPasswordTooShort: Boolean = false,
    val failure: AuthFailure? = null,
)
```

`AuthViewModel` (заодно по правилам проекта: без аннотации типа у `uiState`, `TAG` в `companion object`):

```kotlin
fun signIn() {
    submit { email, password -> signInWithEmail(email, password) }
}

fun register() {
    submit { email, password -> registerWithEmail(email, password) }
}

private fun submit(action: suspend (email: String, password: String) -> AuthSession) {
    val state = _uiState.value
    val email = state.email.trim()
    val isEmailInvalid = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordTooShort = state.password.length < MIN_PASSWORD_LENGTH
    if (isEmailInvalid || isPasswordTooShort) {
        _uiState.update { it.copy(isEmailInvalid = isEmailInvalid, isPasswordTooShort = isPasswordTooShort) }
        return
    }
    launchAuthAction { action(email, state.password) }
}

private fun launchAuthAction(action: suspend () -> AuthSession) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, failure = null) }
        runCatching { action() }
            .onSuccess { _uiState.value = AuthUiState() }
            .onFailure { error ->
                Log.e(TAG, "Auth action failed", error)
                val failure = (error as? AuthFailureException)?.failure ?: AuthFailure.UNKNOWN
                _uiState.update { it.copy(isLoading = false, failure = failure) }
            }
    }
}

companion object {
    private const val TAG = "AuthViewModel"
    private const val MIN_PASSWORD_LENGTH = 6
}
```

`isSignedIn` и `onSignedIn` удаляются: экран переключает `MainViewModel` по `AuthRepository.session`, а колбэк в `GreenPassportApp` пустой.

`presentation/state/AuthFailureMessage.kt` — сообщение для каждой причины:

```kotlin
@StringRes
fun authFailureMessageRes(failure: AuthFailure): Int = when (failure) {
    AuthFailure.INVALID_CREDENTIALS -> R.string.wrong_email_or_password
    AuthFailure.INVALID_EMAIL -> R.string.enter_valid_email
    AuthFailure.EMAIL_ALREADY_IN_USE -> R.string.email_already_registered
    AuthFailure.WEAK_PASSWORD -> R.string.password_too_weak
    AuthFailure.NETWORK -> R.string.no_internet_connection
    AuthFailure.TOO_MANY_REQUESTS -> R.string.too_many_attempts_msg
    AuthFailure.SIGN_IN_METHOD_DISABLED -> R.string.sign_in_method_disabled_msg
    AuthFailure.UNKNOWN -> R.string.could_not_sign_in_msg
}
```

Строки — в `values/strings.xml` и `values-en/strings.xml` модуля `:feature:auth`, например:

```xml
<string name="wrong_email_or_password">Неверная почта или пароль</string>
<string name="password_at_least_6_characters">Пароль — минимум 6 символов</string>
<string name="sign_in_method_disabled_msg">Этот способ входа сейчас недоступен. Попробуйте позже</string>
```

Файлы `AuthScreen.kt`, `AuthUiState.kt`, `AuthViewModel.kt` переезжают в `presentation/{ui,state,viewmodels}` по правилу структуры пакетов.

---

## Шаг 6. Экран входа в новом стиле

### Почему

Экран входа — первое, что видит новый пользователь. Сейчас это маскот и голые поля, а ошибка показывается одна на всё. Нужно: заголовок, ошибка под нужным полем, показ пароля, переход «Далее → Готово» с клавиатуры и спиннер внутри кнопки, чтобы остальные кнопки не прыгали во время загрузки.

### Код

`GpTextField` получает `isError`, `supportingText`, `trailingIcon`, `keyboardActions` и пробрасывает их в `TextField`. `GpPrimaryButton` получает `isLoading`:

```kotlin
@Composable
fun GpPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        …
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = Dimens.ProgressStrokeWidth,
                modifier = Modifier.size(Dimens.IconSizeMedium),
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}
```

Поле пароля в `AuthScreen`:

```kotlin
GpTextField(
    value = uiState.password,
    onValueChange = onPasswordChange,
    label = { Text(stringResource(R.string.password)) },
    singleLine = true,
    isError = uiState.isPasswordTooShort,
    supportingText = if (uiState.isPasswordTooShort) {
        { Text(stringResource(R.string.password_at_least_6_characters)) }
    } else {
        null
    },
    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    trailingIcon = {
        IconButton(onClick = onTogglePasswordVisibility) {
            Icon(
                imageVector = if (uiState.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = stringResource(R.string.show_password),
            )
        }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
    keyboardActions = KeyboardActions(onDone = { onSignIn() }),
)
```

Раскладка: маскот 120dp → заголовок `headlineLarge` «Вход» → почта → пароль → общая ошибка (`authFailureMessageRes`) → «Войти» (`GpPrimaryButton` с `isLoading`) → «Создать аккаунт» и «Продолжить без регистрации» как `TextButton` цвета `primary`. Колонка с `verticalScroll` и `imePadding()`, чтобы при открытой клавиатуре кнопка не пряталась.

---

## Шаг 7. Firebase: включить вход и задеплоить правила из репозитория

### Почему

Пока в консоли не включена Authentication, приложение не может войти ни одним способом, а все чтения Firestore получают `PERMISSION_DENIED`: правила требуют `request.auth != null`. Правила в репозитории уже соответствуют коду, поэтому их нужно только опубликовать. Отдельные JSON не нужны.

### Что делает пользователь (я сюда доступа не имею)

1. Firebase Console → проект `chatroom-85fb8` → **Authentication → Get started → Sign-in method**: включить **Email/Password** и **Anonymous**.
2. **Firestore Database** — если базы ещё нет, создать её в режиме production.
3. Опубликовать правила. Вариант A — через CLI из корня репозитория (`firebase.json` и `.firebaserc` уже настроены):

   ```bash
   npm install -g firebase-tools
   firebase login
   firebase deploy --only firestore:rules,firestore:indexes --project chatroom-85fb8
   firebase deploy --only storage --project chatroom-85fb8
   ```

   Вариант B — в консоли: **Firestore → Rules** вставить целиком содержимое `firestore.rules` и нажать Publish; **Storage → Rules** — содержимое `storage.rules`.
4. Storage: приложение сейчас не читает файлы из Storage, поэтому второй деплой можно пропустить. Если консоль требует тариф Blaze для создания бакета, Storage пока не включаем.
5. Засеять данные: `cd scripts && npm install && node seed-firestore.js` (при повторе — `--only=events`).

В `README.md` раздел «Known limitations» заменяется на «Firebase setup» с этими шагами. В `CLAUDE.md` строка «Firebase Authentication isn't enabled…» заменяется ссылкой на этот раздел.

### Замечание по правилам (не меняем в этом плане)

Любой вошедший пользователь может читать чужие `taskProgress`, `purchases`, `favoriteTasks` и т. д. (`allow read: if request.auth != null`). Очки начисляет клиент (`maxSingleAward = 100000`), так что их можно накрутить. Для пилота это допустимо. Сужение чтения до `resource.data.userId == request.auth.uid` ломает проверку `hasAnsweredSurvey`, потому что она читает несуществующий документ. Поэтому это отдельная задача, её предложу после этого плана.

---

## Критичные файлы

- `core/.../designsystem/theme/{Color,Dimens}.kt`
- `core/.../designsystem/component/{GpSurfaceCard,GpPrimaryButton,GpFilterChip,IconCircle,GpBottomBar,GpSheetScaffold,GpTextField}.kt`
- `app/.../navigation/GreenPassportAppShell.kt`, `app/.../GreenPassportApp.kt`, `app/src/main/res/values/colors.xml`
- `core/.../auth/{AuthFailure,AuthFailureException,FirebaseAuthRepository}.kt`
- `feature/auth/.../presentation/{ui/AuthScreen,state/AuthUiState,state/AuthFailureMessage,viewmodels/AuthViewModel}.kt`, `feature/auth/src/main/res/values{,-en}/strings.xml`
- `README.md`, `CLAUDE.md`

Переиспользуем: `GpTextField`, `GpPrimaryButton`, `MascotWidget`, `Dimens`, `AuthRepository`, `MainViewModel` (переключение экранов по сессии уже работает).

## Проверка

```bash
./gradlew build
./gradlew detektAll
./gradlew installDebug
```

Сценарии:
1. Главная, Магазин, Профиль, Задания в светлой теме: фон белый, карточки мятные с тенью, кнопка «назад», чипы и круги быстрых действий отделены от фона.
2. Нижняя панель: одинаковый отступ слева, справа и снизу, заметная тень, последний элемент списка не прячется под панелью.
3. Тёмная тема: карточки `#1E2A24` на `#121815`, тени не дают «грязных» ореолов.
4. Вход до включения Authentication в консоли: появляется «Этот способ входа сейчас недоступен…», а не общая ошибка.
5. После включения: пустая почта → ошибка под полем без запроса в сеть; пароль из 5 символов → «минимум 6 символов»; неверный пароль → «Неверная почта или пароль»; регистрация существующей почты → «Эта почта уже зарегистрирована»; авиарежим → «Нет подключения к интернету».
6. Успешный вход → главная с данными (Firestore больше не отвечает `PERMISSION_DENIED`); выход из профиля → экран входа с пустыми полями.
7. «Продолжить без регистрации» → главная.
