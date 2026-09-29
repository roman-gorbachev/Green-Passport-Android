package com.smartcity.greenpassport.feature.calendar.presentation.state

import com.smartcity.greenpassport.core.model.EcoEvent

data class EventDetailUiState(
    val event: EcoEvent? = null,
    val isRegistered: Boolean = false,
    val isRegistering: Boolean = false,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
