package com.smartcity.greenpassport.feature.map.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpFilterChip
import com.smartcity.greenpassport.core.designsystem.component.GpListRow
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.GpSearchField
import com.smartcity.greenpassport.core.designsystem.component.GpSheetScaffold
import com.smartcity.greenpassport.core.designsystem.component.IconCircle
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.ScreenHeader
import com.smartcity.greenpassport.core.designsystem.component.SheetDialogProperties
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointType
import com.smartcity.greenpassport.feature.map.MapKitInitializer
import com.smartcity.greenpassport.feature.map.R
import com.smartcity.greenpassport.feature.map.presentation.state.MapUiState
import com.smartcity.greenpassport.feature.map.presentation.viewmodels.MapViewModel
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (MapKitInitializer.isAvailable) {
        MapContent(
            uiState = uiState,
            onTypeSelected = viewModel::onTypeSelected,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onPointSelected = viewModel::onPointSelected,
            onRetry = viewModel::refresh,
            modifier = modifier,
        )
    } else {
        MapPointsListContent(
            uiState = uiState,
            onTypeSelected = viewModel::onTypeSelected,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onPointSelected = viewModel::onPointSelected,
            onRetry = viewModel::refresh,
            modifier = modifier,
        )
    }

    uiState.selectedPoint?.let { point ->
        MapPointSheet(
            point = point,
            isSaved = point.id in uiState.savedPointIds,
            onToggleSaved = { viewModel.onToggleSaved(point.id) },
            onDismiss = { viewModel.onPointSelected(null) },
        )
    }
}

@Composable
private fun MapContent(
    uiState: MapUiState,
    onTypeSelected: (MapPointType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onPointSelected: (String?) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val logoBottomPaddingPx = with(LocalDensity.current) {
        (bottomInset + Dimens.BottomBarReservedHeight).roundToPx()
    }

    Box(modifier = modifier.fillMaxSize()) {
        YandexMap(
            points = uiState.visiblePoints,
            onPointClick = onPointSelected,
            logoBottomPaddingPx = logoBottomPaddingPx,
            modifier = Modifier.fillMaxSize(),
        )

        MapFiltersOverlay(
            uiState = uiState,
            onTypeSelected = onTypeSelected,
            onSearchQueryChange = onSearchQueryChange,
            isFloating = true,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(vertical = Dimens.SpacingSmall),
        )

        when {
            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            uiState.hasError -> ErrorContent(
                message = stringResource(CoreR.string.error_generic_message),
                retryLabel = stringResource(CoreR.string.retry_button),
                onRetry = onRetry,
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
            )
        }
    }
}

@Composable
private fun MapPointsListContent(
    uiState: MapUiState,
    onTypeSelected: (MapPointType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onPointSelected: (String?) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(modifier = modifier.fillMaxSize()) {
        ScreenHeader(title = stringResource(CoreR.string.map))
        MapFiltersOverlay(
            uiState = uiState,
            onTypeSelected = onTypeSelected,
            onSearchQueryChange = onSearchQueryChange,
        )
        when {
            uiState.isLoading -> LoadingContent()
            uiState.hasError -> ErrorContent(
                message = stringResource(CoreR.string.error_generic_message),
                retryLabel = stringResource(CoreR.string.retry_button),
                onRetry = onRetry,
            )
            uiState.visiblePoints.isEmpty() -> EmptyContent(message = stringResource(R.string.map_empty))
            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = Dimens.ScreenHorizontalPadding,
                    end = Dimens.ScreenHorizontalPadding,
                    top = Dimens.SpacingSmall,
                    bottom = bottomInset + Dimens.BottomBarReservedHeight + Dimens.SpacingMedium,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
            ) {
                items(uiState.visiblePoints, key = { it.id }) { point ->
                    GpListRow(
                        title = point.name,
                        subtitle = point.address,
                        leading = {
                            IconCircle(
                                icon = mapPointTypeIcon(point.type),
                                color = mapPointTypeColor(point.type),
                                size = Dimens.IconCircleSmallSize,
                            )
                        },
                        onClick = { onPointSelected(point.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MapFiltersOverlay(
    uiState: MapUiState,
    onTypeSelected: (MapPointType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFloating: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        GpSearchField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = stringResource(R.string.map_search_placeholder),
            modifier = Modifier
                .padding(horizontal = Dimens.ScreenHorizontalPadding)
                .then(
                    if (isFloating) {
                        Modifier.shadow(Dimens.BottomBarElevation, RoundedCornerShape(Dimens.CornerRadiusPill))
                    } else {
                        Modifier
                    },
                ),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.ScreenHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            modifier = Modifier.padding(top = Dimens.SpacingSmall),
        ) {
            item {
                GpFilterChip(
                    label = stringResource(R.string.map_filter_all),
                    selected = uiState.selectedType == null,
                    onClick = { onTypeSelected(null) },
                )
            }
            items(MapPointType.entries) { type ->
                GpFilterChip(
                    label = stringResource(mapPointTypeLabelRes(type)),
                    selected = uiState.selectedType == type,
                    onClick = { onTypeSelected(type) },
                )
            }
        }
    }
}

@Composable
private fun MapPointSheet(
    point: MapPoint,
    isSaved: Boolean,
    onToggleSaved: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss, properties = SheetDialogProperties) {
        GpSheetScaffold(onDismiss = onDismiss) {
            Text(
                text = point.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(mapPointTypeLabelRes(point.type)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            )
            Text(
                text = point.address,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
            GpPrimaryButton(
                text = stringResource(if (isSaved) R.string.saved else R.string.save),
                onClick = onToggleSaved,
                modifier = Modifier.padding(top = Dimens.SpacingLarge),
            )
        }
    }
}
