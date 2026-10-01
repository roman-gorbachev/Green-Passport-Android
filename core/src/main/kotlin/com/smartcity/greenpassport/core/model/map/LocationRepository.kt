package com.smartcity.greenpassport.core.model.map

interface LocationRepository {
    fun hasLocationPermission(): Boolean

    suspend fun currentLocation(): GeoPoint?
}
