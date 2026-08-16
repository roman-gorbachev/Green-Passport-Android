package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.FavoritesRepository
import com.smartcity.greenpassport.core.model.PointsAward
import com.smartcity.greenpassport.core.model.PointsEarnReason
import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TasksRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke(): List<Task> = tasksRepository.getTasks()
}

class GetCompletedTaskIdsUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke(userId: String): Set<String> = tasksRepository.getCompletedTaskIds(userId)
}

class CompleteTaskUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String, task: Task) {
        tasksRepository.markTaskCompleted(userId, task.id)
        pointsRepository.award(
            userId = userId,
            award = PointsAward(
                points = task.rewardPoints,
                xp = task.rewardXp,
                reason = PointsEarnReason.TASK_COMPLETED,
            ),
        )
    }
}

class ObserveFavoriteTaskIdsUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(userId: String): Flow<Set<String>> = favoritesRepository.observeFavoriteTaskIds(userId)
}

class ToggleTaskFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(userId: String, taskId: String, isFavorite: Boolean) =
        favoritesRepository.setTaskFavorite(userId, taskId, isFavorite)
}
