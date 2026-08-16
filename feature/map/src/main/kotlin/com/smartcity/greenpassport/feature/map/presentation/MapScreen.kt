package com.smartcity.greenpassport.feature.map.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointType
import com.smartcity.greenpassport.feature.map.R
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MapContent(
        uiState = uiState,
        onTypeSelected = viewModel::onTypeSelected,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onToggleSaved = viewModel::onToggleSaved,
        onRetry = viewModel::refresh,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapContent(
    uiState: MapUiState,
    onTypeSelected: (MapPointType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleSaved: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text(stringResource(R.string.map_search_placeholder)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpacingMedium),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.SpacingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedType == null,
                    onClick = { onTypeSelected(null) },
                    label = { Text(stringResource(R.string.map_filter_all)) },
                )
            }
            items(MapPointType.entries) { type ->
                FilterChip(
                    selected = uiState.selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(stringResource(mapPointTypeLabelRes(type))) },
                )
            }
        }

        when {
            uiState.isLoading -> LoadingContent(modifier = Modifier.fillMaxSize())
            uiState.hasError -> ErrorContent(
                message = stringResource(CoreR.string.error_generic_message),
                retryLabel = stringResource(CoreR.string.retry_button),
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
            )
            uiState.visiblePoints.isEmpty() -> EmptyContent(
                message = stringResource(R.string.map_empty),
                modifier = Modifier.fillMaxSize(),
            )

            else -> LazyColumn(
                contentPadding = PaddingValues(Dimens.SpacingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            ) {
                items(uiState.visiblePoints) { point ->
                    MapPointRow(
                        point = point,
                        isSaved = uiState.savedPointIds.contains(point.id),
                        onToggleSaved = { onToggleSaved(point.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MapPointRow(
    point: MapPoint,
    isSaved: Boolean,
    onToggleSaved: () -> Unit,
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
                Text(text = point.name, style = MaterialTheme.typography.titleMedium)
                Text(text = point.address, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onToggleSaved) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = null,
                )
            }
        }
    }
}
