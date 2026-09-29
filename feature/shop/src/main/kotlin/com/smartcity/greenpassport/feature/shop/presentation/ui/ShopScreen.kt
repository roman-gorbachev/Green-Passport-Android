package com.smartcity.greenpassport.feature.shop.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpListRow
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.SectionHeader
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.feature.shop.R
import com.smartcity.greenpassport.feature.shop.presentation.state.ShopUiState
import com.smartcity.greenpassport.feature.shop.presentation.viewmodels.ShopViewModel
import java.text.DateFormat
import java.util.Date
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun ShopScreen(
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.hasError) {
        ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::refresh,
            modifier = modifier,
        )
        return
    }

    if (uiState.isLoading) {
        LoadingContent(modifier = modifier)
        return
    }

    ShopContent(uiState = uiState, onPurchase = viewModel::onPurchase, modifier = modifier)
}

@Composable
private fun ShopContent(
    uiState: ShopUiState,
    onPurchase: (Reward) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingSmall,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
    ) {
        item {
            BalanceCard(points = uiState.points, hasInsufficientPoints = uiState.hasInsufficientPoints)
        }

        item {
            SectionHeader(
                title = stringResource(R.string.shop_catalog_title),
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
        }

        if (uiState.rewards.isEmpty()) {
            item {
                EmptyContent(message = stringResource(R.string.shop_empty_rewards))
            }
        } else {
            items(uiState.rewards, key = { it.id }) { reward ->
                RewardCard(
                    reward = reward,
                    isPurchasing = uiState.purchasingRewardId == reward.id,
                    onPurchase = { onPurchase(reward) },
                )
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.shop_history_title),
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
        }

        if (uiState.purchases.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.shop_empty_purchases),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(uiState.purchases) { purchase ->
                val reward = uiState.rewards.firstOrNull { it.id == purchase.rewardId }
                GpListRow(
                    title = reward?.title ?: purchase.rewardId,
                    subtitle = DateFormat.getDateInstance().format(Date(purchase.redeemedAtEpochMillis)),
                    trailing = {},
                    onClick = null,
                )
            }
        }
    }
}

@Composable
private fun BalanceCard(
    points: Int,
    hasInsufficientPoints: Boolean,
    modifier: Modifier = Modifier,
) {
    GpSurfaceCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.CardPadding)) {
            Text(
                text = stringResource(R.string.balance),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.shop_cost_format, points),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            if (hasInsufficientPoints) {
                Text(
                    text = stringResource(R.string.shop_insufficient_points),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = Dimens.SpacingSmall),
                )
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: Reward,
    isPurchasing: Boolean,
    onPurchase: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GpSurfaceCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.CardPadding)) {
            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = reward.partnerName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingMedium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.shop_cost_format, reward.pointsCost),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                if (isPurchasing) {
                    CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSizeMedium))
                } else {
                    GpPrimaryButton(
                        text = stringResource(R.string.shop_purchase_button),
                        onClick = onPurchase,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
