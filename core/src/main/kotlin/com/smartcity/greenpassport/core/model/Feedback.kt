package com.smartcity.greenpassport.core.model

enum class FeedbackType {
    REVIEW,
    SUGGESTION,
}

data class FeedbackEntry(
    val userId: String,
    val type: FeedbackType,
    val message: String,
    val rating: Int?,
)

data class SurveyQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
)

interface FeedbackRepository {
    suspend fun submitFeedback(entry: FeedbackEntry)
    suspend fun getActiveSurvey(): SurveyQuestion?
    suspend fun hasAnsweredSurvey(userId: String, surveyId: String): Boolean
    suspend fun submitSurveyAnswer(userId: String, surveyId: String, optionIndex: Int)
}
