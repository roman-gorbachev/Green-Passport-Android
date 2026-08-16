package com.smartcity.greenpassport.feature.feedback.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.FeedbackEntry
import com.smartcity.greenpassport.core.model.FeedbackRepository
import com.smartcity.greenpassport.core.model.FeedbackType
import com.smartcity.greenpassport.core.model.SurveyQuestion
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveFeedbackSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}

class SubmitFeedbackUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    suspend operator fun invoke(userId: String, type: FeedbackType, message: String, rating: Int?) {
        feedbackRepository.submitFeedback(
            FeedbackEntry(userId = userId, type = type, message = message, rating = rating),
        )
    }
}

class GetActiveSurveyUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    suspend operator fun invoke(): SurveyQuestion? = feedbackRepository.getActiveSurvey()
}

class HasAnsweredSurveyUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    suspend operator fun invoke(userId: String, surveyId: String): Boolean =
        feedbackRepository.hasAnsweredSurvey(userId, surveyId)
}

class SubmitSurveyAnswerUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    suspend operator fun invoke(userId: String, surveyId: String, optionIndex: Int) =
        feedbackRepository.submitSurveyAnswer(userId, surveyId, optionIndex)
}
