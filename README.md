# Green Passport

A mobile app that turns everyday eco-friendly actions into points and real rewards.

Users complete eco tasks (recycling, cleanups, giving up single-use items), join local events, read eco tips, and play themed mini-games — earning points and XP along the way. Points are redeemed for discounts and coupons from partners.

## Stack

- **Kotlin + Jetpack Compose**, Material 3
- **Firebase**: Auth, Cloud Firestore, Storage, Cloud Messaging
- **Hilt** (DI), **KSP** (annotation processing — kapt is not used)
- **Room** — for data that genuinely doesn't belong in Firestore (e.g. local mini-game progress)
- **Jetpack Navigation Compose** with type-safe routes (`kotlinx.serialization`)
- Clean Architecture: a single `:core` module + one Gradle module per feature (`:feature:<name>`)

## Features

Tasks · Rewards shop · Event calendar · Map of recycling points · Community (forum, groups) · Eco tips · Mini-games (waste sorting, maze, quiz, memory match) · Profile (achievements, card collection, history, favorites) · Feedback and surveys

## Build & run

Requires JDK 17+ and the Android SDK (`compileSdk 36`, `minSdk 26`).

```bash
./gradlew assembleDebug          # debug build
./gradlew build                  # full build: code + lint + unit tests
./gradlew installDebug            # install on a connected device/emulator
./gradlew test                    # unit tests
./gradlew connectedAndroidTest     # instrumented tests (needs a device)
```

On Windows PowerShell, use `gradlew.bat` instead of `./gradlew` — it wraps the same Gradle wrapper.

## Project structure

```
:app                    — entry point, DI graph, navigation, home screen
:core                    — shared models, Firebase repositories, design system, navigation
:feature:auth            — sign in / sign up
:feature:profile         — profile, achievements, history
:feature:tasks           — eco tasks
:feature:shop            — rewards shop
:feature:calendar        — events and reminders
:feature:map             — map points
:feature:community       — forum and groups
:feature:ecotips         — eco tips
:feature:feedback        — reviews and surveys
:feature:games           — mini-games
```

`:core` doesn't depend on any feature; every `:feature:<name>` depends only on `:core`. Inside each module, code is split into `domain` (use cases) and `presentation` (Compose screens and ViewModels) packages.

## Known limitations of the pilot

- **Firebase Authentication** isn't enabled in the project console yet — sign-in (including anonymous) won't work until the product is turned on in the Firebase Console.
- **Map** is a list of points, not an interactive map; **route building** isn't implemented — both need a Google Maps/Directions API key.
- Firestore demo data (tasks, rewards, map points, events, eco tips) can be seeded with `scripts/seed-firestore.js` — see `scripts/README.md`.
