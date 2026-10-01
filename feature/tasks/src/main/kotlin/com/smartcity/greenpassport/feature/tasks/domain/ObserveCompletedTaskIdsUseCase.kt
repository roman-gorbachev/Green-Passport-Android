package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.TasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCompletedTaskIdsUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    operator fun invoke(userId: String): Flow<Set<String>> = tasksRepository.observeCompletedTaskIds(userId)
}
