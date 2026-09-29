package com.smartcity.greenpassport.feature.moderation.domain

import com.smartcity.greenpassport.core.model.TasksRepository
import com.smartcity.greenpassport.core.model.moderation.ModerationRepository
import com.smartcity.greenpassport.core.model.verification.TaskSubmissionsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveModerationQueueUseCase @Inject constructor(
    private val moderationRepository: ModerationRepository,
    private val taskSubmissionsRepository: TaskSubmissionsRepository,
    private val tasksRepository: TasksRepository,
) {
    operator fun invoke(): Flow<List<SubmissionItem>> =
        moderationRepository.observePendingSubmissions().mapLatest { submissions ->
            val taskTitles = runCatching { tasksRepository.getTasks() }
                .getOrDefault(emptyList())
                .associate { it.id to it.title }
            submissions.map { submission ->
                SubmissionItem(
                    submission = submission,
                    taskTitle = taskTitles[submission.taskId],
                    photoUrl = runCatching { taskSubmissionsRepository.getPhotoUrl(submission.photoPath) }.getOrNull(),
                )
            }
        }
}
