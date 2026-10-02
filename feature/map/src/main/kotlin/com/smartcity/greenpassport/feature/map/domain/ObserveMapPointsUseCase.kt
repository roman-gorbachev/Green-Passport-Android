package com.smartcity.greenpassport.feature.map.domain

import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveMapPointsUseCase @Inject constructor(
    private val mapPointsRepository: MapPointsRepository,
) {
    operator fun invoke(): Flow<List<MapPoint>> =
        mapPointsRepository.observePoints().map { points -> points.filter { it.isActive } }
}
