package com.smartcity.greenpassport.core.model

enum class FeedbackType {
    REVIEW,
    SUGGESTION,
}

data class SurveyQuestion(
    val id: String,
    val question: LocalizedText,
    val options: LocalizedTextList,
)

interface FeedbackRepository {
    suspend fun getActiveSurvey(): SurveyQuestion?
    suspend fun hasAnsweredSurvey(userId: String, surveyId: String): Boolean
}
