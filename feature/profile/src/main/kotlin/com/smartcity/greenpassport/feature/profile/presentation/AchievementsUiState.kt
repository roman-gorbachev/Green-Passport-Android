package com.smartcity.greenpassport.feature.profile.presentation

import com.smartcity.greenpassport.core.model.Achievement

data class AchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true,
)
