package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.rewards.RewardResult
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    private val rewardsRepository: RewardsRepository,
) {
    suspend operator fun invoke(task: Task): RewardResult = rewardsRepository.completeSelfTask(task.id)
}
