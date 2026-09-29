package com.smartcity.greenpassport.feature.home.domain

import com.smartcity.greenpassport.core.model.Level
import com.smartcity.greenpassport.core.model.LevelProgression
import com.smartcity.greenpassport.core.model.PointsRepository
import javax.inject.Inject

class GetLevelUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String): Level =
        LevelProgression.levelFor(pointsRepository.getExperience(userId))
}
