package com.smartcity.greenpassport.feature.tasks.presentation.state

import com.smartcity.greenpassport.core.model.Task

sealed interface TaskCityFilter {
    data object ProfileCity : TaskCityFilter

    data object All : TaskCityFilter

    data class City(val name: String) : TaskCityFilter

    fun matches(task: Task, profileCity: String?): Boolean = when (this) {
        ProfileCity -> profileCity.isNullOrBlank() || task.city == profileCity
        All -> true
        is City -> task.city == name
    }
}
