package com.smartcity.greenpassport.feature.map.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.model.MapPointType

fun mapPointTypeIcon(type: MapPointType): ImageVector = when (type) {
    MapPointType.ECO_SHOP -> Icons.Filled.Storefront
    MapPointType.RECYCLING_POINT -> Icons.Filled.Recycling
    MapPointType.ECO_EVENT -> Icons.Filled.Event
}

@Composable
fun mapPointTypeColor(type: MapPointType): Color {
    val sectionColors = GreenPassportTheme.sectionColors
    return when (type) {
        MapPointType.ECO_SHOP -> sectionColors.games
        MapPointType.RECYCLING_POINT -> sectionColors.community
        MapPointType.ECO_EVENT -> sectionColors.calendar
    }
}
