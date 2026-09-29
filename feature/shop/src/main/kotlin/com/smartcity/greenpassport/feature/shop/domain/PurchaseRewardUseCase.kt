package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import javax.inject.Inject

class PurchaseRewardUseCase @Inject constructor(
    private val rewardsRepository: RewardsRepository,
) {
    suspend operator fun invoke(reward: Reward): Coupon = rewardsRepository.redeemReward(reward.id)
}
