# Выбор языка приложения в профиле

## Контекст

Пользователь хочет менять язык приложения прямо в профиле, а не через настройки телефона. Все строки уже переведены на русский (`values/`, язык по умолчанию) и английский (`values-en/`) во всех 14 модулях.

Решение пользователя — три варианта:
- «Как в системе»;
- «Русский»;
- «English».

Белорусский — отдельной задачей позже.

Как сейчас:
- **Язык выбирает только система.** Приложение показывает строки по языку телефона, и переключить язык внутри приложения нельзя.
- **`MainActivity` — это `ComponentActivity`**, тема наследует `android:Theme.Material.Light.NoActionBar`, AppCompat в проекте нет.

Выбранный подход — per-app language API из AndroidX (`AppCompatDelegate.setApplicationLocales`):
- **Android 13+:** язык выставляется системным API и появляется в «Настройки → Приложения → Язык».
- **Android 8–12:** AppCompat хранит выбор сам (`autoStoreLocales`).
- **Перезапуск Activity** система делает сама, поэтому все экраны сразу перерисовываются на новом языке, включая даты через `LocalConfiguration`.

Первым действием план сохраняется в `claude/app-language-plan.ru.md`. Сборка — у пользователя (в облаке нет Android SDK); я прогоняю detekt CLI. Коммит — в `claude/zealous-keller-iygmdf`.

---

## Шаг 1. AppCompat и тема

### Почему

`setApplicationLocales` на Android 12 и ниже применяет язык только к `AppCompatActivity`. А `AppCompatActivity` падает при старте, если тема не наследует `Theme.AppCompat`. Compose-экранов это не касается: цвета берутся из `GreenPassportTheme`.

### Код

`gradle/libs.versions.toml`:

```toml
appcompat = "1.7.1"
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
```

`implementation(libs.androidx.appcompat)` в `app/` и `core/`.

`values/themes.xml` и `values-v31/themes.xml`:

```xml
<style name="Theme.GreenPassport" parent="Theme.AppCompat.Light.NoActionBar">
    <item name="android:windowBackground">@color/splash_background</item>
</style>
```

В v31 остаются `windowSplashScreenBackground` и `windowSplashScreenAnimatedIcon`.

`MainActivity`:

```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GreenPassportTheme { GreenPassportApp(modifier = Modifier.fillMaxSize()) } }
    }
}
```

---

## Шаг 2. Список языков для системы

### Почему

- **Android 13+** показывает язык приложения в системных настройках только при наличии `localeConfig`.
- **Android 8–12:** без служебного `AppLocalesMetadataHolderService` с `autoStoreLocales` AppCompat не запомнит выбор после перезапуска.

### Код

`app/src/main/res/xml/locales_config.xml`:

```xml
<locale-config xmlns:android="http://schemas.android.com/apk/res/android">
    <locale android:name="ru" />
    <locale android:name="en" />
</locale-config>
```

`AndroidManifest.xml`:

```xml
<application android:localeConfig="@xml/locales_config" …>
    <service
        android:name="androidx.appcompat.app.AppLocalesMetadataHolderService"
        android:enabled="false"
        android:exported="false">
        <meta-data android:name="autoStoreLocales" android:value="true" />
    </service>
</application>
```

---

## Шаг 3. Язык как настройка в `:core`

### Почему

`:feature:profile` не должен знать про AppCompat: он работает через интерфейс в `:core`, как с остальными настройками. Язык хранится не в DataStore, а в системе/AppCompat — это единственный источник правды, и тогда выбор в системных настройках Android 13 тоже отражается в профиле.

### Код

`core/model/settings/AppLanguage.kt`:

```kotlin
enum class AppLanguage(val languageTag: String?) {
    SYSTEM(null),
    RUSSIAN("ru"),
    ENGLISH("en"),
}
```

`core/model/settings/AppLanguageRepository.kt`:

```kotlin
interface AppLanguageRepository {
    fun getLanguage(): AppLanguage
    fun setLanguage(language: AppLanguage)
}
```

`core/datastore/AppCompatLanguageRepository.kt`:

