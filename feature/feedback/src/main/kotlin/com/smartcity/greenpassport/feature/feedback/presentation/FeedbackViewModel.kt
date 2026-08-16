package com.smartcity.greenpassport.feature.feedback.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.FeedbackType
import com.smartcity.greenpassport.feature.feedback.domain.GetActiveSurveyUseCase
import com.smartcity.greenpassport.feature.feedback.domain.HasAnsweredSurveyUseCase
import com.smartcity.greenpassport.feature.feedback.domain.ObserveFeedbackSessionUseCase
import com.smartcity.greenpassport.feature.feedback.domain.SubmitFeedbackUseCase
import com.smartcity.greenpassport.feature.feedback.domain.SubmitSurveyAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val submitFeedback: SubmitFeedbackUseCase,
    private val getActiveSurvey: GetActiveSurveyUseCase,
    private val hasAnsweredSurvey: HasAnsweredSurveyUseCase,
    private val submitSurveyAnswer: SubmitSurveyAnswerUseCase,
    observeSession: ObserveFeedbackSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                refresh()
            }
        }
    }

    fun retry() {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            try {
                val survey = getActiveSurvey()
                val userId = currentUserId
                val answered = if (survey != null && userId != null) hasAnsweredSurvey(userId, survey.id) else false
                _uiState.update { it.copy(survey = survey, hasAnsweredSurvey = answered, isLoading = false) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun onRatingChanged(rating: Int) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun onReviewMessageChanged(text: String) {
        _uiState.update { it.copy(reviewMessage = text) }
    }

    fun onSubmitReview() {
        val userId = currentUserId ?: return
        val state = _uiState.value
        if (state.rating == 0 || state.isSubmittingReview) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingReview = true) }
            try {
                submitFeedback(userId, FeedbackType.REVIEW, state.reviewMessage.trim(), state.rating)
                _uiState.update { it.copy(isSubmittingReview = false, reviewSubmitted = true) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isSubmittingReview = false) }
            }
        }
    }

    fun onSuggestionMessageChanged(text: String) {
        _uiState.update { it.copy(suggestionMessage = text) }
    }

    fun onSubmitSuggestion() {
        val userId = currentUserId ?: return
        val state = _uiState.value
        val text = state.suggestionMessage.trim()
        if (text.isEmpty() || state.isSubmittingSuggestion) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingSuggestion = true) }
            try {
                submitFeedback(userId, FeedbackType.SUGGESTION, text, null)
                _uiState.update {
                    it.copy(isSubmittingSuggestion = false, suggestionSubmitted = true, suggestionMessage = "")
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isSubmittingSuggestion = false) }
            }
        }
    }

    fun onSurveyOptionSelected(optionIndex: Int) {
        val userId = currentUserId ?: return
        val survey = _uiState.value.survey ?: return
        if (_uiState.value.hasAnsweredSurvey || _uiState.value.isSubmittingSurveyAnswer) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingSurveyAnswer = true) }
            try {
                submitSurveyAnswer(userId, survey.id, optionIndex)
                _uiState.update { it.copy(isSubmittingSurveyAnswer = false, hasAnsweredSurvey = true) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isSubmittingSurveyAnswer = false) }
            }
        }
    }
}
