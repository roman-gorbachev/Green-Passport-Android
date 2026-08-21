package com.smartcity.greenpassport.feature.profile.presentation.achievements

import com.smartcity.greenpassport.core.model.Achievement

data class AchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
