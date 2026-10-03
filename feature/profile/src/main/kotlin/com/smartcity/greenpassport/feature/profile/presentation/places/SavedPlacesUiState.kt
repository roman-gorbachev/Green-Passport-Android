package com.smartcity.greenpassport.feature.profile.presentation.places

import com.smartcity.greenpassport.core.model.MapPoint

data class SavedPlacesUiState(
    val places: List<MapPoint> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
