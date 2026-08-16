package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.PointsSpendReason
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.ShopRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveShopSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}

class GetRewardsUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    suspend operator fun invoke(): List<Reward> = shopRepository.getRewards()
}

class GetPurchasesUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    suspend operator fun invoke(userId: String): List<Coupon> = shopRepository.getPurchases(userId)
}

class GetShopPointsBalanceUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String): Int = pointsRepository.getBalance(userId).availablePoints
}

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
