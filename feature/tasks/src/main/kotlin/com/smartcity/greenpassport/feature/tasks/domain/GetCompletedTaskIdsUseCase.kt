package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.TasksRepository
import javax.inject.Inject

class GetCompletedTaskIdsUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke(userId: String): Set<String> = tasksRepository.getCompletedTaskIds(userId)
}
