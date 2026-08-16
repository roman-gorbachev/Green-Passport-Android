package com.smartcity.greenpassport.feature.games.presentation.quiz

data class QuizUiState(
    val currentQuestionIndex: Int = 0,
    val correctAnswers: Int = 0,
    val score: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isFinished: Boolean = false,
)
