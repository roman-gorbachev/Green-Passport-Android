package com.smartcity.greenpassport.core.model

import kotlinx.coroutines.flow.Flow

enum class PointsEarnReason {
    TASK_COMPLETED,
    GAME_PLAYED,
    ARTICLE_READ,
    EVENT_ATTENDED,
    FEEDBACK_SUBMITTED,
}

interface PointsRepository {
    fun observeBalance(userId: String): Flow<PointsBalance>
    fun observeExperience(userId: String): Flow<Experience>
    fun observeStreak(userId: String): Flow<Streak?>
    suspend fun getBalance(userId: String): PointsBalance
    suspend fun getExperience(userId: String): Experience
}
