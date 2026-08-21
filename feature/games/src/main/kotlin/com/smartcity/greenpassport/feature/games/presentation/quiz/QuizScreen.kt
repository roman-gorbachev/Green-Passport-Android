package com.smartcity.greenpassport.feature.games.presentation.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.games.R

@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.SpacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (uiState.isFinished) {
            Text(
                text = stringResource(
                    R.string.quiz_finished_format,
                    uiState.correctAnswers,
                    quizQuestions.size,
                ),
                style = MaterialTheme.typography.titleLarge,
            )
            Button(
                onClick = viewModel::onRestart,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            ) {
                Text(stringResource(R.string.game_play_again))
            }
            return@Column
        }

        val question = quizQuestions[uiState.currentQuestionIndex]
        val options = stringArrayResource(question.optionsArrayRes)

        Text(
            text = stringResource(
                R.string.quiz_score_format,
                uiState.currentQuestionIndex + 1,
                quizQuestions.size,
                uiState.score,
            ),
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = stringResource(question.questionRes),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = Dimens.SpacingLarge),
        )

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)) {
            options.forEachIndexed { index, option ->
                val isSelected = uiState.selectedOptionIndex == index
                val isCorrect = index == question.correctOptionIndex
                val showFeedback = uiState.selectedOptionIndex != null

                val containerColor: Color
                val contentColor: Color
                when {
                    !showFeedback -> {
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    isCorrect -> {
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    }

                    isSelected -> {
                        containerColor = MaterialTheme.colorScheme.errorContainer
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    }

                    else -> {
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    }
                }

                Button(
                    onClick = { viewModel.onOptionSelected(index) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                    ),
                ) {
                    Text(option)
                }
            }
        }
    }
}
