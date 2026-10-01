package com.smartcity.greenpassport.feature.home.domain

import com.smartcity.greenpassport.core.model.Level
import com.smartcity.greenpassport.core.model.LevelProgression
import com.smartcity.greenpassport.core.model.PointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveLevelUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    operator fun invoke(userId: String): Flow<Level> =
        pointsRepository.observeExperience(userId).map(LevelProgression::levelFor)
}
