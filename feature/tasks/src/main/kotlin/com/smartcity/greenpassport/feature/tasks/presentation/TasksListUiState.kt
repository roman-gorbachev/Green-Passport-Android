package com.smartcity.greenpassport.feature.tasks.presentation

import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TaskCategory

data class TasksListUiState(
    val tasks: List<Task> = emptyList(),
    val completedTaskIds: Set<String> = emptySet(),
    val selectedCategory: TaskCategory? = null,
    val isLoading: Boolean = true,
) {
    val visibleTasks: List<Task>
        get() = selectedCategory?.let { category -> tasks.filter { it.category == category } } ?: tasks
}
