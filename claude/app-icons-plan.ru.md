# Выбор иконки приложения на Android

## Зачем
На iOS в профиле есть блок «Иконка приложения»: шесть вариантов (светлая, тёмная, закат, ночь, океан, лайм) с превью. На Android смены иконки не было (пункт Android backlog). Переносим.

## Как
- **Иконки адаптивные.** Слой маскота `ic_launcher_foreground` общий. Для каждого варианта свой фон: тёмный — цвет `#1F6B47`; закат, ночь, океан и лайм — растровые фоны `mipmap-*/ic_launcher_background_<name>.webp`. Их и превью `drawable-nodpi/app_icon_preview_<name>.webp` генерирует `scripts/app-icons.py` (Pillow).
- **Манифест.** У `MainActivity` убран фильтр LAUNCHER. Вместо него шесть `activity-alias` (`.IconStandard`, `.IconDark`, …) с `targetActivity=".MainActivity"`; включён только `.IconStandard`.
- **Смена иконки.** `PackageManagerAppIconRepository` (core) включает выбранный alias и выключает остальные через `PackageManager.setComponentEnabledSetting(..., DONT_KILL_APP)`. Текущая иконка — включённый alias.
- **Профиль.** Блок `app_icon` — сетка 3 колонки: превью, подпись, у выбранной обводка `Forest`.

```xml
<activity-alias
    android:name=".IconSunset"
    android:enabled="false"
    android:exported="true"
    android:icon="@mipmap/ic_launcher_sunset"
    android:roundIcon="@mipmap/ic_launcher_sunset"
    android:targetActivity=".MainActivity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity-alias>
```

## Ограничения
- Лаунчер обновляет иконку с задержкой; на некоторых оболочках — после выхода из приложения.
- Ярлык на рабочем столе может исчезнуть или переместиться при смене иконки.

## Проверка
`./gradlew assembleDebug detektAll :feature:profile:lintDebug :core:lintDebug :app:lintDebug`
Вручную: Профиль → «Иконка приложения» → «Закат» → на рабочем столе и в списке приложений иконка «Закат», приложение открывается; push о сообщении открывает чат.
