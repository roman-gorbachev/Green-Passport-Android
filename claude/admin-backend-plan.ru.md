# Бэкенд админ-панели «Зелёный паспорт»

Серверная часть плана `greenpassport-admin/claude/admin-panel-plan.ru.md` (§1 модель данных, §2 бэкенд). Админка уже реализована. Что она фактически вызывает и читает, описано в `greenpassport-admin/claude/admin-backend-contract.ru.md`. Отличия от исходного плана собраны в §3 этого файла.

---

## 1. Модель данных (всё под `apps/greenpassport/`)

| Коллекция | Что меняется |
|---|---|
| `admins/{uid}` | + `role: SUPER_ADMIN \| EDITOR \| MODERATOR`, `email`, `grantedBy`. Нет `role` → `MODERATOR`; это обратная совместимость: приложения проверяют только наличие документа, и модерация в них остаётся у всех ролей. |
| `partners/{id}` **новая** | `name`, `names{ru,be,en}`, `logoUrl`, `contactEmail`, `isActive`, `createdAtEpochMillis` |
| `partnerUsers/{uid}` **новая** | `partnerId`, `email`, `grantedAtEpochMillis` |
| `shopItems/{id}` | + `partnerId`, `imageUrl`, `isActive`, `codeSource: GENERATED \| POOL`, `stockLimit?`, `issuedCount`, `usedCount` |
| `shopItems/{id}/codePool/{code}` **новая**, закрыта для клиентов | `status: AVAILABLE \| ISSUED`, `couponId?`, `issuedAtEpochMillis?` |
| `purchases/{id}` | + `partnerId`, `redeemedByUid`, `redeemedByPartnerId` |
| `tasks/{id}` | + `isActive`, `qrActiveFromEpochMillis?`, `qrActiveUntilEpochMillis?`, `qrScanLimit?` |
| `taskSecrets/{id}` | + `version`, `rotatedAtEpochMillis`, `scanCount` |
| `events/{id}` | + `isActive`, `qrMode: STATIC \| DYNAMIC` |
| `eventSecrets/{id}` | + `dynamicKey` (32 байта hex), `version` |
| `ecoTips`, `mapPoints`, `surveys`, `games` | + `isActive` (где его нет) |
| все контентные | + `updatedBy`, `updatedAtEpochMillis` (обязательны по правилам) |
| `auditLog/{id}` **новая** | `collection`, `docId`, `action`, `by`, `atEpochMillis`, `changedFields[]`. Пишет только триггер. |

Локализация остаётся как сейчас: русское поле-фолбэк плюс карта `{ru,be,en}`, например `title` + `titles`. Без фолбэка `localizedText` в приложениях вернёт `null`, и документ пропадёт.

---

---

## 2. Бэкенд

### 2.1 Роли в правилах — `firestore.rules`

**Почему.** Сейчас catch-all `write: if false`, поэтому редактор не может сохранить контент. Права должны различаться: модератор не правит контент, партнёр не видит чужое.

```
function staffRole() {
  let doc = get(/databases/$(database)/documents/apps/greenpassport/admins/$(request.auth.uid));
  return doc == null ? null : doc.data.get('role', 'MODERATOR');
}
function isEditor() { return isSignedIn() && staffRole() in ['SUPER_ADMIN', 'EDITOR']; }
function stampedBy() {
  return request.resource.data.updatedBy == request.auth.uid
    && request.resource.data.updatedAtEpochMillis is int;
}

match /apps/greenpassport/{content}/{docId}
    where content in ['tasks','events','shopItems','ecoTips','mapPoints','surveys','games','partners'] {
  allow create, update: if isEditor() && stampedBy();
  allow delete: if isEditor();
}
match /apps/greenpassport/partnerUsers/{uid} { allow read: if isSignedIn() && request.auth.uid == uid; allow write: if false; }
match /apps/greenpassport/auditLog/{id} { allow read: if isEditor(); allow write: if false; }
match /apps/greenpassport/shopItems/{id}/codePool/{code} { allow read, write: if false; }
```

(`where` в примере — это схема. В реальных правилах это отдельные `match` на каждую коллекцию или проверка `content in [...]` внутри `allow`.)

