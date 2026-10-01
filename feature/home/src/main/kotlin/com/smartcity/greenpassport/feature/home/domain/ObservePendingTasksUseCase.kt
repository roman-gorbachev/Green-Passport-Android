package com.smartcity.greenpassport.feature.home.domain

import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TasksRepository
import com.smartcity.greenpassport.core.model.profile.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObservePendingTasksUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    operator fun invoke(userId: String, profile: UserProfile?): Flow<List<Task>> = combine(
        tasksRepository.observeTasks(),
        tasksRepository.observeCompletedTaskIds(userId),
    ) { tasks, completedIds ->
        tasks
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
