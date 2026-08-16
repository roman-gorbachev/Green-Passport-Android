package com.smartcity.greenpassport.feature.calendar.presentation

import com.smartcity.greenpassport.core.model.EcoEvent

data class CalendarUiState(
    val events: List<EcoEvent> = emptyList(),
    val registeredEventIds: Set<String> = emptySet(),
    val registeringEventId: String? = null,
    val isLoading: Boolean = true,
)
