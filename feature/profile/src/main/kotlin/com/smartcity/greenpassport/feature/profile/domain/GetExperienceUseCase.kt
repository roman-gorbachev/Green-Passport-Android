package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.Experience
import com.smartcity.greenpassport.core.model.PointsRepository
import javax.inject.Inject

class GetExperienceUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String): Experience = pointsRepository.getExperience(userId)
}
