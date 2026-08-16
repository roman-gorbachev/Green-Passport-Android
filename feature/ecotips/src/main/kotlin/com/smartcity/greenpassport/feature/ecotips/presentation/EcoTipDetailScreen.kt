package com.smartcity.greenpassport.feature.ecotips.presentation

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
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.feature.ecotips.R
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun EcoTipDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: EcoTipDetailViewModel = hiltViewModel(),
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

    if (uiState.isLoading || uiState.tip == null) {
        LoadingContent(modifier = modifier)
        return
    }

    EcoTipDetailContent(
        tip = uiState.tip!!,
        isRead = uiState.isRead,
        isSubmitting = uiState.isSubmitting,
        onMarkAsRead = viewModel::onMarkAsRead,
        modifier = modifier,
    )
}

@Composable
private fun EcoTipDetailContent(
    tip: EcoTip,
    isRead: Boolean,
    isSubmitting: Boolean,
    onMarkAsRead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.SpacingLarge),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(text = stringResource(ecoTipCategoryLabelRes(tip.category)), style = MaterialTheme.typography.labelLarge)

        Text(
            text = tip.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = Dimens.SpacingSmall),
        )

        Text(
            text = tip.body,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = Dimens.SpacingMedium),
        )

        val mediaUrl = tip.mediaUrl
        if (mediaUrl != null) {
            Text(
                text = mediaUrl,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
        }

        Text(
            text = stringResource(R.string.ecotip_detail_reward_format, tip.rewardPoints, tip.rewardXp),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = Dimens.SpacingLarge),
        )

        when {
            isRead -> Text(
                text = stringResource(R.string.ecotip_detail_read_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = Dimens.SpacingLarge),
            )

            isSubmitting -> CircularProgressIndicator(modifier = Modifier.padding(top = Dimens.SpacingLarge))

            else -> Button(
                onClick = onMarkAsRead,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingLarge),
            ) {
                Text(stringResource(R.string.ecotip_detail_mark_read_button))
            }
        }
    }
}
