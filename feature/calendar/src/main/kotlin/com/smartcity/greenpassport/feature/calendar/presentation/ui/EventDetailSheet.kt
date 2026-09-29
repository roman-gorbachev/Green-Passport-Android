package com.smartcity.greenpassport.feature.calendar.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.common.formatEventDate
import com.smartcity.greenpassport.core.common.formatEventTime
import com.smartcity.greenpassport.core.designsystem.component.ConfirmHapticOnSuccess
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.GpSheetScaffold
import com.smartcity.greenpassport.core.designsystem.component.NetworkImage
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.feature.calendar.R
import com.smartcity.greenpassport.feature.calendar.presentation.state.EventDetailUiState
import com.smartcity.greenpassport.feature.calendar.presentation.viewmodels.EventDetailViewModel
import java.util.Locale
import com.smartcity.greenpassport.core.R as CoreR

private const val EVENT_IMAGE_ASPECT_RATIO = 1.5f
private const val DESCRIPTION_MAX_LINES = 2

@Composable
fun EventDetailSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EventDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = uiState.event

    ConfirmHapticOnSuccess(inProgress = uiState.isRegistering, succeeded = uiState.isRegistered)

    GpSheetScaffold(onDismiss = onDismiss, modifier = modifier) {
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.HeroCardHeight),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            uiState.hasError || event == null -> ErrorContent(
                message = stringResource(CoreR.string.error_generic_message),
                retryLabel = stringResource(CoreR.string.retry_button),
                onRetry = viewModel::retry,
                modifier = Modifier.fillMaxWidth(),
            )

            else -> EventDetailContent(
                event = event,
                uiState = uiState,
                onSignUp = viewModel::onSignUp,
            )
        }
    }
}

@Composable
private fun EventDetailContent(
    event: EcoEvent,
    uiState: EventDetailUiState,
    onSignUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
        ) {
            NetworkImage(
                url = event.imageUrl,
                contentDescription = null,
                fallback = painterResource(CoreR.drawable.event_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(EVENT_IMAGE_ASPECT_RATIO)
                    .clip(MaterialTheme.shapes.large),
            )
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
            EventInfoRow(
                icon = Icons.Outlined.CalendarMonth,
                text = stringResource(
                    R.string.date_time,
                    formatEventDate(event.startAtEpochMillis, locale),
                    formatEventTime(event.startAtEpochMillis, locale),
                ),
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
            EventInfoRow(
                icon = Icons.Outlined.Place,
                text = event.location,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                maxLines = DESCRIPTION_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.SpacingLarge),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.isRegistered -> Text(
                    text = stringResource(R.string.calendar_registered_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                uiState.isRegistering -> CircularProgressIndicator()

                else -> GpPrimaryButton(
                    text = if (event.rewardPoints > 0) {
                        stringResource(R.string.sign_up_points, event.rewardPoints)
                    } else {
                        stringResource(R.string.sign_up)
                    },
                    onClick = onSignUp,
                )
            }
        }
    }
}

@Composable
private fun EventInfoRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(Dimens.IconSizeSmall),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(start = Dimens.SpacingSmall),
        )
    }
}
