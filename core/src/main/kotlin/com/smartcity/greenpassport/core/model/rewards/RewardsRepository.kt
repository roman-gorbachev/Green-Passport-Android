package com.smartcity.greenpassport.core.model.rewards

import com.smartcity.greenpassport.core.model.Coupon

interface RewardsRepository {
    suspend fun completeSelfTask(taskId: String): RewardResult
    suspend fun redeemTaskCode(code: String): RewardResult
    suspend fun recordTipRead(tipId: String): RewardResult
    suspend fun recordGameResult(gameId: String, score: Int): RewardResult
    suspend fun redeemReward(rewardId: String): Coupon
}
