package com.smartcity.greenpassport.feature.ecotips.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ChoiceCapsule
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpListRow
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.feature.ecotips.R
import com.smartcity.greenpassport.feature.ecotips.presentation.state.EcoTipsListUiState
import com.smartcity.greenpassport.feature.ecotips.presentation.viewmodels.EcoTipsListViewModel
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun EcoTipsListScreen(
    onTipSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: EcoTipsListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EcoTipsListContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onTipSelected = onTipSelected,
        onToggleBookmark = viewModel::onToggleBookmark,
        onRetry = viewModel::refresh,
        contentPadding = contentPadding,
        modifier = modifier,
    )
}

@Composable
private fun EcoTipsListContent(
    uiState: EcoTipsListUiState,
    onCategorySelected: (EcoTipCategory?) -> Unit,
    onTipSelected: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onRetry: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val listPadding = contentPadding + PaddingValues(vertical = Dimens.SpacingSmall)
    when {
        uiState.isLoading -> LoadingContent(modifier = modifier.padding(contentPadding))
        uiState.hasError -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = onRetry,
            modifier = modifier.padding(contentPadding),
        )

        else -> LazyColumn(
            contentPadding = listPadding,
            verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
            modifier = modifier.fillMaxSize(),
        ) {
            val dailyTip = uiState.dailyTip
            if (dailyTip != null) {
                item {
                    DailyTipCard(tip = dailyTip, onClick = { onTipSelected(dailyTip.id) })
                }
            }
            item {
                CategoryFilters(selectedCategory = uiState.selectedCategory, onCategorySelected = onCategorySelected)
            }
            if (uiState.visibleTips.isEmpty()) {
                item {
                    EmptyContent(message = stringResource(R.string.ecotips_empty))
                }
            }
            items(uiState.visibleTips, key = { it.id }) { tip ->
                TipRow(
                    tip = tip,
                    isRead = uiState.readTipIds.contains(tip.id),
                    isBookmarked = uiState.bookmarkedTipIds.contains(tip.id),
                    onClick = { onTipSelected(tip.id) },
                    onToggleBookmark = { onToggleBookmark(tip.id) },
                )
            }
        }
    }
}

@Composable
private fun CategoryFilters(
    selectedCategory: EcoTipCategory?,
    onCategorySelected: (EcoTipCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.ScreenHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = modifier,
    ) {
        item {
            ChoiceCapsule(
                label = stringResource(R.string.ecotips_filter_all),
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
            )
        }
        items(EcoTipCategory.entries) { category ->
            ChoiceCapsule(
                label = stringResource(ecoTipCategoryLabelRes(category)),
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
            )
        }
    }
}

@Composable
private fun TipRow(
    tip: EcoTip,
    isRead: Boolean,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GpListRow(
        title = tip.title,
        subtitle = stringResource(ecoTipCategoryLabelRes(tip.category)),
        leading = { SymbolTile(icon = if (isRead) Icons.Filled.Check else Icons.Filled.Eco) },
        trailing = {
            IconButton(onClick = onToggleBookmark) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = null,
                    tint = if (isBookmarked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        },
        onClick = onClick,
        modifier = modifier.padding(horizontal = Dimens.ScreenHorizontalPadding),
    )
}

@Composable
private fun DailyTipCard(
    tip: EcoTip,
    onClick: () -> Unit,
) {
    GpSurfaceCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenHorizontalPadding),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(modifier = Modifier.padding(Dimens.CardPadding)) {
            Text(
                text = stringResource(R.string.ecotips_daily_tip_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            )
        }
    }
}
