# Demo data seed script

Fills `apps/greenpassport/{tasks,shopItems,mapPoints,events,ecoTips,surveys}` in Firestore with
sample content, matching the exact field names the app's repositories expect
(see `core.database.Firestore*Repository`). These are the collections that were empty
placeholders — Tasks (10), Shop rewards (6), Map points (5), Calendar events (4),
Eco tips (7, one marked as tip of the day), one active survey.

Not seeded: `posts`/`groups`/`chats` (need real Firebase Auth user IDs, which don't
exist until a real user signs in) and `users` (created lazily by the app itself).

## 1. Get a service account key

Firebase Console → Project settings → Service accounts → **Generate new private key**.
This downloads a JSON file — save it as `scripts/service-account.json`
(already in `.gitignore`, never commit it).

## 2. Install the dependency

```
cd scripts
npm init -y
npm install firebase-admin
```

## 3. Run it

```
node seed-firestore.js
```

Or point at a key stored elsewhere:

```
node seed-firestore.js /path/to/service-account.json
```

Each run **adds** new documents (auto-generated IDs) — running it twice duplicates
the data. Delete the collections in the Firebase Console first if you want to reset,
or edit the script to use fixed doc IDs instead of `collectionRef.doc()`.
