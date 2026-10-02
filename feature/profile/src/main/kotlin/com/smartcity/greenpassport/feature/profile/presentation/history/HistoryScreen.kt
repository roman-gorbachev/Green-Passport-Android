package com.smartcity.greenpassport.feature.profile.presentation.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.R as CoreR
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.ListRowContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.component.listSectionItems
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.text.localized
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.HistoryEntry
import com.smartcity.greenpassport.feature.profile.R
import java.text.DateFormat
import java.util.Date

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier.padding(contentPadding))
        uiState.hasError -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::retry,
            modifier = modifier.padding(contentPadding),
        )
        uiState.entries.isEmpty() -> EmptyContent(
            message = stringResource(R.string.history_empty),
            modifier = modifier.padding(contentPadding),
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding + PaddingValues(
                horizontal = Dimens.ScreenHorizontalPadding,
                vertical = Dimens.SpacingMedium,
            ),
        ) {
            listSectionItems(uiState.entries) { entry -> HistoryRow(entry) }
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry) {
    ListRowContent(
        title = entry.title.localized(),
        subtitle = stringResource(
            R.string.history_row_subtitle_format,
            stringResource(historyEntryTypeLabelRes(entry.type)),
            DateFormat.getDateInstance().format(Date(entry.timestampEpochMillis)),
        ),
        leading = { SymbolTile(icon = historyEntryTypeIcon(entry.type), size = Dimens.HistoryTileSize) },
        trailing = {},
    )
}
