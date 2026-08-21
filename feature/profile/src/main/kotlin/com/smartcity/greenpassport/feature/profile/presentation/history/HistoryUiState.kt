package com.smartcity.greenpassport.feature.profile.presentation.history

import com.smartcity.greenpassport.core.model.HistoryEntry

data class HistoryUiState(
    val entries: List<HistoryEntry> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
