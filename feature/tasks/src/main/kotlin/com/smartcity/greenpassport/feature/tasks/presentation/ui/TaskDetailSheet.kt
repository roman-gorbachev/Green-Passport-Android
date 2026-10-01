package com.smartcity.greenpassport.feature.tasks.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.GpSheetScaffold
import com.smartcity.greenpassport.core.designsystem.component.MascotWidget
import com.smartcity.greenpassport.core.designsystem.component.PointsChip
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.verification.SubmissionStatus
import com.smartcity.greenpassport.core.model.verification.TaskVerification
import com.smartcity.greenpassport.feature.tasks.R
import com.smartcity.greenpassport.feature.tasks.presentation.state.TaskDetailUiState
import com.smartcity.greenpassport.feature.tasks.presentation.state.confirmButtonRes
import com.smartcity.greenpassport.feature.tasks.presentation.state.rewardFailureMessageRes
import com.smartcity.greenpassport.feature.tasks.presentation.state.verificationHintRes
import com.smartcity.greenpassport.feature.tasks.presentation.state.verificationLabelRes
import com.smartcity.greenpassport.feature.tasks.presentation.viewmodels.TaskDetailViewModel
import com.smartcity.greenpassport.core.R as CoreR

private const val SHEET_MIN_HEIGHT_FRACTION = 0.55f
private const val UNSAFE_PHOTO_REASON = "unsafe_photo"
private const val INVALID_PHOTO_REASON = "invalid_photo"

@Composable
fun TaskDetailSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val task = uiState.task
    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.onPhotoPicked(uri)
    }
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        val uri = cameraUri
        if (isSaved && uri != null) viewModel.onPhotoPicked(uri)
    }
    var isPhotoSourceVisible by remember { mutableStateOf(false) }
    if (isPhotoSourceVisible) {
        PhotoSourceDialog(
            onTakePhoto = {
                isPhotoSourceVisible = false
                val uri = createTaskPhotoUri(context)
                cameraUri = uri
                camera.launch(uri)
            },
            onChooseFromLibrary = {
                isPhotoSourceVisible = false
                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onDismiss = { isPhotoSourceVisible = false },
        )
    }

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
                uiState = uiState,
                onCompleteTask = viewModel::onCompleteTask,
                onScanCode = { viewModel.onScanCode(context) },
                onPickPhoto = { isPhotoSourceVisible = true },
            )
        }
    }
}

@Composable
private fun PhotoSourceDialog(
    onTakePhoto: () -> Unit,
    onChooseFromLibrary: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.attach_photo)) },
        text = {
            Column {
                TextButton(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.take_photo))
                }
                TextButton(onClick = onChooseFromLibrary, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.choose_from_library))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(CoreR.string.cancel))
            }
        },
    )
}

@Composable
private fun TaskDetailContent(
    task: Task,
    uiState: TaskDetailUiState,
    onCompleteTask: () -> Unit,
    onScanCode: () -> Unit,
    onPickPhoto: () -> Unit,
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
                    ) {
                        PointsChip(points = task.rewardPoints)
                        Text(
                            text = stringResource(verificationLabelRes(task.verification)),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = Dimens.SpacingSmall),
                        )
                    }
                }
            }

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = Dimens.SpacingLarge),
            )
        }

        ConfirmationSection(
            task = task,
            uiState = uiState,
            onCompleteTask = onCompleteTask,
            onScanCode = onScanCode,
            onPickPhoto = onPickPhoto,
            modifier = Modifier.padding(top = Dimens.SpacingExtraLarge),
        )
    }
}

@Composable
private fun ConfirmationSection(
    task: Task,
    uiState: TaskDetailUiState,
    onCompleteTask: () -> Unit,
    onScanCode: () -> Unit,
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val submission = uiState.submission
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        when {
            uiState.isCompleted -> {
                StatusText(
                    text = uiState.earnedPoints?.let { stringResource(R.string.task_done_points_earned, it) }
                        ?: stringResource(R.string.task_detail_completed_label),
                    color = MaterialTheme.colorScheme.primary,
                )
                if (uiState.streakBonus > 0) {
                    StatusText(
                        text = stringResource(R.string.streak_bonus_msg, uiState.streakBonus),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            task.verification == TaskVerification.PHOTO && submission?.status == SubmissionStatus.PENDING ->
                StatusText(
                    text = stringResource(R.string.photo_under_review_msg),
                    color = MaterialTheme.colorScheme.primary,
                )

            else -> {
                if (task.verification == TaskVerification.PHOTO && submission?.status == SubmissionStatus.REJECTED) {
                    StatusText(
                        text = stringResource(R.string.photo_rejected, rejectionReasonText(submission.rejectionReason)),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Text(
                    text = stringResource(verificationHintRes(task.verification)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = Dimens.SpacingMedium),
                )
                GpPrimaryButton(
                    text = stringResource(confirmButtonRes(task.verification, submission?.status)),
                    onClick = when (task.verification) {
                        TaskVerification.SELF -> onCompleteTask
                        TaskVerification.QR -> onScanCode
                        TaskVerification.PHOTO -> onPickPhoto
                    },
                    isLoading = uiState.isSubmitting,
                )
            }
        }
        uiState.failure?.let { failure ->
            Text(
                text = stringResource(rewardFailureMessageRes(failure)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
        }
    }
}

@Composable
private fun StatusText(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = Dimens.SpacingSmall),
    )
}

@Composable
private fun rejectionReasonText(reason: String?): String = when (reason) {
    null, "" -> stringResource(R.string.no_reason_given)
    UNSAFE_PHOTO_REASON, INVALID_PHOTO_REASON -> stringResource(R.string.photo_failed_automatic_check_msg)
    else -> reason
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
