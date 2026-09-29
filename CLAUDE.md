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
- `datasource/remote/repository/`: Firestore implementations (`Firestore*Repository`). They map `DocumentSnapshot`s by hand using private `FIELD_*` constants, not by reflection or `toObject`. Field names must match `scripts/seed-firestore.js`.
- `datasource/remote/FirestoreCollections.kt`: the single source of collection paths. Every collection lives under `apps/greenpassport/…`, not at the Firestore root.
- `datasource/local/`: the Room DB (`GreenPassportDatabase`), DAOs, entities and `Room*Repository`.
- `di/`: Hilt modules. Bind each new repository implementation to its interface here (`RemoteDataSourceModule`, `FirestoreModule`, `LocalModule`, etc.).
- `auth/`: `AuthRepository` / `AuthSession` (Firebase Auth wrapper). `messaging/`: FCM, notification workers (WorkManager). `datastore/`: DataStore preferences.
- `navigation/Destination.kt`: **all** type-safe routes (`@Serializable` objects and data classes in one sealed interface). `NotificationDeepLink` maps pushes to destinations.
- `designsystem/`: theme and shared Compose components. `common/`: the `Result`/`resultOf`, `UiState` and `DispatcherProvider` helpers.

**Feature modules** (`com.smartcity.greenpassport.feature.<name>`) are split into:
- `domain/`: single-purpose `@Inject` use cases with `operator fun invoke`, which call `:core` repository interfaces. Use cases orchestrate cross-repository work; for example, `CompleteTaskUseCase` marks progress and then awards points through `PointsRepository`.
- `presentation/ui` (screens), `presentation/viewmodels` (`@HiltViewModel` exposing `StateFlow<…UiState>`), `presentation/state` (UI state data classes).
- Feature screens take navigation callbacks (e.g. `onTaskSelected`). They don't touch `NavController`.

**`:app`** owns wiring:
- `GreenPassportApp` switches on `AppStartupState` (onboarding → auth → main). The main state renders `navigation/GreenPassportAppShell`: `AppNavHost` plus a floating `GpBottomBar`, shown only on the four `TopLevelDestination` tabs (Home, Shop, Map, Favorites). The app is edge-to-edge, so screens handle insets themselves.
- `navigation/AppNavHost.kt` registers every `Destination`. Nested screens are wrapped in `FeatureScaffold` (`GpTopBar` + back; pass `onNavigateBack = null` for a tab, which also reserves space for the bottom bar). Details that open from several features (`TaskDetail`, `EventDetail`) are `dialog<>` destinations rendered with `GpSheetScaffold`, a bottom sheet with a blurred background on API 31+. Features can't depend on each other, so a sheet can't be embedded in the caller's screen.
- Adding a screen means: add a `Destination` in `:core`, then an entry in `AppNavHost`. A home shortcut is a `HomeQuickAction` in `:feature:home`.
- UI is built from `core/designsystem/component` (`Gp*` components, `LevelProgressCard`, `HeroImageCard`, `QuickActionTile`, …) and theme tokens. Don't use raw Material `Card`/`Button`/`TopAppBar` in feature screens.

## Conventions

- Screens have to survive Firestore failures (e.g. `PERMISSION_DENIED` while Auth isn't enabled). ViewModels wrap repository calls in `runCatching`/`resultOf` and surface an error state rather than crashing.
- Firestore security rules and indexes are in `firestore.rules`, `firestore.indexes.json` and `storage.rules` (project config in `firebase.json` / `.firebaserc`).

## Known limitations

- Firebase Authentication isn't enabled in the console yet, so sign-in doesn't work until it's turned on.
- The Map tab uses Yandex MapKit (`:feature:map`, `MapKitInitializer`). The key is `YANDEX_MAPKIT_API_KEY` in `local.properties` (or an env var), exposed via the module's `BuildConfig`. Without it the tab falls back to a list. No route building.
- Demo Firestore data is seeded with `scripts/seed-firestore.js` (see `scripts/README.md`; needs `scripts/service-account.json`, which is gitignored). Re-running it creates duplicate documents.

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
- **Localization**: never hardcode user-facing strings. Every string shown in the UI or sent as a notification goes through string resources (`context.getString(R.string.key)` / `stringResource(R.string.key)`). Add each one to both `values/strings.xml` (Russian, default) and `values-en/strings.xml` in the module that owns the screen.
- **String resource naming**: the key mirrors the string's own (English) content, lowercased and snake_cased. Never use a category/role prefix like `task_error_load` or a `_title`/`_label` suffix. When the content is too long to spell out in full, take the first few meaningful words and append `_msg`.

  ```xml
  <string name="complete_task">Complete task</string>
  <string name="task_already_completed">Task already completed</string>
  <string name="sign_in_to_earn_points_msg">Sign in to earn points for completed tasks and events</string>
  ```

  Two strings whose content differs only by context still get distinct keys built from their own words (`enter_email` vs `enter_new_email`), not from where they are used.
