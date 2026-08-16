package com.smartcity.greenpassport.core.model

data class Reward(
    val id: String,
    val title: String,
    val partnerName: String,
    val pointsCost: Int,
)

data class Coupon(
    val id: String,
    val rewardId: String,
    val redeemedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long?,
)

interface ShopRepository {
    suspend fun getRewards(): List<Reward>
    suspend fun getPurchases(userId: String): List<Coupon>
    suspend fun recordPurchase(userId: String, reward: Reward): Coupon
}
