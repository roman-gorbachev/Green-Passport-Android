package com.smartcity.greenpassport.feature.tasks.presentation.state

import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.verification.TaskVerification

data class TaskFilters(
    val status: TaskStatusFilter = TaskStatusFilter.AVAILABLE,
    val verifications: Set<TaskVerification> = emptySet(),
    val city: TaskCityFilter = TaskCityFilter.ProfileCity,
    val categories: Set<TaskCategory> = emptySet(),
) {
    val activeCount: Int
        get() = listOf(status != TaskStatusFilter.AVAILABLE, city != TaskCityFilter.ProfileCity).count { it } +
            verifications.size + categories.size

    fun apply(
        tasks: List<Task>,
        completedIds: Set<String>,
        pendingIds: Set<String>,
        profileCity: String?,
    ): List<Task> = tasks.filter { task ->
        matchesStatus(task, completedIds, pendingIds) &&
            (verifications.isEmpty() || task.verification in verifications) &&
            city.matches(task, profileCity) &&
            (categories.isEmpty() || task.category in categories)
    }

    fun chips(): List<TaskFilterChip> = buildList {
        if (status != TaskStatusFilter.AVAILABLE) {
            add(TaskFilterChip.Status(status, copy(status = TaskStatusFilter.AVAILABLE)))
        }
        if (city != TaskCityFilter.ProfileCity) {
            add(TaskFilterChip.City(city, copy(city = TaskCityFilter.ProfileCity)))
        }
        TaskVerification.entries.filter { it in verifications }.forEach { verification ->
            add(TaskFilterChip.Verification(verification, copy(verifications = verifications - verification)))
        }
        TaskCategory.entries.filter { it in categories }.forEach { category ->
            add(TaskFilterChip.Category(category, copy(categories = categories - category)))
        }
    }

    private fun matchesStatus(task: Task, completedIds: Set<String>, pendingIds: Set<String>): Boolean {
        val isCompleted = task.id in completedIds
        val isPending = task.id in pendingIds
        return when (status) {
            TaskStatusFilter.AVAILABLE -> !isCompleted && !isPending
            TaskStatusFilter.UNDER_REVIEW -> isPending && !isCompleted
            TaskStatusFilter.COMPLETED -> isCompleted
            TaskStatusFilter.ALL -> true
        }
    }
}
