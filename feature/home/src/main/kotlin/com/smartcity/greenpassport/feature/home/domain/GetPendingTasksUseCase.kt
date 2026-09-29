package com.smartcity.greenpassport.feature.home.domain

import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TasksRepository
import com.smartcity.greenpassport.core.model.profile.UserProfile
import javax.inject.Inject

class GetPendingTasksUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke(userId: String, profile: UserProfile?): List<Task> {
        val completedIds = tasksRepository.getCompletedTaskIds(userId)
        return tasksRepository.getTasks()
            .filterNot { it.id in completedIds }
            .sortedWith(
                compareByDescending<Task> { it.city == profile?.city }
                    .thenByDescending { profile != null && it.category in profile.interests },
            )
            .take(HOME_TASKS_LIMIT)
    }

    companion object {
        private const val HOME_TASKS_LIMIT = 3
    }
}
