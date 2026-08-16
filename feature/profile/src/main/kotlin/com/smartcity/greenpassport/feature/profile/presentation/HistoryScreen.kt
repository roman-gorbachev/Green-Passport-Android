package com.smartcity.greenpassport.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.smartcity.greenpassport.core.model.HistoryEntry
import com.smartcity.greenpassport.feature.profile.R
import java.text.DateFormat
import java.util.Date
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier)
        uiState.hasError -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::retry,
            modifier = modifier,
        )
        uiState.entries.isEmpty() -> EmptyContent(
            message = stringResource(R.string.history_empty),
            modifier = modifier,
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimens.SpacingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        ) {
            items(uiState.entries) { entry -> HistoryRow(entry) }
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = historyEntryTypeIcon(entry.type),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(modifier = Modifier.padding(start = Dimens.SpacingSmall)) {
                Text(text = entry.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(
                        R.string.history_row_subtitle_format,
                        stringResource(historyEntryTypeLabelRes(entry.type)),
                        DateFormat.getDateInstance().format(Date(entry.timestampEpochMillis)),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
