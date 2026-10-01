package com.smartcity.greenpassport.feature.shop.presentation.state

import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.CouponStatus
import com.smartcity.greenpassport.core.model.Reward
import kotlin.math.ceil

data class CouponItem(
    val coupon: Coupon,
    val reward: Reward?,
) {
    val title: String
        get() = reward?.title ?: coupon.rewardId

    fun daysLeft(nowEpochMillis: Long): Int? = coupon.expiresAtEpochMillis?.let { expiresAt ->
        ceil((expiresAt - nowEpochMillis).toDouble() / DAY_MILLIS).toInt().coerceAtLeast(0)
    }

    fun isExpiringSoon(nowEpochMillis: Long): Boolean {
        val daysLeft = daysLeft(nowEpochMillis) ?: return false
        return coupon.status(nowEpochMillis) == CouponStatus.ACTIVE && daysLeft <= EXPIRING_SOON_DAYS
    }

    companion object {
        private const val DAY_MILLIS = 24 * 60 * 60 * 1000.0
        private const val EXPIRING_SOON_DAYS = 3
    }
}
