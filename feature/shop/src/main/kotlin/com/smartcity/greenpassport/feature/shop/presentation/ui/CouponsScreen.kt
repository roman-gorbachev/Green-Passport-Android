package com.smartcity.greenpassport.feature.shop.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.ListRowChevron
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.SegmentedControl
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.component.SymbolTileStyle
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.CouponStatus
import com.smartcity.greenpassport.feature.shop.presentation.state.CouponItem
import com.smartcity.greenpassport.feature.shop.presentation.state.couponStatusText
import com.smartcity.greenpassport.feature.shop.presentation.state.couponsEmptyMessageRes
import com.smartcity.greenpassport.feature.shop.presentation.state.couponsTabLabelRes
import com.smartcity.greenpassport.feature.shop.presentation.viewmodels.CouponsViewModel
import com.smartcity.greenpassport.core.R as CoreR

private const val TITLE_MAX_LINES = 2

@Composable
fun CouponsScreen(
    onCouponSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: CouponsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val status = CouponStatus.entries[selectedTab]
    val listPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding()),
    ) {
        SegmentedControl(
            options = CouponStatus.entries.map { stringResource(couponsTabLabelRes(it)) },
            selectedIndex = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier.padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
        )
        val items = uiState.items(status)
        when {
            uiState.isLoading -> LoadingContent(modifier = Modifier.padding(listPadding))
            uiState.hasError -> ErrorContent(
                message = stringResource(CoreR.string.error_generic_message),
                retryLabel = stringResource(CoreR.string.retry_button),
                onRetry = viewModel::retry,
                modifier = Modifier.padding(listPadding),
            )
            items.isEmpty() -> EmptyContent(
                message = stringResource(couponsEmptyMessageRes(status)),
                modifier = Modifier.padding(listPadding),
            )

            else -> LazyColumn(
                contentPadding = listPadding + PaddingValues(
                    horizontal = Dimens.ScreenHorizontalPadding,
                    vertical = Dimens.SpacingSmall,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
            ) {
                items(items, key = { it.coupon.id }) { item ->
                    CouponCard(
                        item = item,
                        nowEpochMillis = uiState.nowEpochMillis,
                        onClick = { onCouponSelected(item.coupon.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CouponCard(
    item: CouponItem,
    nowEpochMillis: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val status = item.coupon.status(nowEpochMillis)
    val isAlert = status == CouponStatus.EXPIRED || item.isExpiringSoon(nowEpochMillis)
    GpSurfaceCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact),
            modifier = Modifier.padding(Dimens.CardPadding),
        ) {
            SymbolTile(
                icon = Icons.Filled.ConfirmationNumber,
                style = if (status == CouponStatus.ACTIVE) SymbolTileStyle.Accent else SymbolTileStyle.Muted,
                size = Dimens.CouponTileSize,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingHairline),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = TITLE_MAX_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
                item.reward?.partnerName?.let { partner ->
                    Text(
                        text = partner,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = couponStatusText(item, nowEpochMillis),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            }
            ListRowChevron()
        }
    }
}
