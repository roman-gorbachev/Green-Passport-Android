# Demo data seed script

Fills `apps/greenpassport/{tasks,shopItems,mapPoints,events,ecoTips,surveys,games}` in Firestore,
matching the exact field names the app's repositories expect
(see `core.database.Firestore*Repository`). The content lives in `content/`, one module per
collection, with every text as a `{ru, be, en}` map plus the Russian field as a fallback:
tasks (30, all 12 cities), shop rewards (15), map points (19), calendar events (12),
eco tips (20, one marked as tip of the day; covers in `games/covers/`), surveys (3, one active)
and the games catalog (10 active, 8 retired).

Re-running is safe: existing documents are matched by their Russian title (map points by
name and address) and updated in place, so completed tasks, registrations and coupons keep
their references; QR secrets are only created when missing. New map points must be real
collection points — only add an address you found in an official source and confirmed by
geocoding.

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

Each run updates the seeded documents in place (see above), so it can be repeated.
Documents that are no longer in `content/` are not deleted — remove them in the Firebase
Console if needed.

### Refreshing demo events

The home screen only shows an upcoming event (`startAtEpochMillis` in the future). The seeded
events are dated October 2026 – January 2027 (`startAt` in `content/events.js`, Minsk time).
Once they are in the past, move the dates forward and re-seed only that collection:

```
node seed-firestore.js --only=events
```

`--only` takes a comma-separated list of collection names and skips the rest.

### After the hand-over: the admin panel is the source of truth

Once content is edited in the admin panel (`greenpassport-admin`), a plain re-run would overwrite
those edits, because documents are matched by their Russian title. Seed only what is missing:

```
node seed-firestore.js --only-missing
```

Existing documents are left untouched; new ones from `content/` are added. Every seeded write
is merged (fields set in the admin panel, such as `partnerId` or the shop counters, survive) and
stamped with `updatedBy: "system"`, so the admin's change log shows it as a script change.

`--admin=<uid>` makes that account a super admin (`admins/{uid}.role = SUPER_ADMIN`), who then
grants the other roles in the admin panel.

## Backfill groups

Groups created before invite codes existed have no `inviteCode`, `ownerId` or
`createdAtEpochMillis`. Fill them in once (the first member becomes the owner):

```
node backfill-groups.js --dry-run
node backfill-groups.js
```

Running it again is safe: groups that already have the fields are skipped.
