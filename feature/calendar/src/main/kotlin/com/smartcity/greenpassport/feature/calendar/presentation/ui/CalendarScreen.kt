package com.smartcity.greenpassport.feature.calendar.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.common.formatEventDate
import com.smartcity.greenpassport.core.common.formatEventTime
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.HeroImageCard
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.calendar.R
import com.smartcity.greenpassport.feature.calendar.presentation.viewmodels.CalendarViewModel
import java.util.Locale
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun CalendarScreen(
    onEventSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier)
        uiState.hasError -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::refresh,
            modifier = modifier,
        )
        uiState.events.isEmpty() -> EmptyContent(
            message = stringResource(R.string.calendar_empty),
            modifier = modifier,
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = Dimens.ScreenHorizontalPadding,
                vertical = Dimens.SpacingMedium,
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
        ) {
            items(uiState.events, key = { it.id }) { event ->
                HeroImageCard(
                    imageUrl = event.imageUrl,
                    title = event.title,
                    subtitle = stringResource(
                        R.string.date_time,
                        formatEventDate(event.startAtEpochMillis, locale),
                        formatEventTime(event.startAtEpochMillis, locale),
                    ),
                    onClick = { onEventSelected(event.id) },
                    height = Dimens.EventCardHeight,
                )
            }
        }
    }
}
