package com.smartcity.greenpassport.core.datasource.remote.repository

import com.smartcity.greenpassport.core.model.Achievement
import com.smartcity.greenpassport.core.model.AchievementId
import com.smartcity.greenpassport.core.model.AchievementsRepository
import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import com.smartcity.greenpassport.core.model.EventsRepository
import com.smartcity.greenpassport.core.model.LevelProgression
import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.TasksRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val FIRST_TASK_THRESHOLD = 1
private const val TASK_MASTER_THRESHOLD = 5
private const val EVENT_GOER_THRESHOLD = 1
private const val ECO_READER_THRESHOLD = 3
private const val COMMUNITY_MEMBER_THRESHOLD = 1
private const val LEVEL_FIVE_THRESHOLD = 5

class AchievementsRepositoryImpl @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val eventsRepository: EventsRepository,
    private val ecoTipsRepository: EcoTipsRepository,
    private val communityRepository: CommunityRepository,
    private val pointsRepository: PointsRepository,
) : AchievementsRepository {

    override suspend fun getAchievements(userId: String): List<Achievement> {
        val completedTaskCount = tasksRepository.getCompletedTaskIds(userId).size
        val registeredEventCount = eventsRepository.getRegisteredEventIds(userId).size
        val readTipCount = ecoTipsRepository.getReadTipIds(userId).size
        val isCommunityMember = communityRepository.observeGroups().first()
            .any { it.memberIds.contains(userId) }
        val level = LevelProgression.levelFor(pointsRepository.getExperience(userId)).number

        return listOf(
            Achievement(AchievementId.FIRST_TASK, completedTaskCount, FIRST_TASK_THRESHOLD),
            Achievement(AchievementId.TASK_MASTER, completedTaskCount, TASK_MASTER_THRESHOLD),
            Achievement(AchievementId.EVENT_GOER, registeredEventCount, EVENT_GOER_THRESHOLD),
            Achievement(AchievementId.ECO_READER, readTipCount, ECO_READER_THRESHOLD),
            Achievement(AchievementId.COMMUNITY_MEMBER, if (isCommunityMember) 1 else 0, COMMUNITY_MEMBER_THRESHOLD),
            Achievement(AchievementId.LEVEL_FIVE, level, LEVEL_FIVE_THRESHOLD),
        )
    }
}
