package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.TasksRepository
import javax.inject.Inject

class GetTaskProgressUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke(taskId: String, userId: String?): TaskProgress {
        val task = tasksRepository.getTasks().firstOrNull { it.id == taskId }
        val isCompleted = userId?.let { tasksRepository.getCompletedTaskIds(it) }?.contains(taskId) ?: false
        return TaskProgress(task = task, isCompleted = isCompleted)
    }
}
