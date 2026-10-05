package com.smartcity.greenpassport.feature.ecotips.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.common.articlePreview
import com.smartcity.greenpassport.core.common.articleReadMinutes
import com.smartcity.greenpassport.core.designsystem.component.ChoiceCapsule
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.text.localized
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.feature.ecotips.R
import com.smartcity.greenpassport.feature.ecotips.presentation.state.EcoTipsListUiState
import com.smartcity.greenpassport.feature.ecotips.presentation.viewmodels.EcoTipsListViewModel
import com.smartcity.greenpassport.core.R as CoreR

private const val PREVIEW_LINES = 2
private const val META_SEPARATOR = " · "

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
    val body = tip.body.localized()
    GpSurfaceCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenHorizontalPadding),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact),
            modifier = Modifier.padding(
                start = Dimens.CardPadding,
                top = Dimens.SpacingCompact,
                bottom = Dimens.SpacingCompact
            ),
        ) {
            ArticleCover(
                tip = tip,
                modifier = Modifier
                    .size(Dimens.ArticleThumbnailSize)
                    .clip(RoundedCornerShape(Dimens.CornerRadiusMedium)),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = tip.title.localized(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = PREVIEW_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = remember(body) { articlePreview(body) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = PREVIEW_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
                TipMeta(tip = tip, body = body, isRead = isRead)
            }
            IconButton(onClick = onToggleBookmark) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isBookmarked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

@Composable
private fun TipMeta(tip: EcoTip, body: String, isRead: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
    ) {
        if (isRead) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = stringResource(R.string.ecotip_detail_read_label),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.IconSizeExtraSmall),
            )
        }
        Text(
            text = stringResource(ecoTipCategoryLabelRes(tip.category)) + META_SEPARATOR +
                stringResource(CoreR.string.read_minutes_format, remember(body) { articleReadMinutes(body) }),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DailyTipCard(
    tip: EcoTip,
    onClick: () -> Unit,
) {
    val body = tip.body.localized()
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
                text = tip.title.localized(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            )
            Text(
                text = remember(body) { articlePreview(body) },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = PREVIEW_LINES,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            )
        }
    }
}
