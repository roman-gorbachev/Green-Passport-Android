package com.smartcity.greenpassport.feature.profile.presentation.profile

import com.smartcity.greenpassport.core.model.Level

data class ProfileUiState(
    val userId: String? = null,
    val email: String? = null,
    val isAnonymous: Boolean = false,
    val level: Level? = null,
    val points: Int = 0,
    val notificationsEnabled: Boolean = false,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
