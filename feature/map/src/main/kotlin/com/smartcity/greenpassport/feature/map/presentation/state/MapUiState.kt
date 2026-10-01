package com.smartcity.greenpassport.feature.map.presentation.state

import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointType
import com.smartcity.greenpassport.core.model.map.MapFocus

data class MapUiState(
    val points: List<MapPoint> = emptyList(),
    val savedPointIds: Set<String> = emptySet(),
    val selectedType: MapPointType? = null,
    val searchQuery: String = "",
    val selectedPointId: String? = null,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val focus: MapFocus? = null,
) {
    val visiblePoints: List<MapPoint>
        get() = points
            .filter { selectedType == null || it.type == selectedType }
            .filter { searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) }

    val selectedPoint: MapPoint?
        get() = points.firstOrNull { it.id == selectedPointId }
}
