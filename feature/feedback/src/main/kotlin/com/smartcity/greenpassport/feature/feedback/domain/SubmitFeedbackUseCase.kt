package com.smartcity.greenpassport.feature.feedback.domain

import com.smartcity.greenpassport.core.model.FeedbackType
import com.smartcity.greenpassport.core.model.rewards.RewardResult
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.core.moderation.TextModerator
import javax.inject.Inject

class SubmitFeedbackUseCase @Inject constructor(
    private val rewardsRepository: RewardsRepository,
    private val textModerator: TextModerator,
) {
    suspend operator fun invoke(type: FeedbackType, message: String, rating: Int?): RewardResult {
        if (!textModerator.isAllowed(message)) throw ContentRejectedException()
        return rewardsRepository.submitFeedback(type = type.name, message = message.ifBlank { null }, rating = rating)
    }
}
