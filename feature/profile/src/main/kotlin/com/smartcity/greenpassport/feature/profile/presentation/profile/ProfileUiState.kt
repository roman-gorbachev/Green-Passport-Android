package com.smartcity.greenpassport.feature.profile.presentation.profile

import com.smartcity.greenpassport.core.model.Level
import com.smartcity.greenpassport.core.model.profile.UserProfile
import com.smartcity.greenpassport.core.model.settings.AppTheme
import com.smartcity.greenpassport.core.model.settings.NotificationCategory

data class ProfileUiState(
    val userId: String? = null,
    val email: String? = null,
    val isAnonymous: Boolean = false,
    val profile: UserProfile? = null,
    val isModerator: Boolean = false,
    val level: Level? = null,
    val points: Int = 0,
    val enabledNotificationCategories: Set<NotificationCategory> = emptySet(),
    val theme: AppTheme = AppTheme.SYSTEM,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
