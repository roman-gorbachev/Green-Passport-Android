package com.smartcity.greenpassport.feature.profile.presentation.notifications

import com.smartcity.greenpassport.core.datasource.local.repository.NotificationLogEntry

data class NotificationsUiState(
    val entries: List<NotificationLogEntry> = emptyList(),
    val isLoading: Boolean = true,
)
