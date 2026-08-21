package com.smartcity.greenpassport.feature.profile.presentation.favorites

import com.smartcity.greenpassport.core.model.Task

data class FavoritesUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
