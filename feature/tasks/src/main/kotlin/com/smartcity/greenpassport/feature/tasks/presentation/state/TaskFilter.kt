package com.smartcity.greenpassport.feature.tasks.presentation.state

import com.smartcity.greenpassport.core.model.TaskCategory

sealed interface TaskFilter {
    data object ForYou : TaskFilter

    data object All : TaskFilter

    data class Category(val category: TaskCategory) : TaskFilter
}
