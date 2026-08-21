package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.PointsRepository
import javax.inject.Inject

class GetShopPointsBalanceUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String): Int = pointsRepository.getBalance(userId).availablePoints
}
