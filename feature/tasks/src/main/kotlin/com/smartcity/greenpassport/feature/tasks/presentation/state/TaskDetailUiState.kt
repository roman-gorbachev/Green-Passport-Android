package com.smartcity.greenpassport.feature.tasks.presentation.state

import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.core.model.verification.TaskSubmission

data class TaskDetailUiState(
    val task: Task? = null,
    val isCompleted: Boolean = false,
    val submission: TaskSubmission? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val hasError: Boolean = false,
    val earnedPoints: Int? = null,
    val failure: RewardFailure? = null,
)