```kotlin
class AppCompatLanguageRepository @Inject constructor() : AppLanguageRepository {

    override fun getLanguage(): AppLanguage {
        val tag = AppCompatDelegate.getApplicationLocales()[0]?.language
        return AppLanguage.entries.firstOrNull { it.languageTag != null && it.languageTag == tag } ?: AppLanguage.SYSTEM
    }

    override fun setLanguage(language: AppLanguage) {
        val locales = language.languageTag
            ?.let(LocaleListCompat::forLanguageTags)
            ?: LocaleListCompat.getEmptyLocaleList()
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
```

Привязка — в `DatastoreModule` (`@Binds`).

---

## Шаг 4. Пункт «Язык» в профиле

### Почему

Выбор языка — настройка, как уведомления, поэтому пункт стоит рядом с переключателем уведомлений. Справа показано текущее значение, по нажатию открывается меню из трёх вариантов. Названия языков пишутся на самих языках («Русский», «English»): так их найдёт человек, который случайно переключил язык и не понимает интерфейс.

### Код

Use case в `feature/profile/domain`:

```kotlin
class GetAppLanguageUseCase @Inject constructor(private val repository: AppLanguageRepository) {
    operator fun invoke(): AppLanguage = repository.getLanguage()
}

class SetAppLanguageUseCase @Inject constructor(private val repository: AppLanguageRepository) {
    operator fun invoke(language: AppLanguage) = repository.setLanguage(language)
}
```

`ProfileUiState` получает `language: AppLanguage`. `ProfileViewModel`:

```kotlin
fun onLanguageSelected(language: AppLanguage) {
    setAppLanguage(language)
    _uiState.update { it.copy(language = language) }
}
```

В `ProfileScreen` новая строка:

```kotlin
GpListRow(
    title = stringResource(R.string.language),
    leading = { ProfileMenuIcon(icon = Icons.Filled.Translate, color = sectionColors.calendar) },
    trailing = { LanguageMenu(selected = uiState.language, onSelected = onLanguageSelected) },
    onClick = { isLanguageMenuOpen = true },
)
```

`LanguageMenu` — это текущее значение и `DropdownMenu` с тремя пунктами. Подписи:

```kotlin
@StringRes
fun appLanguageLabelRes(language: AppLanguage): Int = when (language) {
    AppLanguage.SYSTEM -> R.string.system_language
    AppLanguage.RUSSIAN -> R.string.russian_language_name
    AppLanguage.ENGLISH -> R.string.english_language_name
}
```

Строки:
- `language` — «Язык» / «Language»;
- `system_language` — «Как в системе» / «System default».

`russian_language_name` = «Русский» и `english_language_name` = «English» одинаковы в обоих `values`: каждый язык подписан на самом себе.

---

## Критичные файлы

- `gradle/libs.versions.toml`, `app/build.gradle.kts`, `core/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`, `app/src/main/res/values{,-v31}/themes.xml`, новый `app/src/main/res/xml/locales_config.xml`, `app/.../MainActivity.kt`
- новые `core/.../model/settings/{AppLanguage,AppLanguageRepository}.kt`, `core/.../datastore/AppCompatLanguageRepository.kt`, `core/.../di/DatastoreModule.kt`
- `feature/profile/.../domain/{Get,Set}AppLanguageUseCase.kt`, `presentation/profile/{ProfileUiState,ProfileViewModel,ProfileScreen}.kt`, новый `presentation/profile/AppLanguageLabel.kt`, `feature/profile/src/main/res/values{,-en}/strings.xml`

Переиспользуем: `GpListRow`, `ProfileMenuIcon`/`IconCircle`, `SectionColors`, `DatastoreModule`.

## Проверка

```bash
./gradlew build
./gradlew detektAll
./gradlew installDebug
```

Сценарии:
1. **Смена языка.** Телефон на русском. Профиль → «Язык» → English: приложение сразу перерисовывается на английском (главная, вкладки, даты), в профиле выбрано «English».
2. **Сохранение выбора.** Закрыть приложение из недавних и открыть снова — язык остался английским. На Android 8–12 тоже.
3. **Системный язык.** «System default» / «Как в системе» → язык снова как у телефона.
4. **Системные настройки (Android 13+).** Настройки → Приложения → Зелёный Паспорт → Язык: в списке русский и английский. Выбор там отражается в профиле.
5. **Регрессия.** Старт приложения, сплэш, вход через Google и выбор фото в задании работают после перехода на `AppCompatActivity`, тема не падает.
