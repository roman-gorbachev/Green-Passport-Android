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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.component.ListSectionDivider
import com.smartcity.greenpassport.core.designsystem.component.ListSectionRow
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.ProgressHeroCard
import com.smartcity.greenpassport.core.designsystem.component.SectionHeader
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.layout.plus
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
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ShopViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.hasError) {
        ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::refresh,
            modifier = modifier.padding(contentPadding),
        )
        return
    }

    if (uiState.isLoading) {
        LoadingContent(modifier = modifier.padding(contentPadding))
        return
    }

    var pendingReward by remember { mutableStateOf<Reward?>(null) }
    pendingReward?.let { reward ->
        AlertDialog(
            onDismissRequest = { pendingReward = null },
            title = { Text(stringResource(R.string.shop_purchase_button)) },
            text = { Text(stringResource(R.string.exchange_points_for_reward_msg, reward.pointsCost, reward.title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingReward = null
                        viewModel.onPurchase(reward)
                    },
                ) {
                    Text(stringResource(R.string.shop_purchase_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingReward = null }) {
                    Text(stringResource(CoreR.string.cancel))
                }
            },
        )
    }

    ShopContent(
        uiState = uiState,
        onPurchase = { reward -> pendingReward = reward },
        contentPadding = contentPadding,
        modifier = modifier,
    )
}

@Composable
private fun ShopContent(
    uiState: ShopUiState,
    onPurchase: (Reward) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding + PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingMedium,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
    ) {
        item {
            BalanceCard(points = uiState.points, hasInsufficientPoints = uiState.hasInsufficientPoints)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact)) {
                SectionHeader(title = stringResource(R.string.shop_catalog_title))
                if (uiState.rewards.isEmpty()) {
                    Text(
                        text = stringResource(R.string.shop_empty_rewards),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    ListSection {
                        uiState.rewards.forEachIndexed { index, reward ->
                            RewardRow(
                                reward = reward,
                                isPurchasing = uiState.purchasingRewardId == reward.id,
                                onPurchase = { onPurchase(reward) },
                            )
                            if (index < uiState.rewards.lastIndex) {
                                ListSectionDivider(hasLeading = false)
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact)) {
                SectionHeader(title = stringResource(R.string.shop_history_title))
                if (uiState.purchases.isEmpty()) {
                    Text(
                        text = stringResource(R.string.shop_empty_purchases),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    ListSection {
                        uiState.purchases.forEachIndexed { index, purchase ->
                            val reward = uiState.rewards.firstOrNull { it.id == purchase.rewardId }
                            ListSectionRow(
                                title = reward?.title ?: purchase.rewardId,
                                subtitle = DateFormat.getDateInstance().format(Date(purchase.redeemedAtEpochMillis)),
                                leading = { SymbolTile(icon = Icons.Filled.ConfirmationNumber) },
                                trailing = {},
                                onClick = null,
                                showDivider = index < uiState.purchases.lastIndex,
                            )
                        }
                    }
                }
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
    Column(modifier = modifier.fillMaxWidth()) {
        ProgressHeroCard(points = points)
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

@Composable
private fun RewardRow(
    reward: Reward,
    isPurchasing: Boolean,
    onPurchase: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.CardPadding, vertical = Dimens.SpacingCompact),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingHairline),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = reward.partnerName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.shop_cost_format, reward.pointsCost),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        if (isPurchasing) {
            CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSizeMedium))
        } else {
            Button(
                onClick = onPurchase,
                shape = RoundedCornerShape(Dimens.CornerRadiusPill),
                modifier = Modifier.padding(start = Dimens.SpacingCompact),
            ) {
                Text(text = stringResource(R.string.shop_purchase_button), style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
