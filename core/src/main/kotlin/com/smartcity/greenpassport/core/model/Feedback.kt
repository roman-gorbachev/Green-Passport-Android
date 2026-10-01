package com.smartcity.greenpassport.core.model

enum class FeedbackType {
    REVIEW,
    SUGGESTION,
}

data class SurveyQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
)

interface FeedbackRepository {
    suspend fun getActiveSurvey(): SurveyQuestion?
    suspend fun hasAnsweredSurvey(userId: String, surveyId: String): Boolean
}
