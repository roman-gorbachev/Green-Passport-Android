package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.PointsSpendReason
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.ShopRepository
import javax.inject.Inject

class PurchaseRewardUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String, reward: Reward): Coupon {
        pointsRepository.spend(
            userId = userId,
            points = reward.pointsCost,
            reason = PointsSpendReason.COUPON_REDEEMED,
        )
        return shopRepository.recordPurchase(userId, reward)
    }
}
