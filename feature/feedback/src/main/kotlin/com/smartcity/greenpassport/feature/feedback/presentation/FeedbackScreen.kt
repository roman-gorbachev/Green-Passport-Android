package com.smartcity.greenpassport.feature.feedback.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.SurveyQuestion
import com.smartcity.greenpassport.feature.feedback.R
import com.smartcity.greenpassport.core.R as CoreR

private const val MAX_RATING = 5
private const val SUPPORT_EMAIL = "support@greenpassport.app"
private const val SUPPORT_PHONE = "+375 (33) 555-01-01"

@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedbackViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.hasError) {
        ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::retry,
            modifier = modifier,
        )
        return
    }

    if (uiState.isLoading) {
        LoadingContent(modifier = modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
    ) {
        item {
            ReviewSection(
                rating = uiState.rating,
                message = uiState.reviewMessage,
                isSubmitting = uiState.isSubmittingReview,
                isSubmitted = uiState.reviewSubmitted,
                onRatingChanged = viewModel::onRatingChanged,
                onMessageChanged = viewModel::onReviewMessageChanged,
                onSubmit = viewModel::onSubmitReview,
            )
        }
        item {
            SuggestionSection(
                message = uiState.suggestionMessage,
                isSubmitting = uiState.isSubmittingSuggestion,
                isSubmitted = uiState.suggestionSubmitted,
                onMessageChanged = viewModel::onSuggestionMessageChanged,
                onSubmit = viewModel::onSubmitSuggestion,
            )
        }
        val survey = uiState.survey
        if (survey != null) {
            item {
                SurveySection(
                    survey = survey,
                    hasAnswered = uiState.hasAnsweredSurvey,
                    isSubmitting = uiState.isSubmittingSurveyAnswer,
                    onOptionSelected = viewModel::onSurveyOptionSelected,
                )
            }
        }
        item { SupportSection() }
    }
}

@Composable
private fun ReviewSection(
    rating: Int,
    message: String,
    isSubmitting: Boolean,
    isSubmitted: Boolean,
    onRatingChanged: (Int) -> Unit,
    onMessageChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Text(text = stringResource(R.string.feedback_review_title), style = MaterialTheme.typography.titleMedium)

            Row(modifier = Modifier.padding(top = Dimens.SpacingSmall)) {
                for (star in 1..MAX_RATING) {
                    IconButton(onClick = { onRatingChanged(star) }) {
                        Icon(
                            imageVector = if (star <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            OutlinedTextField(
                value = message,
                onValueChange = onMessageChanged,
                label = { Text(stringResource(R.string.feedback_review_message_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingSmall),
            )

            when {
                isSubmitted -> Text(
                    text = stringResource(R.string.feedback_review_submitted_label),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = Dimens.SpacingSmall),
                )

                isSubmitting -> CircularProgressIndicator(modifier = Modifier.padding(top = Dimens.SpacingSmall))

                else -> Button(
                    onClick = onSubmit,
                    modifier = Modifier.padding(top = Dimens.SpacingSmall),
                ) {
                    Text(stringResource(R.string.feedback_review_submit_button))
                }
            }
        }
    }
}

@Composable
private fun SuggestionSection(
    message: String,
    isSubmitting: Boolean,
    isSubmitted: Boolean,
    onMessageChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Text(
                text = stringResource(R.string.feedback_suggestion_title),
                style = MaterialTheme.typography.titleMedium,
            )

            OutlinedTextField(
                value = message,
                onValueChange = onMessageChanged,
                label = { Text(stringResource(R.string.feedback_suggestion_message_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingSmall),
            )

            if (isSubmitted) {
                Text(
                    text = stringResource(R.string.feedback_suggestion_submitted_label),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = Dimens.SpacingSmall),
                )
            }

            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.padding(top = Dimens.SpacingSmall))
            } else {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.padding(top = Dimens.SpacingSmall),
                ) {
                    Text(stringResource(R.string.feedback_suggestion_submit_button))
                }
            }
        }
    }
}

@Composable
private fun SurveySection(
    survey: SurveyQuestion,
    hasAnswered: Boolean,
    isSubmitting: Boolean,
    onOptionSelected: (Int) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Text(text = stringResource(R.string.feedback_survey_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = survey.question,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )

            when {
                hasAnswered -> Text(
                    text = stringResource(R.string.feedback_survey_answered_label),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = Dimens.SpacingSmall),
                )

                isSubmitting -> CircularProgressIndicator(modifier = Modifier.padding(top = Dimens.SpacingSmall))

                else -> Column(modifier = Modifier.padding(top = Dimens.SpacingSmall)) {
                    survey.options.forEachIndexed { index, option ->
                        OutlinedButton(
                            onClick = { onOptionSelected(index) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Dimens.SpacingExtraSmall),
                        ) {
                            Text(option)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SupportSection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Text(text = stringResource(R.string.feedback_support_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.feedback_support_email_format, SUPPORT_EMAIL),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
            Text(
                text = stringResource(R.string.feedback_support_phone_format, SUPPORT_PHONE),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            )
        }
    }
}
