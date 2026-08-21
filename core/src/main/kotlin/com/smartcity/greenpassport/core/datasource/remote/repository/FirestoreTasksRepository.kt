package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.TasksRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_TITLE = "title"
private const val FIELD_DESCRIPTION = "description"
private const val FIELD_CATEGORY = "category"
private const val FIELD_CITY = "city"
private const val FIELD_REWARD_POINTS = "rewardPoints"
private const val FIELD_REWARD_XP = "rewardXp"
private const val FIELD_IMAGE_URL = "imageUrl"

private const val FIELD_USER_ID = "userId"
private const val FIELD_TASK_ID = "taskId"
private const val FIELD_COMPLETED_AT = "completedAtEpochMillis"

class FirestoreTasksRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : TasksRepository {

    override suspend fun getTasks(): List<Task> {
        val snapshot = FirestoreCollections.tasks(firestore).get().await()
        return snapshot.documents.mapNotNull { it.toTask() }
    }

    override suspend fun getCompletedTaskIds(userId: String): Set<String> {
        val snapshot = FirestoreCollections.taskProgress(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.getString(FIELD_TASK_ID) }.toSet()
    }

    override suspend fun markTaskCompleted(userId: String, taskId: String) {
        val progressId = "${userId}_$taskId"
        FirestoreCollections.taskProgress(firestore).document(progressId)
            .set(
                mapOf(
                    FIELD_USER_ID to userId,
                    FIELD_TASK_ID to taskId,
                    FIELD_COMPLETED_AT to System.currentTimeMillis(),
                ),
            )
            .await()
    }
}

private fun DocumentSnapshot.toTask(): Task? {
    val title = getString(FIELD_TITLE) ?: return null
    val description = getString(FIELD_DESCRIPTION) ?: return null
    val category = getString(FIELD_CATEGORY)?.let { name ->
        runCatching { TaskCategory.valueOf(name) }.getOrNull()
    } ?: return null
    val city = getString(FIELD_CITY) ?: return null

    return Task(
        id = id,
        title = title,
        description = description,
        category = category,
        city = city,
        rewardPoints = getLong(FIELD_REWARD_POINTS)?.toInt() ?: 0,
        rewardXp = getLong(FIELD_REWARD_XP)?.toInt() ?: 0,
        imageUrl = getString(FIELD_IMAGE_URL),
    )
}
