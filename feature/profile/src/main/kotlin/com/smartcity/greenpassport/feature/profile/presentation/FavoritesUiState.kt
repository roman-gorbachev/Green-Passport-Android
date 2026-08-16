package com.smartcity.greenpassport.feature.profile.presentation

import com.smartcity.greenpassport.core.model.Task

data class FavoritesUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
)
