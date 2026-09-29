package com.smartcity.greenpassport.feature.tasks.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ConfirmHapticOnSuccess
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.GpSheetScaffold
import com.smartcity.greenpassport.core.designsystem.component.MascotWidget
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.feature.tasks.R
import com.smartcity.greenpassport.feature.tasks.presentation.viewmodels.TaskDetailViewModel
import com.smartcity.greenpassport.core.R as CoreR

private const val SHEET_MIN_HEIGHT_FRACTION = 0.55f

@Composable
fun TaskDetailSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val task = uiState.task

    ConfirmHapticOnSuccess(inProgress = uiState.isSubmitting, succeeded = uiState.isCompleted)

    GpSheetScaffold(onDismiss = onDismiss, modifier = modifier) {
        when {
            uiState.hasError -> ErrorContent(
                message = stringResource(CoreR.string.error_generic_message),
                retryLabel = stringResource(CoreR.string.retry_button),
                onRetry = viewModel::retry,
                modifier = Modifier.fillMaxWidth(),
            )

            uiState.isLoading || task == null -> SheetLoading()

            else -> TaskDetailContent(
                task = task,
                isCompleted = uiState.isCompleted,
                isSubmitting = uiState.isSubmitting,
                onCompleteTask = viewModel::onCompleteTask,
            )
        }
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    isCompleted: Boolean,
    isSubmitting: Boolean,
    onCompleteTask: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val minHeight = LocalConfiguration.current.screenHeightDp.dp * SHEET_MIN_HEIGHT_FRACTION

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MascotWidget(size = Dimens.SheetMascotSize)
                Column(modifier = Modifier.padding(start = Dimens.SpacingMedium)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(CoreR.string.points_reward, task.rewardPoints),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = Dimens.SpacingLarge),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.SpacingExtraLarge),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isCompleted -> Text(
                    text = stringResource(R.string.task_detail_completed_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                isSubmitting -> CircularProgressIndicator()

                else -> GpPrimaryButton(
                    text = stringResource(R.string.mark_as_done),
                    onClick = onCompleteTask,
                )
            }
        }
    }
}

@Composable
private fun SheetLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.HeroCardHeight),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
