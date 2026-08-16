package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.PointsAward
import com.smartcity.greenpassport.core.model.PointsEarnReason
import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TasksRepository
import javax.inject.Inject

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
