package com.smartcity.greenpassport.feature.map.domain

import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointsRepository
import javax.inject.Inject

class GetMapPointsUseCase @Inject constructor(
    private val mapPointsRepository: MapPointsRepository,
) {
    suspend operator fun invoke(): List<MapPoint> = mapPointsRepository.getPoints()
}
