package com.smartcity.greenpassport.feature.feedback.domain

import com.smartcity.greenpassport.core.model.FeedbackEntry
import com.smartcity.greenpassport.core.model.FeedbackRepository
import com.smartcity.greenpassport.core.model.FeedbackType
import javax.inject.Inject

class SubmitFeedbackUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    suspend operator fun invoke(userId: String, type: FeedbackType, message: String, rating: Int?) {
        feedbackRepository.submitFeedback(
            FeedbackEntry(userId = userId, type = type, message = message, rating = rating),
        )
    }
}
