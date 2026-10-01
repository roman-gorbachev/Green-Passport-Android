package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.PointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveShopPointsBalanceUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    operator fun invoke(userId: String): Flow<Int> = pointsRepository.observeBalance(userId).map { it.availablePoints }
}
