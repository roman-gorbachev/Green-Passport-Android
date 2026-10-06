# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Green Passport: an Android app (Kotlin, Jetpack Compose, Material 3) that rewards eco-friendly actions with points and XP. The backend is Firebase (Auth, Firestore, Storage, Cloud Messaging). DI uses Hilt, and annotation processing uses **KSP only (never kapt)**. Room handles local-only data such as mini-game progress and the notification log.

## Commands

```bash
./gradlew assembleDebug                 # debug build
./gradlew build                         # assemble + Android lint + unit tests (what CI runs)
./gradlew installDebug                  # install on device/emulator
./gradlew test                          # unit tests, all modules
./gradlew :feature:tasks:testDebugUnitTest --tests "*.SomeTest"   # single module / single test
./gradlew connectedAndroidTest          # instrumented tests (needs a device)
./gradlew detektAll                     # detekt over the whole repo (config/detekt.yml, baseline config/baseline.xml)
./gradlew detektAll -PdetektAutoFix     # detekt with auto-correct (ktlint formatting rules)
./gradlew detektGenerateBaseline        # regenerate the detekt baseline
```

- CI (`.github/workflows/ci.yml`) runs `./gradlew build` on JDK 21. Detekt is **not** part of `build`, so run `detektAll` separately. Detekt `maxIssues: 0`: any new finding outside the baseline fails the task.
- Release signing reads `keystore.properties` (see `keystore.properties.example`). Without it, release builds are unsigned. Release uses R8 with `app/proguard-rules.pro`.
- No unit tests exist yet.

## Architecture

Module graph: `:app` → `:core` + every `:feature:<name>`; each `:feature:<name>` → `:core` only. Features never depend on each other, and `:core` never depends on a feature. Modules are registered in `settings.gradle.kts` (the feature list loop). A new feature module also has to be added to `app/build.gradle.kts` dependencies.

**`:core`** (`com.smartcity.greenpassport.core`) holds everything shared:
- `model/`: domain models **and repository interfaces** (e.g. `TasksRepository`, `PointsRepository`).
- Content text from Firestore is `LocalizedText` / `LocalizedTextList` (Russian field as `fallback` plus the `titles`/`descriptions`/… maps), read with `DocumentSnapshot.localizedText(field, translationsField)`. Screens resolve it at display time with `.localized()` (`designsystem/text`), never in a mapper or ViewModel, so a language switch updates already-loaded data. City names are Russian keys in data; show them with `cityName(city)`.
- `datasource/remote/repository/`: Firestore implementations (`Firestore*Repository`). They map `DocumentSnapshot`s by hand using private `FIELD_*` constants, not by reflection or `toObject`. Field names must match `scripts/seed-firestore.js`.
- `datasource/remote/FirestoreCollections.kt`: the single source of collection paths. Every collection lives under `apps/greenpassport/…`, not at the Firestore root.
- `datasource/local/`: the Room DB (`GreenPassportDatabase`), DAOs, entities and `Room*Repository`.
- `di/`: Hilt modules. Bind each new repository implementation to its interface here (`RemoteDataSourceModule`, `FirestoreModule`, `LocalModule`, etc.).
- `auth/`: `AuthRepository` / `AuthSession` (Firebase Auth wrapper). `messaging/`: FCM, notification workers (WorkManager). Notifications have three categories (`NotificationCategory`): event reminders follow `EVENTS`, rewards, streak and coupon reminders follow `TASKS` (DataStore `notifications_events_enabled` / `notifications_tasks_enabled`, defaulting to the legacy `notifications_enabled`), chat pushes follow `MESSAGES` (`users/{uid}.messageNotificationsEnabled`, read by the server). `GreenPassportApplication` creates the rewards and messages channels. `MainViewModel` writes the FCM token to the owner-only `userDevices/{token}` on every sign-in, sign-out deletes it; `ChatMessagingService` shows data-only chat pushes on the messages channel unless that chat is open (`OpenChatTracker`), and a tap opens the chat (`ChatNotifier.EXTRA_CHAT_ID` → `MainActivity`). Event and coupon workers log to the notification log when they fire; `StreakReminderWorker` (unique work `streak_reminder`, 20:00 Europe/Minsk) does not, because `UpdateStreakReminderUseCase` in `:feature:home` re-plans or cancels it on every streak update. `datastore/`: DataStore preferences.
- App icon: the launcher entries are `activity-alias`es in `app/src/main/AndroidManifest.xml` (`.IconStandard` enabled, `.IconDark`, `.IconSunset`, `.IconNight`, `.IconOcean`, `.IconLime` disabled), all targeting `MainActivity`, which has no launcher filter of its own. `PackageManagerAppIconRepository` (core) enables the chosen alias and disables the rest (`DONT_KILL_APP`); the profile shows `AppIconSection`. The enabled/disabled state of the aliases survives reinstalls, so after picking another icon Android Studio's default launch (`.IconStandard`) fails with "Activity class … does not exist": set the run configuration to *Launch: Specified Activity* `com.smartcity.greenpassport.MainActivity` (it is exported for this), or reset with `adb shell pm enable com.smartcity.greenpassport/.IconStandard` (uninstalling also resets it). The scene backgrounds, the transparent `ic_launcher_foreground_mascot` layer and the previews in `:feature:profile` `drawable-nodpi` are generated by `scripts/app-icons.py`.
- `navigation/Destination.kt`: **all** type-safe routes (`@Serializable` objects and data classes in one sealed interface). `NotificationDeepLink` maps pushes to destinations.
- `designsystem/`: theme and shared Compose components. `common/`: the `Result`/`resultOf`, `UiState` and `DispatcherProvider` helpers.

