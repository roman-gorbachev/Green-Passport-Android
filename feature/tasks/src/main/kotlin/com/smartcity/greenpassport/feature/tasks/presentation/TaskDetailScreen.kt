package com.smartcity.greenpassport.feature.tasks.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.feature.tasks.R
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun TaskDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel(),
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

    if (uiState.isLoading || uiState.task == null) {
        LoadingContent(modifier = modifier)
        return
    }

    TaskDetailContent(
        task = uiState.task!!,
        isCompleted = uiState.isCompleted,
        isSubmitting = uiState.isSubmitting,
        onCompleteTask = viewModel::onCompleteTask,
        modifier = modifier,
    )
}

@Composable
private fun TaskDetailContent(
    task: Task,
    isCompleted: Boolean,
    isSubmitting: Boolean,
    onCompleteTask: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.SpacingLarge),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(text = task.title, style = MaterialTheme.typography.headlineMedium)

        Text(
            text = task.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = Dimens.SpacingMedium),
        )

        Text(
            text = stringResource(R.string.task_detail_reward_format, task.rewardPoints, task.rewardXp),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = Dimens.SpacingLarge),
        )

        when {
            isCompleted -> Text(
                text = stringResource(R.string.task_detail_completed_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = Dimens.SpacingLarge),
            )

            isSubmitting -> CircularProgressIndicator(modifier = Modifier.padding(top = Dimens.SpacingLarge))

            else -> Button(
                onClick = onCompleteTask,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingLarge),
            ) {
                Text(stringResource(R.string.task_detail_complete_button))
            }
        }
    }
}
