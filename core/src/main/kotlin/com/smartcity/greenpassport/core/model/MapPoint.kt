package com.smartcity.greenpassport.core.model

import kotlinx.coroutines.flow.Flow

enum class MapPointType {
    ECO_SHOP,
    RECYCLING_POINT,
    ECO_EVENT,
}

data class MapPoint(
    val id: String,
    val name: String,
    val type: MapPointType,
    val address: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
)

interface MapPointsRepository {
    fun observePoints(): Flow<List<MapPoint>>
    suspend fun getPoints(): List<MapPoint>
}
