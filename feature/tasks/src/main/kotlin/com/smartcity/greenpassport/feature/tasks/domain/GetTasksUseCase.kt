package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TasksRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke(): List<Task> = tasksRepository.getTasks()
}
