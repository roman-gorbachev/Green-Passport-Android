package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.LevelProgression
import com.smartcity.greenpassport.core.model.PointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveProfileProgressUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    operator fun invoke(userId: String): Flow<ProfileProgress> = combine(
        pointsRepository.observeBalance(userId),
        pointsRepository.observeExperience(userId),
    ) { balance, experience ->
        ProfileProgress(points = balance.availablePoints, level = LevelProgression.levelFor(experience))
    }
}
