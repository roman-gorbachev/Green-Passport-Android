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
