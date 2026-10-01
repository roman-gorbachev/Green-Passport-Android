package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTasksUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    operator fun invoke(): Flow<List<Task>> = tasksRepository.observeTasks()
}
