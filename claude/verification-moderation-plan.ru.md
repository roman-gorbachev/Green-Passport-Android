# План 2. Города Беларуси, проверка заданий, очки на сервере, модерация

## Контекст

Пользователь попросил:
- убрать Москву и Санкт-Петербург и добавить города Беларуси, первым — Минск;
- реализовать план 2 из `claude/personalization-plan.ru.md`: проверка выполнения заданий, роль модератора, жалобы, очередь модерации, серверная проверка текста и фото (тариф Blaze разрешён).

Что сейчас в коде:
- **Очки начисляет клиент.** `PointsRepository.award` вызывают `CompleteTaskUseCase`, `MarkTipReadUseCase` и `SubmitGameResultUseCase`. Правила `users/{uid}` разрешают прибавить до 100 000 за раз, так что накрутка — дело секунд. Игра начисляет `points = score` без ограничений.
- **Задание засчитывается кнопкой** «Отметить выполненной» без проверки.
- **Покупка в магазине:** клиент сам списывает очки и сам пишет `purchases`.
- **Модерации нет:** жалоб нет, скрыть пост нельзя, ролей нет.
- **Города и точки на карте** в `SupportedCities` и `scripts/seed-firestore.js` — московские.

Регион Cloud Functions — `europe-central2` (Варшава), ближайший к Беларуси.

---

## Шаг 1. Города Беларуси

### Почему

Аудитория — Беларусь. Город из профиля сортирует задания («Для тебя»), поэтому список городов и города в демо-данных должны совпадать.

### Код

```kotlin
object SupportedCities {
    val all = listOf(
        "Минск", "Брест", "Витебск", "Гомель", "Гродно", "Могилёв",
        "Бобруйск", "Барановичи", "Борисов", "Пинск", "Орша", "Мозырь",
    )
}
```

`seed-firestore.js`: задания, события и точки карты переезжают в Минск, Гомель и Брест. Адреса и координаты реальные: пр-т Независимости, парк Горького в Минске, «Лошицкий парк» и т. д. Центр карты по умолчанию — Минск.

---

## Шаг 2. Способ проверки у каждого задания

### Почему

Проверка должна зависеть от того, что можно проверить:
- **Уборку или сдачу вторсырья** видно на фото.
- **Лекцию или день вторсырья** подтверждает QR-код организатора на месте.
- **Привычку** («неделю с многоразовой сумкой») проверить нельзя, поэтому очков за неё мало и есть лимит в день.

### Код

```kotlin
enum class TaskVerification { SELF, PHOTO, QR }

data class Task(
    …,
    val verification: TaskVerification,
)
```

Поле `verification` в документе задания; если его нет — `SELF`. Статусы фото-заявки:

```kotlin
enum class SubmissionStatus { PENDING, APPROVED, REJECTED }

data class TaskSubmission(
    val id: String,
    val taskId: String,
    val userId: String,
    val userName: String?,
    val photoUrl: String,
    val status: SubmissionStatus,
    val rejectionReason: String?,
    val createdAtEpochMillis: Long,
)
```

---

## Шаг 3. Очки только на сервере (Cloud Functions)

### Почему

Пока клиент может писать `availablePoints`, любая проверка задания обходится одним запросом к Firestore. Поэтому:
- начисление и списание переносятся в callable-функции;
- правила запрещают клиенту менять очки;
- `taskProgress`, `ecoTipReads` и `purchases` пишет только сервер.

### Код

`functions/` (TypeScript, Firebase Functions v2). Общий хелпер начисления в транзакции:

```ts
export async function awardInTransaction(
  tx: Transaction, uid: string, points: number, xp: number, reason: string,
) {
  const userRef = db.doc(`apps/greenpassport/users/${uid}`);
  tx.set(userRef, {
    availablePoints: FieldValue.increment(points),
    lifetimeXp: FieldValue.increment(xp),
  }, { merge: true });
  tx.create(db.collection('apps/greenpassport/pointsLedger').doc(), {
    userId: uid, points, xp, reason, createdAt: FieldValue.serverTimestamp(),
  });
}
```

