package com.smartcity.greenpassport.core.messaging

import com.smartcity.greenpassport.core.model.PointsEarnReason

interface RewardNotifier {
    suspend fun notifyReward(reason: PointsEarnReason, points: Int, xp: Int)
}
