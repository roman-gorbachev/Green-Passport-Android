package com.smartcity.greenpassport.core.model.rewards

data class RewardResult(
    val points: Int,
    val xp: Int,
    val streakBonus: Int = 0,
)
