package com.smartcity.greenpassport.feature.tasks.presentation.state

import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.profile.UserProfile

data class TasksListUiState(
    val tasks: List<Task> = emptyList(),
    val completedTaskIds: Set<String> = emptySet(),
    val favoriteTaskIds: Set<String> = emptySet(),
    val pendingTaskIds: Set<String> = emptySet(),
    val profile: UserProfile? = null,
    val filter: TaskFilter = TaskFilter.ForYou,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
) {
    val effectiveFilter: TaskFilter
        get() = if (filter == TaskFilter.ForYou && profile == null) TaskFilter.All else filter

    val visibleTasks: List<Task>
        get() = when (val current = effectiveFilter) {
            TaskFilter.All -> tasks
            is TaskFilter.Category -> tasks.filter { it.category == current.category }
            TaskFilter.ForYou -> {
                val userProfile = profile ?: return tasks
                tasks
                    .filter { it.city == userProfile.city || it.category in userProfile.interests }
                    .sortedByDescending { it.city == userProfile.city && it.category in userProfile.interests }
            }
        }
}
