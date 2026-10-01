package com.smartcity.greenpassport.core.model.map

sealed interface MapFocus {
    val point: GeoPoint

    data class UserLocation(override val point: GeoPoint) : MapFocus

    data class City(override val point: GeoPoint) : MapFocus
}
