package com.smartcity.greenpassport.feature.feedback.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.GpTextField
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.layout.plus
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
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: FeedbackViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.hasError) {
        ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::retry,
            modifier = modifier.padding(contentPadding),
        )
        return
    }

    if (uiState.isLoading) {
        LoadingContent(modifier = modifier.padding(contentPadding))
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentPadding = contentPadding + PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingMedium,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
    ) {
        item {
            ReviewSection(
                uiState = uiState,
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
                isRejected = uiState.isSuggestionRejected,
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
    uiState: FeedbackUiState,
    onRatingChanged: (Int) -> Unit,
    onMessageChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val rating = uiState.rating
    val message = uiState.reviewMessage
    val isSubmitting = uiState.isSubmittingReview
    val isSubmitted = uiState.reviewSubmitted
    val isRejected = uiState.isReviewRejected
    GpSurfaceCard(modifier = Modifier.fillMaxWidth()) {
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

            GpTextField(
                value = message,
                onValueChange = onMessageChanged,
                label = { Text(stringResource(R.string.feedback_review_message_label)) },
                isError = isRejected,
                supportingText = rejectedText(isRejected),
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
    isRejected: Boolean,
    onMessageChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    GpSurfaceCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Text(
                text = stringResource(R.string.feedback_suggestion_title),
                style = MaterialTheme.typography.titleMedium,
            )

            GpTextField(
                value = message,
                onValueChange = onMessageChanged,
                label = { Text(stringResource(R.string.feedback_suggestion_message_label)) },
                isError = isRejected,
                supportingText = rejectedText(isRejected),
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
    GpSurfaceCard(modifier = Modifier.fillMaxWidth()) {
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
    val uriHandler = LocalUriHandler.current
    GpSurfaceCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Text(text = stringResource(R.string.feedback_support_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.feedback_support_email_format, SUPPORT_EMAIL),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = Dimens.SpacingSmall)
                    .clickable(role = Role.Button) { runCatching { uriHandler.openUri("mailto:$SUPPORT_EMAIL") } },
            )
            Text(
                text = stringResource(R.string.feedback_support_phone_format, SUPPORT_PHONE),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = Dimens.SpacingExtraSmall)
                    .clickable(role = Role.Button) {
                        runCatching { uriHandler.openUri("tel:${SUPPORT_PHONE.filter { it.isDigit() || it == '+' }}") }
                    },
            )
        }
    }
}

private fun rejectedText(isRejected: Boolean): (@Composable () -> Unit)? =
    if (isRejected) {
        { Text(stringResource(R.string.text_contains_banned_words)) }
    } else {
        null
    }
