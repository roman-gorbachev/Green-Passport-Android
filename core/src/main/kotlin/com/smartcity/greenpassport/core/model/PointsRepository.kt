package com.smartcity.greenpassport.core.model

enum class PointsEarnReason {
    TASK_COMPLETED,
    GAME_PLAYED,
    ARTICLE_READ,
    EVENT_ATTENDED,
    FEEDBACK_SUBMITTED,
}

enum class PointsSpendReason {
    COUPON_REDEEMED,
}

data class PointsAward(
    val points: Int,
    val xp: Int,
    val reason: PointsEarnReason,
)

interface PointsRepository {
    suspend fun getBalance(userId: String): PointsBalance
    suspend fun getExperience(userId: String): Experience
    suspend fun award(userId: String, award: PointsAward): PointsBalance
    suspend fun spend(userId: String, points: Int, reason: PointsSpendReason): PointsBalance
}
