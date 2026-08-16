package com.smartcity.greenpassport.feature.profile.presentation

import com.smartcity.greenpassport.core.local.NotificationLogEntry

data class NotificationsUiState(
    val entries: List<NotificationLogEntry> = emptyList(),
    val isLoading: Boolean = true,
)
