package com.smartcity.greenpassport.feature.ecotips.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.feature.ecotips.R
import com.smartcity.greenpassport.feature.ecotips.presentation.viewmodels.EcoTipDetailViewModel
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun EcoTipDetailScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: EcoTipDetailViewModel = hiltViewModel(),
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

    if (uiState.isLoading || uiState.tip == null) {
        LoadingContent(modifier = modifier.padding(contentPadding))
        return
    }

    EcoTipDetailContent(
        tip = uiState.tip!!,
        isRead = uiState.isRead,
        isSubmitting = uiState.isSubmitting,
        onMarkAsRead = viewModel::onMarkAsRead,
        contentPadding = contentPadding,
        modifier = modifier,
    )
}

@Composable
private fun EcoTipDetailContent(
    tip: EcoTip,
    isRead: Boolean,
    isSubmitting: Boolean,
    onMarkAsRead: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = contentPadding.calculateTopPadding())
                .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingMedium),
        ) {
            Text(
                text = stringResource(ecoTipCategoryLabelRes(tip.category)),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = tip.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = tip.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            val mediaUrl = tip.mediaUrl
            if (mediaUrl != null) {
                Text(
                    text = mediaUrl,
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(role = Role.Button) { runCatching { uriHandler.openUri(mediaUrl) } },
                )
            }
            Text(
                text = stringResource(R.string.ecotip_detail_reward_format, tip.rewardPoints, tip.rewardXp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = contentPadding.calculateBottomPadding())
                .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingMedium),
        ) {
            if (isRead) {
                Text(
                    text = stringResource(R.string.ecotip_detail_read_label),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                GpPrimaryButton(
                    text = stringResource(R.string.ecotip_detail_mark_read_button),
                    onClick = onMarkAsRead,
                    isLoading = isSubmitting,
                )
            }
        }
    }
}
