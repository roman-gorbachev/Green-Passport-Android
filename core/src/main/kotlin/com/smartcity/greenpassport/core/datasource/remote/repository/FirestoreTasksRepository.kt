package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.cacheFirstSnapshots
import com.smartcity.greenpassport.core.datasource.remote.localizedText
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.TasksRepository
import com.smartcity.greenpassport.core.model.verification.TaskVerification
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val FIELD_TITLE = "title"
private const val FIELD_DESCRIPTION = "description"
private const val FIELD_TITLES = "titles"
private const val FIELD_DESCRIPTIONS = "descriptions"
private const val FIELD_CATEGORY = "category"
private const val FIELD_CITY = "city"
private const val FIELD_REWARD_POINTS = "rewardPoints"
private const val FIELD_REWARD_XP = "rewardXp"
private const val FIELD_IMAGE_URL = "imageUrl"
private const val FIELD_VERIFICATION = "verification"

private const val FIELD_USER_ID = "userId"
private const val FIELD_TASK_ID = "taskId"

class FirestoreTasksRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : TasksRepository {

    override fun observeTasks(): Flow<List<Task>> =
        FirestoreCollections.tasks(firestore).cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toTask() } }
            .distinctUntilChanged()

    override fun observeCompletedTaskIds(userId: String): Flow<Set<String>> =
        FirestoreCollections.taskProgress(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.getString(FIELD_TASK_ID) }.toSet() }
            .distinctUntilChanged()

    override suspend fun getTasks(): List<Task> = observeTasks().first()

    override suspend fun getCompletedTaskIds(userId: String): Set<String> = observeCompletedTaskIds(userId).first()
}

private fun DocumentSnapshot.toTask(): Task? {
    val title = localizedText(FIELD_TITLE, FIELD_TITLES) ?: return null
    val description = localizedText(FIELD_DESCRIPTION, FIELD_DESCRIPTIONS) ?: return null
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
        verification = TaskVerification.entries.firstOrNull { it.name == getString(FIELD_VERIFICATION) }
            ?: TaskVerification.SELF,
    )
}
