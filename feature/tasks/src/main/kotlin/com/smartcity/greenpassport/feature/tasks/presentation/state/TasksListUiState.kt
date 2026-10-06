package com.smartcity.greenpassport.feature.tasks.presentation.state

import com.smartcity.greenpassport.core.common.matchesSearchQuery
import com.smartcity.greenpassport.core.common.resolveForDeviceLanguage
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.profile.UserProfile

data class TasksListUiState(
    val tasks: List<Task> = emptyList(),
    val completedTaskIds: Set<String> = emptySet(),
    val favoriteTaskIds: Set<String> = emptySet(),
    val pendingTaskIds: Set<String> = emptySet(),
    val profile: UserProfile? = null,
    val filters: TaskFilters = TaskFilters(),
    val query: String = "",
    val isFilterSheetVisible: Boolean = false,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
) {
    val isSearching: Boolean
        get() = query.isNotBlank()

    val visibleTasks: List<Task>
        get() = tasksMatching(filters)

    fun tasksMatching(candidate: TaskFilters): List<Task> {
        val currentProfile = profile
        return candidate
            .apply(tasks, completedTaskIds, pendingTaskIds, currentProfile?.city)
            .filter { it.title.resolveForDeviceLanguage().matchesSearchQuery(query) }
            .sortedWith(
                compareByDescending<Task> { currentProfile != null && it.city == currentProfile.city }
                    .thenByDescending { currentProfile != null && it.category in currentProfile.interests },
            )
    }
}
