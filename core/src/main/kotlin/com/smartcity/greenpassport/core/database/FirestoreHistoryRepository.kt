package com.smartcity.greenpassport.core.database

import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.model.EventsRepository
import com.smartcity.greenpassport.core.model.HistoryEntry
import com.smartcity.greenpassport.core.model.HistoryEntryType
import com.smartcity.greenpassport.core.model.HistoryRepository
import com.smartcity.greenpassport.core.model.ShopRepository
import com.smartcity.greenpassport.core.model.TasksRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

private const val FIELD_USER_ID = "userId"
private const val FIELD_TASK_ID = "taskId"
private const val FIELD_COMPLETED_AT = "completedAtEpochMillis"
private const val FIELD_EVENT_ID = "eventId"
private const val FIELD_REGISTERED_AT = "registeredAtEpochMillis"

class FirestoreHistoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val tasksRepository: TasksRepository,
    private val eventsRepository: EventsRepository,
    private val shopRepository: ShopRepository,
) : HistoryRepository {

    override suspend fun getHistory(userId: String): List<HistoryEntry> {
        val taskTitles = tasksRepository.getTasks().associate { it.id to it.title }
        val eventTitles = eventsRepository.getEvents().associate { it.id to it.title }
        val rewardTitles = shopRepository.getRewards().associate { it.id to it.title }

        val taskEntries = FirestoreCollections.taskProgress(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val taskId = doc.getString(FIELD_TASK_ID) ?: return@mapNotNull null
                val completedAt = doc.getLong(FIELD_COMPLETED_AT) ?: return@mapNotNull null
                HistoryEntry(
                    id = doc.id,
                    type = HistoryEntryType.TASK_COMPLETED,
                    title = taskTitles[taskId] ?: taskId,
                    timestampEpochMillis = completedAt,
                )
            }

        val eventEntries = FirestoreCollections.eventRegistrations(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val eventId = doc.getString(FIELD_EVENT_ID) ?: return@mapNotNull null
                val registeredAt = doc.getLong(FIELD_REGISTERED_AT) ?: return@mapNotNull null
                HistoryEntry(
                    id = doc.id,
                    type = HistoryEntryType.EVENT_ATTENDED,
                    title = eventTitles[eventId] ?: eventId,
                    timestampEpochMillis = registeredAt,
                )
            }

        val rewardEntries = shopRepository.getPurchases(userId).map { coupon ->
            HistoryEntry(
                id = coupon.id,
                type = HistoryEntryType.REWARD_REDEEMED,
                title = rewardTitles[coupon.rewardId] ?: coupon.rewardId,
                timestampEpochMillis = coupon.redeemedAtEpochMillis,
            )
        }

        return (taskEntries + eventEntries + rewardEntries).sortedByDescending { it.timestampEpochMillis }
    }
}