### 2.2 Storage для картинок контента — `storage.rules`

```
match /greenpassport/content/{kind}/{id}/{file} {
  allow read: if request.auth != null;
  allow write: if request.auth != null
    && firestore.get(/databases/(default)/documents/apps/greenpassport/admins/$(request.auth.uid)).data.get('role','MODERATOR') in ['SUPER_ADMIN','EDITOR']
    && request.resource.size < 5 * 1024 * 1024
    && request.resource.contentType.matches('image/.*');
}
```

### 2.3 Новые функции — `functions/src/admin/*.ts` (`europe-central2`)

Общий гард в `guards.ts`:

```ts
export async function requireRole(request: CallableRequest, roles: StaffRole[]): Promise<string> {
  const uid = requireUser(request);
  const snapshot = await adminsCollection().doc(uid).get();
  const role = (snapshot.get("role") as StaffRole | undefined) ?? "MODERATOR";
  if (!snapshot.exists || !roles.includes(role)) throw new HttpsError("permission-denied", "Insufficient role");
  return uid;
}
```

| Функция | Роль | Что делает |
|---|---|---|
| `adminSetStaffRole({email, role \| null})` | SUPER_ADMIN | `getUserByEmail`, пишет или удаляет `admins/{uid}` |
| `adminSetPartnerUser({email, partnerId \| null})` | SUPER_ADMIN | пишет или удаляет `partnerUsers/{uid}` |
| `adminListStaff()` | SUPER_ADMIN | сотрудники и партнёры с e-mail |
| `adminEnsureQrSecret({kind, id})` | EDITOR+ | создаёт секрет, если его нет, и возвращает payload |
| `adminRotateQrSecret({kind, id})` | EDITOR+ | новый `code`, `version + 1`, `scanCount = 0` |
| `adminGetQrPayloads({kind, ids[]})` | EDITOR+ | payload'ы для печати пачкой |
| `adminGetEventDynamicKey({eventId})` | EDITOR+ | создаёт или отдаёт `dynamicKey` |
| `adminImportCouponCodes({rewardId, codes[]})` | EDITOR+ или партнёр этой акции | дедупликация, ≤ 5000 за вызов, `AVAILABLE` |
| `partnerPreviewCoupon({couponId?, code})` | партнёр / SUPER_ADMIN | статус, акция, срок; без гашения |
| `partnerRedeemCoupon({couponId?, code})` | партнёр своей акции / SUPER_ADMIN | транзакция `ACTIVE` → `USED`, `usedCount + 1` |
| `expireCoupons` | `onSchedule` раз в сутки | просроченные `ACTIVE` → `EXPIRED` (для статистики) |
| `auditContentChange` | `onDocumentWritten` на контентных коллекциях (`us-central1`) | запись в `auditLog` |

Генерация секрета переиспользует формат сида: `crypto.randomBytes(16).toString("hex")`. Payload `greenpassport:task:<id>:<secret>` нельзя менять: его ждут приложения и уже напечатанные наклейки.

### 2.4 Изменения существующих функций

**`redeemTaskCode` (`functions/src/tasks.ts:45`).** Сейчас не проверяется, что задание вообще QR, и нет окна и лимита. Добавить в ту же транзакцию:

```ts
if (task.get("verification") !== "QR" || task.get("isActive") === false) throw unknownCode();
const now = Date.now();
const from = task.get("qrActiveFromEpochMillis") as number | undefined;
const until = task.get("qrActiveUntilEpochMillis") as number | undefined;
if ((from && now < from) || (until && now > until)) throw new HttpsError("failed-precondition", "qr_not_active");
const limit = task.get("qrScanLimit") as number | undefined;
const scanCount = (secret.get("scanCount") as number | undefined) ?? 0;
if (limit && scanCount >= limit) throw new HttpsError("resource-exhausted", "qr_limit_reached");
transaction.update(secret.ref, { scanCount: FieldValue.increment(1) });
```

**`checkInEvent` (`functions/src/events.ts:13`): динамический QR.** Формат строки остаётся `greenpassport:event:<id>:<secret>`, поэтому приложения менять не нужно. Динамический секрет выглядит как `d<окно>.<hmac>`:

