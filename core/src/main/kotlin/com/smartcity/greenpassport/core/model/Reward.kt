package com.smartcity.greenpassport.core.model

import kotlinx.coroutines.flow.Flow

data class Reward(
    val id: String,
    val title: LocalizedText,
    val partnerName: LocalizedText,
    val pointsCost: Int,
)

data class Coupon(
    val id: String,
    val rewardId: String,
    val redeemedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long?,
    val code: String? = null,
    val usedAtEpochMillis: Long? = null,
) {
    fun status(nowEpochMillis: Long): CouponStatus = when {
        usedAtEpochMillis != null -> CouponStatus.USED
        expiresAtEpochMillis != null && expiresAtEpochMillis < nowEpochMillis -> CouponStatus.EXPIRED
        else -> CouponStatus.ACTIVE
    }
}

interface ShopRepository {
    fun observeRewards(): Flow<List<Reward>>
    fun observePurchases(userId: String): Flow<List<Coupon>>
    fun observePurchase(couponId: String): Flow<Coupon?>
    suspend fun getRewards(): List<Reward>
    suspend fun getPurchases(userId: String): List<Coupon>
}
