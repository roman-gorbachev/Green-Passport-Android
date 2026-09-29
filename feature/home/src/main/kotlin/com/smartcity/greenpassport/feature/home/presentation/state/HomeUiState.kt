package com.smartcity.greenpassport.feature.home.presentation.state

import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.Level
import com.smartcity.greenpassport.core.model.Task

data class HomeUiState(
    val isLoading: Boolean = true,
    val hasTasksError: Boolean = false,
    val displayName: String? = null,
    val points: Int = 0,
    val level: Level? = null,
    val upcomingEvent: EcoEvent? = null,
    val tasks: List<Task> = emptyList(),
)
