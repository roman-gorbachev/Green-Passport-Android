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
:app                    — entry point, DI graph, navigation shell (bottom bar)
:core                    — shared models, Firebase repositories, design system, navigation
:feature:auth            — sign in / sign up
:feature:home            — home screen
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

## Firebase setup

The app talks to the Firebase project `chatroom-85fb8` (see `.firebaserc`). Until these steps are done, sign-in fails and every Firestore read returns `PERMISSION_DENIED`.

1. **Authentication** → Get started → Sign-in method: enable **Email/Password** and **Anonymous**.
2. **Firestore Database**: create the database (production mode) if it doesn't exist yet.
3. Publish the security rules from this repo — `firestore.rules`, `storage.rules` and `firestore.indexes.json` are the source of truth, there are no separate JSON rule files:

   ```bash
   npm install -g firebase-tools
   firebase login
   firebase deploy --only firestore:rules,firestore:indexes --project chatroom-85fb8
   firebase deploy --only storage --project chatroom-85fb8
   ```

   Or paste the contents of `firestore.rules` into Firestore → Rules (and `storage.rules` into Storage → Rules) in the console and press Publish. The app doesn't read from Storage yet, so the storage step can be skipped if the console asks for the Blaze plan.
4. Seed the demo data: `cd scripts && npm install && node seed-firestore.js` (see `scripts/README.md`).

## Known limitations of the pilot

- **Map** uses Yandex MapKit. Put `YANDEX_MAPKIT_API_KEY=<key>` into `local.properties` (key from the Yandex developer console, product "MapKit Mobile SDK"). Without a key the Map tab falls back to a list of points. **Route building** isn't implemented.
- Firestore demo data (tasks, rewards, map points, events, eco tips) can be seeded with `scripts/seed-firestore.js` — see `scripts/README.md`.
