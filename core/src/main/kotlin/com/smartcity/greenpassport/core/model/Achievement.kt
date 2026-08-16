package com.smartcity.greenpassport.core.model

enum class AchievementId {
    FIRST_TASK,
    TASK_MASTER,
    EVENT_GOER,
    ECO_READER,
    COMMUNITY_MEMBER,
    LEVEL_FIVE,
}

data class Achievement(
    val id: AchievementId,
    val isUnlocked: Boolean,
)

interface AchievementsRepository {
    suspend fun getAchievements(userId: String): List<Achievement>
}
