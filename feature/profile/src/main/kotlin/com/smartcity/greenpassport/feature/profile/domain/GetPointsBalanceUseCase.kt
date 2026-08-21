package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.PointsBalance
import com.smartcity.greenpassport.core.model.PointsRepository
import javax.inject.Inject

class GetPointsBalanceUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String): PointsBalance = pointsRepository.getBalance(userId)
}
