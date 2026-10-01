package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import javax.inject.Inject

class MarkCouponUsedUseCase @Inject constructor(
    private val rewardsRepository: RewardsRepository,
) {
    suspend operator fun invoke(couponId: String): Long = rewardsRepository.markCouponUsed(couponId)
}
