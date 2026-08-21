package com.smartcity.greenpassport.feature.tasks.presentation.state

import com.smartcity.greenpassport.core.model.Task

data class TaskDetailUiState(
    val task: Task? = null,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val hasError: Boolean = false,
)
