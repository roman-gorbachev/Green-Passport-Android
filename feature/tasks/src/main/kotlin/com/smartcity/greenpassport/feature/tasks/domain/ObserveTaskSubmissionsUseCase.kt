package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.verification.TaskSubmission
import com.smartcity.greenpassport.core.model.verification.TaskSubmissionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTaskSubmissionsUseCase @Inject constructor(
    private val taskSubmissionsRepository: TaskSubmissionsRepository,
) {
    operator fun invoke(userId: String): Flow<List<TaskSubmission>> =
        taskSubmissionsRepository.observeUserSubmissions(userId)
}
