package com.smartcity.greenpassport.feature.shop.presentation

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.smartcity.greenpassport.core.designsystem.component.PointsBadge
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.feature.shop.R
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
        contentPadding = PaddingValues(Dimens.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PointsBadge(points = uiState.points)
                if (uiState.hasInsufficientPoints) {
                    Text(
                        text = stringResource(R.string.shop_insufficient_points),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = Dimens.SpacingSmall),
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.shop_catalog_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (uiState.rewards.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.shop_empty_rewards),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            items(uiState.rewards) { reward ->
                RewardCard(
                    reward = reward,
                    isPurchasing = uiState.purchasingRewardId == reward.id,
                    onPurchase = { onPurchase(reward) },
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.shop_history_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
        }

        if (uiState.purchases.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.shop_empty_purchases),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            items(uiState.purchases) { purchase ->
                val reward = uiState.rewards.firstOrNull { it.id == purchase.rewardId }
                PurchaseRow(purchase = purchase, rewardTitle = reward?.title ?: purchase.rewardId)
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: Reward,
    isPurchasing: Boolean,
    onPurchase: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpacingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reward.title, style = MaterialTheme.typography.titleMedium)
                Text(text = reward.partnerName, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = stringResource(R.string.shop_cost_format, reward.pointsCost),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (isPurchasing) {
                CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSizeMedium))
            } else {
                Button(onClick = onPurchase) {
                    Text(stringResource(R.string.shop_purchase_button))
                }
            }
        }
    }
}

@Composable
private fun PurchaseRow(purchase: Coupon, rewardTitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = rewardTitle, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = DateFormat.getDateInstance().format(Date(purchase.redeemedAtEpochMillis)),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
