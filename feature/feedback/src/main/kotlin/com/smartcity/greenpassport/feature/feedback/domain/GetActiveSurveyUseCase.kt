package com.smartcity.greenpassport.feature.feedback.domain

import com.smartcity.greenpassport.core.model.FeedbackRepository
import com.smartcity.greenpassport.core.model.SurveyQuestion
import javax.inject.Inject

class GetActiveSurveyUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    suspend operator fun invoke(): SurveyQuestion? = feedbackRepository.getActiveSurvey()
}
