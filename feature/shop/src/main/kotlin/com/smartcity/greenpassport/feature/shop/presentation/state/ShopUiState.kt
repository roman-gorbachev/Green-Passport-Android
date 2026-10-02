package com.smartcity.greenpassport.feature.shop.presentation.state

import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.CouponStatus
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.rewards.RewardFailure

data class ShopUiState(
    val points: Int = 0,
    val rewards: List<Reward> = emptyList(),
    val purchases: List<Coupon> = emptyList(),
    val purchasingRewardId: String? = null,
    val purchaseFailure: RewardFailure? = null,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val purchasedCouponId: String? = null,
) {
    val activeCouponCount: Int
        get() {
            val now = System.currentTimeMillis()
            return purchases.count { it.status(now) == CouponStatus.ACTIVE }
        }
}
