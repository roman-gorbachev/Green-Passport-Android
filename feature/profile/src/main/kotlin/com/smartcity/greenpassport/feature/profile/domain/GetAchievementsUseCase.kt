package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.Achievement
import com.smartcity.greenpassport.core.model.AchievementsRepository
import javax.inject.Inject

class GetAchievementsUseCase @Inject constructor(
    private val achievementsRepository: AchievementsRepository,
) {
    suspend operator fun invoke(userId: String): List<Achievement> = achievementsRepository.getAchievements(userId)
}
