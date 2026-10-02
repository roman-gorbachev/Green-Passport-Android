package com.smartcity.greenpassport.core.model

import com.smartcity.greenpassport.core.model.verification.TaskVerification
import kotlinx.coroutines.flow.Flow

enum class TaskCategory {
    RECYCLING,
    CLEANUP,
    TRANSPORT,
    REUSABLE_ITEMS,
    LECTURE,
}

data class Task(
    val id: String,
    val title: LocalizedText,
    val description: LocalizedText,
    val category: TaskCategory,
    val city: String,
    val rewardPoints: Int,
    val rewardXp: Int,
    val imageUrl: String?,
    val verification: TaskVerification = TaskVerification.SELF,
    val isActive: Boolean = true,
)

interface TasksRepository {
    fun observeTasks(): Flow<List<Task>>
    fun observeCompletedTaskIds(userId: String): Flow<Set<String>>
    suspend fun getTasks(): List<Task>
    suspend fun getCompletedTaskIds(userId: String): Set<String>
}