```ts
const DYNAMIC_WINDOW_MILLIS = 30_000;
const ACCEPTED_PAST_WINDOWS = 1;
const DYNAMIC_SIGNATURE_LENGTH = 12;

export function dynamicSignature(key: string, eventId: string, window: number): string {
  return createHmac("sha256", Buffer.from(key, "hex")).update(`${eventId}:${window}`).digest("hex").slice(0, DYNAMIC_SIGNATURE_LENGTH);
}

function isValidDynamicSecret(secret: string, eventId: string, key: string, now: number): boolean {
  const match = /^d(\d+)\.([0-9a-f]+)$/.exec(secret);
  if (!match) return false;
  const window = Number(match[1]);
  const current = Math.floor(now / DYNAMIC_WINDOW_MILLIS);
  if (window > current || current - window > ACCEPTED_PAST_WINDOWS) return false;
  return timingSafeEqual(Buffer.from(match[2]), Buffer.from(dynamicSignature(key, eventId, window)));
}
```

Если `qrMode == DYNAMIC`, статический код отклоняется: распечатанный и пересланный QR не сработает. Админка получает ключ один раз и считает HMAC в браузере через WebCrypto. Так экран организатора работает и без интернета на площадке.

**`redeemReward` (`functions/src/shop.ts:19`).** Добавить:
- проверку `isActive`;
- лимит `stockLimit` против `issuedCount`, иначе `resource-exhausted` `reward_sold_out`;
- `issuedCount + 1`;
- `partnerId` в покупке;
- для `codeSource == POOL`: в транзакции `codePool.where("status","==","AVAILABLE").limit(1)` → `ISSUED`. Пустой пул → `reward_sold_out`.

**`scanCoupon` (`functions/src/shop.ts:81`).** Больше не гасит купон. Отвечает `302` на `https://greenpassport-admin.web.app/redeem?id=&code=`. Ссылки в уже выданных QR не меняются, приложения править не нужно.

**`markCouponUsed`.** Компромисс: это лазейка «гашу сам», но и запасной путь, если у партнёра нет интернета. Предлагаю оставить и показывать такие погашения в статистике партнёра отдельно (`redeemedByUid == userId`).

### 2.5 Сид

`seed-firestore.js` после передачи затрёт правки из админки, потому что делает upsert по русскому названию. Нужен флаг `--only-missing`, который не трогает существующие документы, и пометка в `scripts/README.md`, что после передачи источник правды — админка. `--admin=<uid>` пишет `role: "SUPER_ADMIN"`.

### 2.6 Тесты

- `rules-tests/`: редактор пишет контент, модератор не может, `updatedBy` чужой → отказ, `codePool` закрыт.
- `functions` integration:
  - окно и лимит QR;
  - ротация (старый код → `not-found`);
  - динамический токен (текущее окно ок, окно −2 отклоняется);
  - пул кодов, лимит акции;
  - гашение чужим партнёром → `permission-denied`.

---

## 3. Уточнения к §2 после реализации админки

### 3.1 Catch-all в правилах открывает `codePool`, `partnerUsers` и `auditLog`

**Почему.** Firestore объединяет `match` через ИЛИ. Последнее правило `apps/greenpassport/{collection}/{document=**}` разрешает чтение любому вошедшему пользователю, в том числе вложенных путей. Поэтому отдельный `match …/codePool/{code} { allow read: if false; }` из §2.1 ничего не закроет: пул кодов партнёра прочитает кто угодно. Так же откроются `partnerUsers` и `auditLog`.

**Код.** Catch-all делится на документ верхнего уровня и вложенные коллекции, закрытые коллекции добавляются в исключения:

```
function isClosedCollection(collection) {
  return collection in ['taskSecrets', 'admins', 'reports', 'taskSubmissions', 'pointsLedger', 'taskProgress',
    'purchases', 'ecoTipReads', 'eventSecrets', 'eventAttendance', 'chats', 'partnerUsers', 'auditLog'];
}

match /apps/greenpassport/{collection}/{docId} {
  allow read: if request.auth != null && !isClosedCollection(collection);
}

match /apps/greenpassport/{collection}/{docId}/{subcollection}/{rest=**} {
  allow read: if request.auth != null && !isClosedCollection(collection) && subcollection != 'codePool';
}
```

