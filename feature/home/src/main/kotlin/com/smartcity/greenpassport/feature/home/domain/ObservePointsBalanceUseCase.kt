package com.smartcity.greenpassport.feature.home.domain

import com.smartcity.greenpassport.core.model.PointsBalance
import com.smartcity.greenpassport.core.model.PointsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePointsBalanceUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    operator fun invoke(userId: String): Flow<PointsBalance> = pointsRepository.observeBalance(userId)
}
