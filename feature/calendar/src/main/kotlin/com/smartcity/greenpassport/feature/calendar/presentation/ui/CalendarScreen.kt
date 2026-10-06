package com.smartcity.greenpassport.feature.calendar.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.common.formatEventDate
import com.smartcity.greenpassport.core.common.formatEventTime
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.HeroImageCard
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.text.localized
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.calendar.R
import com.smartcity.greenpassport.feature.calendar.presentation.viewmodels.CalendarViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import com.smartcity.greenpassport.core.R as CoreR

private const val MONTH_KEY = "month"
private const val DAY_TITLE_KEY = "day_title"
private const val NO_EVENTS_KEY = "no_events"
private const val DAY_TITLE_PATTERN = "EEEE, d MMMM"
private const val DAY_TITLE_ITEM_INDEX = 1

@Composable
fun CalendarScreen(
    onEventSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locale = LocalLocale.current.platformLocale
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier.padding(contentPadding))
        uiState.hasError -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::refresh,
            modifier = modifier.padding(contentPadding),
        )
        uiState.events.isEmpty() -> EmptyContent(
            message = stringResource(R.string.calendar_empty),
            modifier = modifier.padding(contentPadding),
        )

        else -> LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding + PaddingValues(
                horizontal = Dimens.ScreenHorizontalPadding,
                vertical = Dimens.SpacingMedium,
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
        ) {
            item(key = MONTH_KEY) {
                GpSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
                        modifier = Modifier.padding(Dimens.CardPadding),
                    ) {
                        MonthCalendar(
                            month = uiState.visibleMonth,
                            selectedDay = uiState.selectedDay,
                            dayCounts = uiState.dayCounts,
                            onMonthChange = viewModel::onMonthChange,
                            onDaySelected = { day ->
                                viewModel.onDaySelected(day)
                                coroutineScope.launch { listState.animateScrollToItem(DAY_TITLE_ITEM_INDEX) }
                            },
                        )
                        CalendarLegend()
                    }
                }
            }
            item(key = DAY_TITLE_KEY) {
                Text(
                    text = uiState.selectedDay
                        .format(DateTimeFormatter.ofPattern(DAY_TITLE_PATTERN, locale))
                        .replaceFirstChar { it.titlecase(locale) },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            if (uiState.dayEvents.isEmpty()) {
                item(key = NO_EVENTS_KEY) {
                    Text(
                        text = stringResource(R.string.calendar_no_events_on_day),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(uiState.dayEvents, key = { it.id }) { event ->
                HeroImageCard(
                    imageUrl = event.imageUrl,
                    title = event.title.localized(),
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
