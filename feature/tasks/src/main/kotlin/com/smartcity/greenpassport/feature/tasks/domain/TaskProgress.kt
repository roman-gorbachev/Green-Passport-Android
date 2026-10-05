package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.Task

data class TaskProgress(
    val task: Task?,
    val isCompleted: Boolean,
)
