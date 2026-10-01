package com.smartcity.greenpassport.feature.home.domain

import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.Streak
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveStreakUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    operator fun invoke(userId: String): Flow<Streak?> = pointsRepository.observeStreak(userId)
}
