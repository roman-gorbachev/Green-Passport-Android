# Green Passport

A mobile app that turns everyday eco-friendly actions into points and real rewards.

Users complete eco tasks (recycling, cleanups, giving up single-use items), join local events, read eco tips, and play themed mini-games — earning points and XP along the way. Points are redeemed for discounts and coupons from partners.

Content, moderation, QR codes and partner coupons are managed in the [web admin panel](https://github.com/roman-gorbachev/greenpassport-admin); the [iOS app](https://github.com/roman-gorbachev/Green-Passport-iOS) shares the same backend and UX. This repository also holds that backend: security rules, indexes and Cloud Functions.

## Stack

- **Kotlin + Jetpack Compose**, Material 3
- **Firebase**: Auth, Cloud Firestore, Storage, Cloud Messaging, Hosting
- **Cloud Functions** (TypeScript, `functions/`) — everything that changes points, QR codes, coupons and staff roles
- **Hilt** (DI), **KSP** (annotation processing — kapt is not used)
- **Room** — for data that genuinely doesn't belong in Firestore (e.g. local mini-game progress)
- **Jetpack Navigation Compose** with type-safe routes (`kotlinx.serialization`)
- Clean Architecture: a single `:core` module + one Gradle module per feature (`:feature:<name>`)

## Features

Tasks (self-reported, photo or QR) · Rewards shop and coupons · Event calendar with QR check-in · Map of recycling points · Community (forum, groups) · Eco tips · Web mini-games (`games/`, ten HTML5 games in a WebView) · Profile (achievements, history, favorites) · Feedback and surveys · Moderation

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
:feature:games           — web mini-games
:feature:moderation      — task photos and reported posts
```

Outside the Gradle build: `functions/` (Cloud Functions), `rules-tests/` (security rules tests), `scripts/` (seed and maintenance scripts), `games/` (the web games served by Firebase Hosting).

`:core` doesn't depend on any feature; every `:feature:<name>` depends only on `:core`. Inside each module, code is split into `domain` (use cases) and `presentation` (Compose screens and ViewModels) packages.

## Firebase setup

The app talks to the Firebase project `chatroom-85fb8` (see `.firebaserc`). Until these steps are done, sign-in fails and every Firestore read returns `PERMISSION_DENIED`.

1. **Authentication** → Get started → Sign-in method: enable **Email/Password**, **Anonymous** and **Google**.
   For Google sign-in also add the app's SHA-1 and SHA-256 (`./gradlew signingReport`, debug and release keys) in
   Project settings → Android app `com.smartcity.greenpassport`, then download the fresh `google-services.json` into
   `app/`. Until then the "Continue with Google" button reports that Google sign-in is unavailable.
2. **Firestore Database**: create the database (production mode) if it doesn't exist yet.
3. Publish the security rules from this repo — `firestore.rules`, `storage.rules` and `firestore.indexes.json` are the source of truth, there are no separate JSON rule files:

   ```bash
   npm install -g firebase-tools
   firebase login
   firebase deploy --only firestore:rules,firestore:indexes --project chatroom-85fb8
   firebase deploy --only storage --project chatroom-85fb8
   ```

   Or paste the contents of `firestore.rules` into Firestore → Rules (and `storage.rules` into Storage → Rules) in the console and press Publish. The app doesn't read from Storage yet, so the storage step can be skipped if the console asks for the Blaze plan.
4. Switch the project to the **Blaze** plan, enable the **Cloud Vision API** in Google Cloud, then deploy the backend:

   ```bash
   cd functions && npm install && cd ..
   firebase deploy --only functions,firestore:rules,firestore:indexes,storage --project chatroom-85fb8
   ```

   Points are awarded and spent only by Cloud Functions (`functions/`, region `europe-central2`); the rules forbid the app
   from writing `availablePoints`/`lifetimeXp`.
5. Make yourself a super admin: `cd scripts && npm install && node seed-firestore.js --only=none --admin=<your uid>` (uid from Authentication).
   Other staff roles and partner accounts are then granted in the web admin panel. Every staff member also sees "Moderation" in the app profile.
6. Seed the demo data: `node seed-firestore.js` from `scripts/` (see `scripts/README.md`). Once content is edited in the admin panel, seed only with `--only-missing`, otherwise the edits are overwritten.

Tests for the backend:

```bash
cd functions && npm test && cd ..
firebase emulators:exec --only firestore,storage "npm --prefix rules-tests test"
firebase emulators:exec --only auth,firestore,functions,storage "npm --prefix functions run test:integration"
```

## Known limitations of the pilot

- **Map** uses Yandex MapKit. Put `YANDEX_MAPKIT_API_KEY=<key>` into `local.properties` (key from the Yandex developer console, product "MapKit Mobile SDK"). Without a key the Map tab falls back to a list of points. "Route" opens the system maps app.
- Firestore demo data (tasks, rewards, map points, events, eco tips) can be seeded with `scripts/seed-firestore.js` — see `scripts/README.md`.

## License

Proprietary. Copyright (c) 2026 Roman Gorbachev. All rights reserved. See [LICENSE](LICENSE).

## Author

Roman Gorbachev
