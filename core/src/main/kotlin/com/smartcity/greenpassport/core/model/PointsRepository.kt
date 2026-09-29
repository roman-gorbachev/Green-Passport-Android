package com.smartcity.greenpassport.core.model

enum class PointsEarnReason {
    TASK_COMPLETED,
    GAME_PLAYED,
    ARTICLE_READ,
    EVENT_ATTENDED,
    FEEDBACK_SUBMITTED,
}

interface PointsRepository {
    suspend fun getBalance(userId: String): PointsBalance
    suspend fun getExperience(userId: String): Experience
}
