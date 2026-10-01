package com.smartcity.greenpassport.feature.tasks.presentation.state

import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.verification.TaskVerification

sealed interface TaskFilterChip {
    val remaining: TaskFilters

    data class Status(val status: TaskStatusFilter, override val remaining: TaskFilters) : TaskFilterChip

    data class City(val city: TaskCityFilter, override val remaining: TaskFilters) : TaskFilterChip

    data class Verification(val verification: TaskVerification, override val remaining: TaskFilters) : TaskFilterChip

    data class Category(val category: TaskCategory, override val remaining: TaskFilters) : TaskFilterChip
}
