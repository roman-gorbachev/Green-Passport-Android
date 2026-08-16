package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.Achievement
import com.smartcity.greenpassport.core.model.AchievementId
import com.smartcity.greenpassport.core.model.AchievementsRepository
import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import com.smartcity.greenpassport.core.model.EventsRepository
import com.smartcity.greenpassport.core.model.LevelProgression
import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.TasksRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

private const val TASK_MASTER_THRESHOLD = 5
private const val ECO_READER_THRESHOLD = 3
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
        val hasAttendedEvent = eventsRepository.getRegisteredEventIds(userId).isNotEmpty()
        val readTipCount = ecoTipsRepository.getReadTipIds(userId).size
        val isCommunityMember = communityRepository.observeGroups().first()
            .any { it.memberIds.contains(userId) }
        val level = LevelProgression.levelFor(pointsRepository.getExperience(userId)).number

        return listOf(
            Achievement(AchievementId.FIRST_TASK, completedTaskCount >= 1),
            Achievement(AchievementId.TASK_MASTER, completedTaskCount >= TASK_MASTER_THRESHOLD),
            Achievement(AchievementId.EVENT_GOER, hasAttendedEvent),
            Achievement(AchievementId.ECO_READER, readTipCount >= ECO_READER_THRESHOLD),
            Achievement(AchievementId.COMMUNITY_MEMBER, isCommunityMember),
            Achievement(AchievementId.LEVEL_FIVE, level >= LEVEL_FIVE_THRESHOLD),
        )
    }
}
