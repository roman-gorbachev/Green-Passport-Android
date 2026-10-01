package com.smartcity.greenpassport.core.model

import kotlinx.coroutines.flow.Flow

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
    fun observeRewards(): Flow<List<Reward>>
    fun observePurchases(userId: String): Flow<List<Coupon>>
    suspend fun getRewards(): List<Reward>
    suspend fun getPurchases(userId: String): List<Coupon>
}
