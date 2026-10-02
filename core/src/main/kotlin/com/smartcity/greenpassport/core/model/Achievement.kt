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
    val progress: Int,
    val target: Int,
) {
    val isUnlocked: Boolean get() = progress >= target
    val clampedProgress: Int get() = progress.coerceAtMost(target)
}

interface AchievementsRepository {
    suspend fun getAchievements(userId: String): List<Achievement>
}
