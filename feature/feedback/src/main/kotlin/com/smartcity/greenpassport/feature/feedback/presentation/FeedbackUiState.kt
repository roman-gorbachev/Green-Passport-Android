package com.smartcity.greenpassport.feature.feedback.presentation

import com.smartcity.greenpassport.core.model.SurveyQuestion

data class FeedbackUiState(
    val rating: Int = 0,
    val reviewMessage: String = "",
    val isSubmittingReview: Boolean = false,
    val reviewSubmitted: Boolean = false,
    val suggestionMessage: String = "",
    val isSubmittingSuggestion: Boolean = false,
    val suggestionSubmitted: Boolean = false,
    val survey: SurveyQuestion? = null,
    val hasAnsweredSurvey: Boolean = false,
    val isSubmittingSurveyAnswer: Boolean = false,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
