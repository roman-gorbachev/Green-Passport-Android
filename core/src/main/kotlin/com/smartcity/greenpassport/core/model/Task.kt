package com.smartcity.greenpassport.core.model

import com.smartcity.greenpassport.core.model.verification.TaskVerification

enum class TaskCategory {
    RECYCLING,
    CLEANUP,
    TRANSPORT,
    REUSABLE_ITEMS,
    LECTURE,
}

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val city: String,
    val rewardPoints: Int,
    val rewardXp: Int,
    val imageUrl: String?,
    val verification: TaskVerification = TaskVerification.SELF,
)

interface TasksRepository {
    suspend fun getTasks(): List<Task>
    suspend fun getCompletedTaskIds(userId: String): Set<String>
}
