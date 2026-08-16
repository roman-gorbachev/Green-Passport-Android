package com.smartcity.greenpassport.feature.map.presentation

import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointType

data class MapUiState(
    val points: List<MapPoint> = emptyList(),
    val savedPointIds: Set<String> = emptySet(),
    val selectedType: MapPointType? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
) {
    val visiblePoints: List<MapPoint>
        get() = points
            .filter { selectedType == null || it.type == selectedType }
            .filter { searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) }
}
