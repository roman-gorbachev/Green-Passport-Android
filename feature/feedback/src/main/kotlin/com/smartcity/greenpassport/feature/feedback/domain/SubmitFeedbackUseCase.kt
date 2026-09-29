package com.smartcity.greenpassport.feature.feedback.domain

import com.smartcity.greenpassport.core.model.FeedbackEntry
import com.smartcity.greenpassport.core.model.FeedbackRepository
import com.smartcity.greenpassport.core.model.FeedbackType
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.core.moderation.TextModerator
import javax.inject.Inject

class SubmitFeedbackUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
    private val textModerator: TextModerator,
) {
    suspend operator fun invoke(userId: String, type: FeedbackType, message: String, rating: Int?) {
        if (!textModerator.isAllowed(message)) throw ContentRejectedException()
        feedbackRepository.submitFeedback(
            FeedbackEntry(userId = userId, type = type, message = message, rating = rating),
        )
    }
}
