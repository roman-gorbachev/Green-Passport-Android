package com.smartcity.greenpassport.feature.profile.presentation.places

import com.smartcity.greenpassport.core.model.MapPointType
import com.smartcity.greenpassport.feature.profile.R

fun mapPointTypeLabelRes(type: MapPointType): Int = when (type) {
    MapPointType.ECO_SHOP -> R.string.map_type_eco_shop
    MapPointType.RECYCLING_POINT -> R.string.map_type_recycling_point
    MapPointType.ECO_EVENT -> R.string.map_type_eco_event
}
