package com.smartcity.greenpassport.core.model

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
)

interface TasksRepository {
    suspend fun getTasks(): List<Task>
    suspend fun getCompletedTaskIds(userId: String): Set<String>
    suspend fun markTaskCompleted(userId: String, taskId: String)
}
