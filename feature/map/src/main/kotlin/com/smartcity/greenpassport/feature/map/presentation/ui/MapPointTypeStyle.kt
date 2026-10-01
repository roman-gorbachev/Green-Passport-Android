package com.smartcity.greenpassport.feature.map.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.core.model.MapPointType

fun mapPointTypeIcon(type: MapPointType): ImageVector = when (type) {
    MapPointType.ECO_SHOP -> Icons.Filled.Storefront
    MapPointType.RECYCLING_POINT -> Icons.Filled.Recycling
    MapPointType.ECO_EVENT -> Icons.Filled.Event
}
