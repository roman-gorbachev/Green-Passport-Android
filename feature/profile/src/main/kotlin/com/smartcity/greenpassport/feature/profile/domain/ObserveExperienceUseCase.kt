package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.Experience
import com.smartcity.greenpassport.core.model.PointsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveExperienceUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    operator fun invoke(userId: String): Flow<Experience> = pointsRepository.observeExperience(userId)
}
