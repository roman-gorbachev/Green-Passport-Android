package com.smartcity.greenpassport.feature.games.presentation.quiz

import com.smartcity.greenpassport.feature.games.R

data class QuizQuestion(
    val questionRes: Int,
    val optionsArrayRes: Int,
    val correctOptionIndex: Int,
)

val quizQuestions = listOf(
    QuizQuestion(R.string.quiz_question_1, R.array.quiz_options_1, correctOptionIndex = 2),
    QuizQuestion(R.string.quiz_question_2, R.array.quiz_options_2, correctOptionIndex = 1),
    QuizQuestion(R.string.quiz_question_3, R.array.quiz_options_3, correctOptionIndex = 1),
    QuizQuestion(R.string.quiz_question_4, R.array.quiz_options_4, correctOptionIndex = 2),
    QuizQuestion(R.string.quiz_question_5, R.array.quiz_options_5, correctOptionIndex = 1),
)