### 3.2 Чтение для админки

**Почему.** Перед удалением админка проверяет ссылки запросами `where <ref> == id limit 1`. При удалении партнёра это запрос к `partnerUsers` по `partnerId`. Правило «только свой uid» такой запрос отклонит. «Обзор» считает `purchases` через `count()`, журнал читает `auditLog`.

```
match /apps/greenpassport/partnerUsers/{uid} {
  allow read: if isOwner(uid) || isEditor();
}
match /apps/greenpassport/auditLog/{entryId} {
  allow read: if isEditor();
}
```

`taskProgress`, `purchases`, `ecoTipReads`, `eventAttendance` и `taskSubmissions` уже читаются через `isOwnerOrAdmin`, то есть любым сотрудником.

### 3.3 Счётчики и функции сверх §2.3

**Почему.** Админка не читает закрытый `codePool`, поэтому остаток пула хранится счётчиком в акции. Погашения через `markCouponUsed` («гашу сам») в статистике показываются отдельно.

| Что | Где |
|---|---|
| `shopItems.poolAvailableCount` | `+added` в `adminImportCouponCodes`, `-1` в `redeemReward` для `POOL` |
| `shopItems.selfMarkedCount` | `+1` в `markCouponUsed` вместе с `usedCount` |
| `partnerListRedemptions({partnerId?})` | последние погашения партнёра; для кассира `partnerId` берётся из `partnerUsers` |
| ошибка `cannot_demote_self` | `adminSetStaffRole`: супер-админ не снимает роль сам с себя, иначе можно остаться без супер-админа |

Ответы и тексты ошибок callable-функций — дословно по контракту админки:

```ts
export interface CouponPreview {
  couponId: string;
  state: 'active' | 'used' | 'expired' | 'notFound';
  rewardId: string;
  rewardTitle: string;
  partnerName: string;
  code: string;
  redeemedAtEpochMillis: number | null;
  expiresAtEpochMillis: number | null;
  usedAtEpochMillis: number | null;
}
```

### 3.4 Общий тестовый вектор динамического QR

**Почему.** Подпись считают два независимых кода: WebCrypto в админке и `node:crypto` на сервере. Расхождение заметно только на площадке, когда ни один скан не проходит.

```ts
test('the dynamic signature matches the admin test vector', () => {
  assert.equal(dynamicSignature(TEST_KEY, 'cleanup_2026', 59_666_666), '362f08279b8a');
});
```

### 3.5 Индексы журнала

**Почему.** Журнал фильтрует по `collection` и (или) `by` с сортировкой по `atEpochMillis desc`. Без составных индексов запрос падает с `failed-precondition`.

```json
{ "collectionGroup": "auditLog", "queryScope": "COLLECTION", "fields": [
  { "fieldPath": "collection", "order": "ASCENDING" }, { "fieldPath": "atEpochMillis", "order": "DESCENDING" } ] }
```

Плюс такие же индексы для `by` и для пары `collection` + `by`.

---

## Проверка

```bash
cd functions && npm run lint && npm test && cd ..
firebase emulators:exec --only firestore,storage "npm --prefix rules-tests test"
firebase emulators:exec --only auth,firestore,functions,storage "npm --prefix functions run test:integration"
```

Сценарии (эмулятор, затем прод) — раздел «Проверка» в плане админки:
1. QR-задание с лимитом 2: двое получают баллы, третий видит `qr_limit_reached`. После перевыпуска старый код даёт `not-found`.
2. Событие `DYNAMIC`: код с экрана `/live` проходит, код старше двух окон отклоняется, статический код отклоняется.
3. Акция с пулом из трёх кодов: три покупки получают коды пула, четвёртая — `reward_sold_out`. Партнёр B не гасит купон партнёра A (`foreign_coupon`).
4. Модератор не может сохранить задание (правила), редактор может только с `updatedBy == uid`.
5. Правка задания из админки появляется в `auditLog` с автором.