| Функция | Что делает |
|---|---|
| `completeSelfTask({taskId})` | Только для `SELF`. Не больше 3 в сутки, не больше одного раза на задание. Пишет `taskProgress`, начисляет очки. |
| `redeemTaskCode({code})` | Код `greenpassport:task:{taskId}:{secret}`. Сверяет с `taskSecrets/{taskId}` (клиенту не читается), засчитывает один раз. |
| `recordTipRead({tipId})` | Один раз на совет. Пишет `ecoTipReads`. |
| `recordGameResult({gameId, score})` | `min(score, 30)` очков, не больше 5 наград в сутки. |
| `redeemReward({rewardId})` | Проверяет баланс, списывает, пишет `purchases`. |
| `reviewSubmission({submissionId, approve, reason})` | Только admin. Одобрение пишет `taskProgress` и начисляет очки. |
| `moderateContent({postId, action})` | Только admin: `hide` / `restore` / `delete`. |

Клиентский вызов в `:core`:

```kotlin
class FirebaseRewardsRepository @Inject constructor(
    private val functions: FirebaseFunctions,
    private val rewardNotifier: RewardNotifier,
) : RewardsRepository {
    override suspend fun completeSelfTask(taskId: String): RewardResult =
        call(FUNCTION_COMPLETE_SELF_TASK, mapOf(PARAM_TASK_ID to taskId), PointsEarnReason.TASK_COMPLETED)
}
```

Ошибки функций (`resource-exhausted` — лимит, `already-exists` — уже засчитано, `not-found` — неверный код) переводятся в `RewardFailure`, а UI показывает понятный текст.

Правила `users/{uid}` — клиент меняет только профиль:

```
allow update: if isOwner(userId) && hasCleanProfileNames(request.resource.data) &&
  !request.resource.data.diff(resource.data).affectedKeys()
    .hasAny(['availablePoints', 'lifetimeXp']);
```

`PointsRepository` теряет `award`/`spend` и остаётся только для чтения.

---

## Шаг 4. Фото-подтверждение и автопроверка фото

### Почему

Фото смотрит модератор. Непристойное фото не должно даже попасть к нему в очередь: Cloud Vision SafeSearch отклоняет его автоматически и удаляет файл.

### Код

Клиент:
1. Выбирает фото через `PickVisualMedia` (без разрешений).
2. Сжимает до 1600px JPEG.
3. Загружает в `greenpassport/submissions/{uid}/{submissionId}.jpg`.
4. Создаёт `taskSubmissions/{submissionId}` со статусом `PENDING`.

`storage.rules`:

```
match /greenpassport/submissions/{userId}/{fileName} {
  allow write: if request.auth.uid == userId &&
    request.resource.size < 5 * 1024 * 1024 && request.resource.contentType.matches('image/.*');
  allow read: if request.auth.uid == userId || isAdmin();
}
```

Триггер:

```ts
export const screenSubmissionPhoto = onDocumentCreated(
  { region: REGION, document: 'apps/greenpassport/taskSubmissions/{id}' },
  async (event) => {
    const [result] = await vision.safeSearchDetection(`gs://${bucket}/${data.photoPath}`);
    if (isUnsafe(result.safeSearchAnnotation)) {
      await event.data.ref.update({ status: 'REJECTED', rejectionReason: 'unsafe_photo' });
      await storage.bucket().file(data.photoPath).delete();
    }
  },
);
```

`isUnsafe` — `adult`, `violence` или `racy` на уровне `LIKELY`/`VERY_LIKELY`.

---

## Шаг 5. QR-коды

### Почему

Мероприятие или пункт приёма подтверждается присутствием: организатор показывает QR, пользователь сканирует. Google Code Scanner (`play-services-code-scanner`) даёт готовый экран сканирования без разрешения на камеру и без CameraX.

### Код

```kotlin
class QrCodeScanner @Inject constructor() {
    suspend fun scan(activityContext: Context): String? =
        GmsBarcodeScanning.getClient(activityContext, options).startScan().await().rawValue
}
```

`seed-firestore.js` для QR-заданий создаёт `taskSecrets/{taskId}` со случайным кодом и сохраняет PNG в `scripts/qr/` через пакет `qrcode`. Эти картинки печатаются для организаторов.

---

## Шаг 6. Жалобы и автоскрытие

### Почему

Словарь не ловит оскорбления без мата, спам и травлю. Жалобы пользователей закрывают этот пробел. После трёх разных жалоб пост скрывается до решения модератора.

### Код

Клиент создаёт `reports/{postId}_{uid}` (id гарантирует одну жалобу от человека):

```kotlin
data class ContentReport(val postId: String, val reporterId: String, val reason: ReportReason)
enum class ReportReason { OFFENSIVE, SPAM, INAPPROPRIATE_IMAGE, OTHER }
```

Триггер `onReportCreated` увеличивает `posts/{postId}.reportCount` и при `>= 3` ставит `hidden = true`. Триггер `screenForumPost` проверяет текст тем же словарём, что и приложение: `banned_roots.txt` копируется в `functions/` при сборке. Форум показывает только `hidden != true`, а в меню поста появляется «Пожаловаться».

---

## Шаг 7. Роль модератора и экран «Модерация»

### Почему

Решения по фото и жалобам принимает человек прямо в приложении.

### Код

- **Роль.** Документ `apps/greenpassport/admins/{uid}`; назначается вручную в консоли. `isAdmin()` в правилах:

  ```
  function isAdmin() {
    return request.auth != null &&
      exists(/databases/$(database)/documents/apps/greenpassport/admins/$(request.auth.uid));
  }
  ```

- **Модуль `:feature:moderation`.** Экран с двумя вкладками:
  - «Фото заданий» — очередь `PENDING`: фото, задание, автор, кнопки «Одобрить» и «Отклонить» с причиной;
  - «Жалобы» — скрытые и обжалованные посты: «Вернуть» и «Удалить».
- **Вход.** Пункт «Модерация» в профиле виден только администратору (`ModerationRepository.observeIsAdmin`).

---

## Критичные файлы

- `core/.../model/{Task,…}`, новые `core/.../model/{verification,moderation,rewards}/*`
- `core/.../datasource/remote/{functions,repository}/*`, `core/.../di/*`
- `feature/tasks/...` (шторка задания: SELF/PHOTO/QR), `feature/ecotips`, `feature/games`, `feature/shop`, `feature/community`, `feature/profile`
- новый `feature/moderation`, `settings.gradle.kts`, `app/build.gradle.kts`, `AppNavHost`, `Destination`
- `functions/**`, `firebase.json`, `firestore.rules`, `storage.rules`, `firestore.indexes.json`
- `scripts/seed-firestore.js`, `scripts/package.json`, `README.md`, `CLAUDE.md`

## Проверка

```bash
./gradlew build
./gradlew detektAll
cd functions && npm install && npm run build && npm run lint
firebase deploy --only functions,firestore:rules,firestore:indexes,storage --project chatroom-85fb8
node scripts/seed-firestore.js
```

Сценарии:
1. **Города.** В профиле первым идёт Минск; задания Минска стоят первыми у пользователя из Минска.
2. **SELF-задание:**
   - «Отметить выполненной» → очки начислены, уведомление;
   - четвёртое за день → «Лимит на сегодня исчерпан»;
   - повтор того же задания → «Уже выполнено».
3. **PHOTO-задание:**
   - фото → «На проверке»;
   - модератор одобряет → очки у пользователя, в заданиях «Выполнено»;
   - отклонение → причина видна пользователю;
   - непристойное фото отклоняется автоматически и исчезает из Storage.
4. **QR-задание:**
   - скан кода из `scripts/qr/` → очки;
   - чужой или неверный код → «Код не подходит»;
   - повторный скан → «Уже выполнено».
5. **Накрутка.** Прямая запись `availablePoints` из консоли под пользователем (Rules Playground) → отказ.
6. **Магазин.** Покупка списывает очки через функцию; при нехватке — ошибка, баланс не меняется.
7. **Жалобы:**
   - три жалобы от разных аккаунтов → пост исчезает из ленты и появляется у модератора;
   - «Вернуть» → пост снова в ленте.
8. **Серверный фильтр.** Пост с матом, записанный в обход приложения, скрывается триггером.
9. **Доступ.** Пункт «Модерация» не виден обычному пользователю; вызов `reviewSubmission` не-админом → `permission-denied`.
