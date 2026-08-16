package com.smartcity.greenpassport.core.network

data class Route(
    val distanceMeters: Int,
    val durationSeconds: Int,
    val polyline: String,
)

interface RoutingRepository {
    suspend fun getRoute(
        originLatitude: Double,
        originLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
    ): Route
}
