package com.smartcity.greenpassport.feature.calendar.presentation.state

import com.smartcity.greenpassport.core.model.EcoEvent

data class CalendarUiState(
    val events: List<EcoEvent> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
