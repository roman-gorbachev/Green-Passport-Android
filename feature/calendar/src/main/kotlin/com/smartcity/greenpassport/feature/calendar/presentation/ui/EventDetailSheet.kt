package com.smartcity.greenpassport.feature.calendar.presentation.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.common.formatEventDate
import com.smartcity.greenpassport.core.common.formatEventTime
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.GpSheetScaffold
import com.smartcity.greenpassport.core.designsystem.component.NetworkImage
import com.smartcity.greenpassport.core.designsystem.text.localized
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.feature.calendar.R
import com.smartcity.greenpassport.feature.calendar.presentation.state.EventDetailUiState
import com.smartcity.greenpassport.feature.calendar.presentation.state.checkInFailureMessageRes
import com.smartcity.greenpassport.feature.calendar.presentation.viewmodels.EventDetailViewModel
import com.smartcity.greenpassport.core.R as CoreR

private const val EVENT_IMAGE_ASPECT_RATIO = 1.5f

@Composable
fun EventDetailSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EventDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = uiState.event
    val context = LocalContext.current

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
                onCheckIn = { viewModel.onCheckIn(context) },
            )
        }
    }
}

@Composable
private fun EventDetailContent(
    event: EcoEvent,
    uiState: EventDetailUiState,
    onSignUp: () -> Unit,
    onCheckIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale

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
                text = event.title.localized(),
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
                text = event.location.localized(),
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
            Text(
                text = event.description.localized(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.SpacingLarge),
            contentAlignment = Alignment.Center,
        ) {
            EventActions(event = event, uiState = uiState, onSignUp = onSignUp, onCheckIn = onCheckIn)
        }
    }
}

@Composable
private fun EventActions(
    event: EcoEvent,
    uiState: EventDetailUiState,
    onSignUp: () -> Unit,
    onCheckIn: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact),
        modifier = Modifier.fillMaxWidth(),
    ) {
        when {
            uiState.isCheckedIn -> {
                EventStatusLabel(
                    text = uiState.checkInPoints?.let { stringResource(R.string.event_points_earned, it) }
                        ?: stringResource(R.string.checked_in_at_event),
                )
                if (uiState.streakBonus > 0) {
                    Text(
                        text = stringResource(R.string.streak_bonus_msg, uiState.streakBonus),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            uiState.isRegistered -> {
                EventStatusLabel(text = stringResource(R.string.calendar_registered_label))
                if (uiState.isCheckInOpen(System.currentTimeMillis())) {
                    GpPrimaryButton(
                        text = stringResource(R.string.check_in_on_site),
                        onClick = onCheckIn,
                        isLoading = uiState.isCheckingIn,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.check_in_window_msg),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                uiState.checkInFailure?.let { failure ->
                    Text(
                        text = stringResource(checkInFailureMessageRes(failure)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> GpPrimaryButton(
                text = if (event.rewardPoints > 0) {
                    stringResource(R.string.sign_up_points, event.rewardPoints)
                } else {
                    stringResource(R.string.sign_up)
                },
                onClick = onSignUp,
                isLoading = uiState.isRegistering,
            )
        }
    }
}

@Composable
private fun EventStatusLabel(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(text = text, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
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
