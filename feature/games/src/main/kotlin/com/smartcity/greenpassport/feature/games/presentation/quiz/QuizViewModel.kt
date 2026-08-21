package com.smartcity.greenpassport.feature.games.presentation.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.games.domain.GameId
import com.smartcity.greenpassport.feature.games.domain.ObserveGamesSessionUseCase
import com.smartcity.greenpassport.feature.games.domain.SubmitGameResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val POINTS_PER_CORRECT_ANSWER = 20
private const val ANSWER_FEEDBACK_DELAY_MILLIS = 600L

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val submitGameResult: SubmitGameResultUseCase,
    private val observeSession: ObserveGamesSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun onOptionSelected(optionIndex: Int) {
        val state = _uiState.value
        if (state.isFinished || state.selectedOptionIndex != null) return

        val question = quizQuestions[state.currentQuestionIndex]
        val isCorrect = optionIndex == question.correctOptionIndex

        _uiState.update {
            val correctAnswers = if (isCorrect) it.correctAnswers + 1 else it.correctAnswers
            it.copy(
                selectedOptionIndex = optionIndex,
                correctAnswers = correctAnswers,
                score = correctAnswers * POINTS_PER_CORRECT_ANSWER,
            )
        }

        viewModelScope.launch {
            delay(ANSWER_FEEDBACK_DELAY_MILLIS.milliseconds)
            advance()
        }
    }

    private suspend fun advance() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex >= quizQuestions.size) {
            _uiState.update { it.copy(isFinished = true, selectedOptionIndex = null) }
            val userId = observeSession().first()?.userId ?: return
            runCatching {
                submitGameResult(userId, GameId.ECO_QUIZ, _uiState.value.score)
            }.onFailure { error ->
                Log.e("QuizViewModel::advance()", error.message.orEmpty())
            }
        } else {
            _uiState.update { it.copy(currentQuestionIndex = nextIndex, selectedOptionIndex = null) }
        }
    }

    fun onRestart() {
        _uiState.value = QuizUiState()
    }
}