**Feature modules** (`com.smartcity.greenpassport.feature.<name>`) are split into:
- `domain/`: single-purpose `@Inject` use cases with `operator fun invoke`, which call `:core` repository interfaces. Use cases orchestrate cross-repository work; for example, `CompleteTaskUseCase` marks progress and then awards points through `PointsRepository`.
- `presentation/ui` (screens), `presentation/viewmodels` (`@HiltViewModel` exposing `StateFlow<…UiState>`), `presentation/state` (UI state data classes).
- Feature screens take navigation callbacks (e.g. `onTaskSelected`). They don't touch `NavController`.

**`:app`** owns wiring:
- `GreenPassportApp` switches on `AppStartupState` (onboarding → auth → profile setup → main). `MainViewModel` shows the `ProfileSetupScreen` wizard (name, city, interests, avatar) to any non-anonymous user whose `users/{uid}` document has no `profileCompletedAt`; the same wizard is reused for `Destination.EditProfile`. The main state renders `navigation/GreenPassportAppShell`: `AppNavHost` plus a floating `GpBottomBar` (inside `GlassBottomBarLayout`, which marks the app content as a Haze source so the capsule blurs what scrolls under it; icon + label per tab, the selected one Forest on a translucent Forest pill), shown only on the four `TopLevelDestination` tabs (Home, Shop, Map, Favorites). The app is edge-to-edge, so screens handle insets themselves.
- `navigation/AppNavHost.kt` registers every `Destination`. Nested screens are wrapped in `FeatureScaffold` (core `GlassHeaderScaffold`): a pinned `ScreenHeader` (round back button + centered title) drawn over the content; once the content scrolls under it, the header blurs what is behind it with Haze (`hazeSource` / `hazeBlur`) and shows a hairline divider. Screens take `contentPadding: PaddingValues` and must apply it to their scroll container (`LazyColumn(contentPadding = contentPadding + …)` or `verticalScroll().padding(contentPadding)`), never as an outer `Modifier.padding`, otherwise nothing scrolls under the glass. Pass `onNavigateBack = null` for a tab, which also reserves space for the bottom bar. Details that open from several features (`TaskDetail`, `EventDetail`) are `dialog<>` destinations rendered with `GpSheetScaffold`, a full-width bottom sheet with a rounded top over a dimmed scrim (no blur). Features can't depend on each other, so a sheet can't be embedded in the caller's screen.
- Adding a screen means: add a `Destination` in `:core`, then an entry in `AppNavHost`. A home shortcut is a `HomeQuickAction` in `:feature:home`.
- UI is built from `core/designsystem/component` (`Gp*` components, `ProgressHeroCard`, `QuickActionButton`, `SymbolTile` + `SymbolTileStyle`, `ListSection` / `ListSectionRow` / `listSectionItems` for grouped rows like iOS `insetGrouped`, `SegmentedControl`, `ChoiceCapsule`, `MessageComposer`, `PointsChip`, `HeroImageCard`, `ArticleText` for the eco tips' Markdown subset (`## `, `- `, `**bold**`, links; parsed by `core/common/ArticleBlock`), …) and theme tokens. The look mirrors iOS: grey `background` (`ScreenBackground`), white `surface`/`surfaceContainer` cards (`CardBackground`), `surfaceContainerHighest` = `MintSurfaceHigh` for tiles, `onSurfaceVariant` for secondary text, `outlineVariant` for separators. `GreenPassportTheme.sectionColors` is only for the sorting bins, avatars and profile row tiles (`SymbolTileStyle.Tinted`). Don't use raw Material `Card`/`TopAppBar` in feature screens. A button that runs an action wraps its label in `LoadingLabel`: the label stays in the layout transparent and a small spinner sits on top, so the button never changes size (`GpPrimaryButton` does this via `isLoading`). The calendar is a custom month grid (`MonthCalendar` in `:feature:calendar`, `java.time`) with an event-count capsule under each day; the games hub is a two-column grid of `GameTile`s (PNG from `iconPath` over the `iconColors` gradient with `iconEmoji` as the fallback, spring press).

## Backend

- `functions/` (TypeScript, Firebase Functions v2; callables in `europe-central2`, Firestore triggers in `us-central1` next to the `nam5` database) owns everything that changes points: `completeSelfTask`, `redeemTaskCode`, `recordTipRead`, `recordGameResult`, `redeemReward`, `reviewSubmission` (moderators only), plus triggers that hide reported or obscene posts and screen task photos with Cloud Vision. `community/triggers.ts`: `notifyGroupMessage` / `notifyForumPost` push new messages to members (skipping the sender, muted chats in `chatSettings/{uid}_{chatId}` and users with `messageNotificationsEnabled == false`; the forum only to users who unmuted it) and stamp `groups.lastMessageAtEpochMillis`; `onForumPostUpdated` re-screens edited posts; both update triggers empty `replyTo.text` in replies to a deleted message. Messages carry `replyTo`, `forwardedFrom`, `editedAtEpochMillis` and `deleted`; the rules let the author change only `text` + `editedAtEpochMillis` or delete. The app calls them through `RewardsRepository` / `ModerationRepository`; never write points from the client.
- Tasks carry `verification: SELF | PHOTO | QR`. QR secrets live in `taskSecrets` / `eventSecrets` (unreadable by clients: `code`, `version`, `scanCount`, `dynamicKey`). The web admin panel (`greenpassport-admin` repo) creates, rotates and prints them through `admin*` callables (`functions/src/admin/`); the seed only creates missing ones. A QR task may have a window (`qrActiveFrom/UntilEpochMillis`) and a scan limit (`qrScanLimit`), rejected as `qr_not_active` / `qr_limit_reached`. An event with `qrMode: DYNAMIC` accepts only `greenpassport:event:<id>:d<window>.<hmac>` from the admin's live screen (30 s windows, `dynamicQr.ts`).
- Staff are documents in `apps/greenpassport/admins/{uid}` with `role: SUPER_ADMIN | EDITOR | MODERATOR` (no role = moderator; the apps only check that the document exists). Editors write content collections directly under the rules (stamped with `updatedBy` / `updatedAtEpochMillis`); `audit*` triggers log every content change to `auditLog`. Partner cashiers are `partnerUsers/{uid}.partnerId`. Plan: `claude/admin-backend-plan.ru.md`.
- Shop items may belong to a partner (`partnerId`), be limited (`stockLimit`) and hand out partner codes from the closed `shopItems/{id}/codePool` (`codeSource: POOL`); the server keeps `issuedCount`, `usedCount`, `poolAvailableCount`, `selfMarkedCount`. A sold-out item fails `redeemReward` with `reward_sold_out`.
- Coupon QR codes are links `https://chatroom-85fb8.web.app/coupon?id=<couponId>&code=<code>`. The Hosting rewrite `/coupon` calls `scanCoupon`, which now only redirects to the partner cabinet (`greenpassport-admin.web.app/redeem`); the cashier redeems there via `partnerRedeemCoupon`, and only for their own partner. The apps observe the purchase document to show the new status. `expireCoupons` marks expired coupons daily.
- Tests: `cd functions && npm test` (unit tests: word filter, coupon state, QR helpers, audit diff); with the emulators: `firebase emulators:exec --only firestore,storage "npm --prefix rules-tests test"` (security rules) and `firebase emulators:exec --only auth,firestore,functions,storage "npm --prefix functions run test:integration"` (callables).

## Web games

Mini-games are HTML5 pages in `games/` (plain HTML/CSS/JS, no build step), served by Firebase Hosting (`firebase deploy --only hosting`) and opened in a WebView by both apps.
- Catalog: `apps/greenpassport/games/{gameId}` (`titles {ru,be,en}`, `path`, `sfSymbol`, `materialIcon`, `iconPath` (`<id>/icon.png`, a 512×512 PNG next to the game) for the hub tile with `iconEmoji` and `iconColors` (two hex colours) as the fallback while it loads, `maxPoints`, `order`, `isActive`), seeded by `scripts/seed-firestore.js`. A new game is a new folder plus a catalog document, no app release needed.
- `games/common/game.js` is the shared shell: `?lang=ru|be|en`, `?theme=light|dark`, `GP.t()`, `GP.showResult()`, and the bridge `GP.finish(score)` → iOS `webkit.messageHandlers.greenPassport`, Android `window.GreenPassportAndroid.finish(score)` / `close()` (`GameBridge` JavascriptInterface in `:feature:games`). The app lists the catalog (`FirestoreGamesRepository`) in `GamesHubScreen` and opens `Destination.GameWeb(gameId)`: a full-screen `WebView` (no bottom bar, the back gesture is swallowed, only the close button leaves) loading `GAMES_BASE_URL` (module `BuildConfig`) + `path` + `?lang=&theme=`. `finish` saves the best score in Room and calls `recordGameResult`; the reward or `no_internet_for_points_msg` shows in a top banner. There are no native games any more. `games/common/theme.css` mirrors the shared color tokens.
- `recordGameResult` accepts active catalog games (plus the four legacy ids) and caps points at the game's `maxPoints`.
- Ten games: `eco_runner`, `sort_conveyor`, `ocean_cleanup`, `forest_guard`, `eco_merge`, `quiz_rush`, `light_switch`, `bee_garden`, `bike_lane`, `eco_memory`. Each folder is `index.html` (shared shell: HUD, `<canvas class="stage">`, hint) plus `main.js`; `quiz_rush` is DOM cards with its own shell and a 40-question bank in `questions.js`. `games/common/engine.js` (global `Engine`) holds the canvas plumbing: `createStage` (devicePixelRatio, resize, shake), `loop(update, render)`, `input` (down / move / tap / swipe), cached emoji glyphs, `createParticles`, easing, WebAudio `sound` effects, `startScreen` and `scoreLabel`. One in-game point is one reward point; the server caps it at `maxPoints`. The old eight DOM games are retired in the catalog (`isActive: false`). Hub icons are drawn by `scripts/game-icons.py` (Pillow: flat scenes on the game's gradient, the mascot from `games/common/mascot.png`; no emoji artwork, which can't be redistributed) — re-run it after changing a scene or adding a game.
- Lives: every game except `eco_merge` and `eco_memory` (timed levels) gives the player 3 lives through `GP.lives(container, onGameOver)`; a mistake costs one, 0 lives ends the round. `body` has `touch-action: none`, so drags never scroll or zoom the WebView.

## Conventions

- User profile (`core/model/profile`: `UserProfile`, `UserProfileRepository`) lives in the same `apps/greenpassport/users/{uid}` document as points; write it with `SetOptions.merge()` so points are never overwritten. Personalize screens from the profile, not from `AuthSession.displayName`.
- Every piece of user-generated text (forum posts, chat messages, group names, feedback, profile names) goes through `core/moderation/TextModerator` in the use case before it is written, and the UI shows `ContentRejectedException` as a field error. `firestore.rules` repeats a shorter check (`hasBannedWords`) so the filter can't be bypassed; keep both word lists (`core/src/main/res/raw/banned_roots.txt`, rules regex) in sync.

- Content archived in the web admin panel has `isActive: false` (missing means active). Models carry `isActive`; repositories return everything because coupons, history, moderation and detail screens resolve documents by id; only catalog use cases (`ObserveTasksUseCase`, `ObserveActiveEventsUseCase`, `ObserveRewardCatalogUseCase`, …) hide archived items. Plan: `claude/admin-apps-plan.ru.md`.
- Screens have to survive Firestore failures (e.g. `PERMISSION_DENIED` while Auth isn't enabled). ViewModels wrap repository calls in `runCatching`/`resultOf` and surface an error state rather than crashing.
- Firestore security rules and indexes are in `firestore.rules`, `firestore.indexes.json` and `storage.rules` (project config in `firebase.json` / `.firebaserc`).
- **`storage.rules` is shared with OurMemory** (`~/Personal/OurMemory-80`, `~/Personal/ourmemory-admin`): the Storage bucket of `chatroom-85fb8` has one rules file, and `firebase deploy --only storage` from this repo replaces the rules of both apps. Never delete or rewrite the `// ===== OurMemory =====` section, keep Green Passport helpers prefixed (`isGreenPassport*`) so names don't clash, and copy every change to `~/Personal/OurMemory-80/firebase/storage.rules` (the files must stay identical).

## Known limitations

- Sign-in and Firestore reads only work once Authentication (Email/Password + Anonymous) is enabled and the rules from `firestore.rules` are deployed; see "Firebase setup" in `README.md`. `FirebaseAuthRepository` maps Firebase errors to `AuthFailure` (a disabled sign-in method surfaces as `SIGN_IN_METHOD_DISABLED`).
- The Map tab uses Yandex MapKit (`:feature:map`, `MapKitInitializer`). The key is `YANDEX_MAPKIT_API_KEY` in `local.properties` (or an env var), exposed via the module's `BuildConfig`. Without it the tab falls back to a list. On the first map open the app asks for location (`ACCESS_COARSE/FINE_LOCATION`, `FusedLocationRepository`, 5 s timeout); `ResolveMapFocusUseCase` puts the camera on the user, otherwise on the profile city (`SupportedCities.centers`), otherwise on Minsk. The camera is set once; placemarks are rebuilt only when the visible points change. "Route" opens a `geo:` intent in the system maps app.
- Demo Firestore data is seeded with `scripts/seed-firestore.js` (see `scripts/README.md`; needs `scripts/service-account.json`, which is gitignored). The content lives in `scripts/content/` (one module per collection, every text as a `{ru, be, en}` map). Re-running the seed updates documents in place: an existing document is matched by its Russian title (map points by name and address), new ones get the fixed `id` from the module, and QR secrets are only created when missing, so printed codes keep working. Covers for eco tips are `games/covers/<id>.jpg` on Hosting.

## UX parity with iOS

The Android app and the iOS app (`~/Personal/Green-Passport-iOS`, a SwiftUI port on the same Firebase backend) share one UX: the same tabs, entry points, step order, texts, colors, rules and loading/empty/error states. Controls stay native to each platform (Material 3 here, HIG on iOS). Parity is about flows, not pixels.

`claude/ux-spec.ru.md` is the platform-neutral source of truth, and the same file lives in the iOS repo's `claude/`. **Any UX change goes into the spec first, then into code on both platforms.** A behaviour difference that is not recorded in the spec is a bug. The spec's "Android backlog" section lists iOS-first changes that Android still has to adopt. String keys and color token names (`Forest`, `Lime`, `SectionCommunity`, …) are shared with iOS, so never rename one without updating the other platform.

## Team Conventions

### Files

Save all Claude-generated documents (plans, review summaries, task lists) in the `claude/` folder at the repo root (create it if missing). Plans must be written in Russian and saved as `claude/<topic>-plan.ru.md`. Write the plan file into `claude/` as the **first** action after a plan is approved, before any code is touched. A plan that only exists in the chat is not delivered.

### Plan content

Every plan must be self-explanatory. For each meaningful step, state **why** the change is needed (what is missing or breaks without it) and show a **code example** (Kotlin, XML, TOML) of the resulting code, not a prose description of it. A step that only names the files to touch is not an acceptable plan step.

Bad:

```markdown
- Добавить экран деталей события, который открывается из календаря.
```

Good:

````markdown
### Почему

Сейчас `Destination.Calendar` — единственный маршрут календаря, и открыть конкретное событие нельзя:
маршруту деталей нужен `eventId`, чтобы `EventDetailViewModel` загрузил событие через `SavedStateHandle`.

### Код

```kotlin
@Serializable
data class EventDetail(val eventId: String) : Destination
```
````

Code examples in plans follow the same rules as production code (one type per file, no comments, named constants, string resources). A plan must also end with a **verification** section listing the build/lint commands and the manual scenarios that prove the feature works end to end.

### Mindset

Do not be a yes-man. If a proposed approach has problems, say so and explain the trade-off before implementing. State your position first, then implement what the user decides after the discussion.

### Commit messages

A commit message is a one-line summary of the feature (or features) the commit delivers, then a blank line, then a bullet list with one short sentence per feature. Keep the summary at the level of what the commit does as a whole, not a file-by-file changelog.

```text
Add event reminders and a recycling point list

- Schedule a push reminder one hour before a registered event
- Show recycling points as a list grouped by waste type
```

Never mention Claude, Claude Code, or any AI assistant in a commit: no `Co-Authored-By: Claude ...` trailer, no `Generated with Claude Code` line, no emoji marker. The same applies to pull request descriptions.

### Package structure

Never flat-pack every file of a feature into one feature-named folder. Split packages into sub-packages by the role each file plays.

In a feature module, `presentation/` uses:

- `ui/`: composables (`*Screen.kt`, reusable components);
- `viewmodels/`: `@HiltViewModel` classes;
- `state/`: UI state (`*UiState.kt`), UI models with their mappers, UI-only enums/constants;
- any further sub-package the feature genuinely needs (`components/`, `mappers/`, …) when a role grows large enough to stand on its own.

A feature with several sub-screens (e.g. `profile/`, `games/`) nests this split per sub-screen (`presentation/<screen>/ui|viewmodels|state`).

```text
feature/tasks/.../presentation/
├── state/
│   ├── TaskDetailUiState.kt
│   └── TasksListUiState.kt
├── ui/
│   ├── TaskDetailScreen.kt
│   └── TasksListScreen.kt
└── viewmodels/
    ├── TaskDetailViewModel.kt
    └── TasksListViewModel.kt
```

The same principle applies to `domain/` and `:core`'s data layer: group files by meaning, not in one pile (`datasource/local/{dao,entities,repository}`, `datasource/remote/repository`, …). When adding a file to a package that is still flat, create the missing sub-package for it rather than extending the flat layout.

### Kotlin code rules

- **One type per file**: every `class`, `data class`, `sealed class`, `enum class`, `interface`, and `object` lives in its own file named after the type. The only exceptions are small private helper types used exclusively by one other type in the same file.
- **No comments**: no `//`, `/* */`, or KDoc (`/** */`) in Kotlin source. Self-documenting names are the only acceptable form of documentation.
- **Naming**: ordinary variables, properties and functions are plain `camelCase`. The leading underscore is reserved for the backing private mutable half of a mutable/immutable pair (`StateFlow`, `MutableList` exposed as `List`, …): the private mutable property is `_camelCase` and the public immutable one carries the same name without the underscore. Never write `_name` for a private property that has no public immutable counterpart.
- **Inferred types**: never write a type annotation the compiler can infer from the initializer. Declare `val uiState = _uiState.asStateFlow()`, not `val uiState: StateFlow<TasksListUiState> = _uiState.asStateFlow()`. Keep the annotation only where it changes the inferred type (the initializer would infer a mutable or narrower type than the one to expose, e.g. `val items: List<Task> = mutableListOf()`, or an explicitly nullable/upcast property).
- **Named constants**: all numeric limits (timeouts, thresholds, counts, weights, etc.) must be `private const val` inside the owning type's `companion object`. Never write a raw number inline where the value carries meaning.
- **No redundant `return@label`**: never label-return the last expression of a lambda. `return@label` is reserved for an early exit from the middle of a lambda (`?: return@mapNotNull null`, a guard inside an `if`).
- **No `init {}` blocks**: never kick work off from an `init {}` block in a ViewModel (or anywhere else). It starts loading before anything collects, keeps running when the UI is gone, and re-runs nothing after process death. Build the state in a private `observeXxxUiState()` function and turn it into a `StateFlow` with `stateIn(viewModelScope, SharingStarted.WhileSubscribed(...), <initial state>)`. Model one-shot loads as flows (`flow { }`, `onStart { }`, `asFlow()`) inside that chain. `viewModelScope.launch` is only for user-triggered actions in event handler functions.

  ```kotlin
  @HiltViewModel
  class TasksListViewModel @Inject constructor(
      private val getTasks: GetTasksUseCase,
      observeSession: ObserveTasksSessionUseCase,
  ) : ViewModel() {

      val uiState = observeTasksListUiState(observeSession()).stateIn(
          viewModelScope,
          SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
          TasksListUiState(),
      )

      private fun observeTasksListUiState(sessions: Flow<AuthSession?>): Flow<TasksListUiState> {
          return sessions.mapLatest {
              TasksListUiState(tasks = getTasks())
          }
      }

      companion object {
          private const val STOP_TIMEOUT_MILLIS = 2000L
      }
  }
  ```
- **Localization**: never hardcode user-facing strings. Every string shown in the UI or sent as a notification goes through string resources (`context.getString(R.string.key)` / `stringResource(R.string.key)`). Add each one to `values/strings.xml` (Russian, default), `values-be/strings.xml` (Belarusian) and `values-en/strings.xml` in the module that owns the screen. The in-app language picker lives in the profile (`AppLanguage`, applied with `AppCompatDelegate.setApplicationLocales`); a new language also needs an entry in `app/src/main/res/xml/locales_config.xml`.
- **String resource naming**: the key mirrors the string's own (English) content, lowercased and snake_cased. Never use a category/role prefix like `task_error_load` or a `_title`/`_label` suffix. When the content is too long to spell out in full, take the first few meaningful words and append `_msg`.

  ```xml
  <string name="complete_task">Complete task</string>
  <string name="task_already_completed">Task already completed</string>
  <string name="sign_in_to_earn_points_msg">Sign in to earn points for completed tasks and events</string>
  ```

  Two strings whose content differs only by context still get distinct keys built from their own words (`enter_email` vs `enter_new_email`), not from where they are used.
