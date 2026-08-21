package com.smartcity.greenpassport.feature.feedback.domain

import com.smartcity.greenpassport.core.model.FeedbackRepository
import javax.inject.Inject

class HasAnsweredSurveyUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    suspend operator fun invoke(userId: String, surveyId: String): Boolean =
        feedbackRepository.hasAnsweredSurvey(userId, surveyId)
}
