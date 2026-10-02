package com.smartcity.greenpassport.feature.calendar.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.common.eventDay
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.feature.calendar.domain.ObserveEventsUseCase
import com.smartcity.greenpassport.feature.calendar.presentation.state.CalendarSelection
import com.smartcity.greenpassport.feature.calendar.presentation.state.CalendarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val observeEvents: ObserveEventsUseCase,
) : ViewModel() {

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val selection = MutableStateFlow<CalendarSelection?>(null)

    val uiState = combine(observeCalendarUiState(), selection) { state, chosen ->
        if (state.isLoading || state.hasError) {
            state
        } else {
            val current = chosen ?: initialSelection(state.events)
            state.copy(selectedDay = current.day, visibleMonth = current.month)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        CalendarUiState(),
    )

    fun refresh() {
        retryRequests.tryEmit(Unit)
    }

    fun onDaySelected(day: LocalDate) {
        selection.value = CalendarSelection(day = day, month = YearMonth.from(day))
    }

    fun onMonthChange(month: YearMonth) {
        val day = selection.value?.day ?: uiState.value.selectedDay
        selection.value = CalendarSelection(day = day, month = month)
    }

    private fun initialSelection(events: List<EcoEvent>): CalendarSelection {
        val today = LocalDate.now()
        val day = events
            .map { eventDay(it.startAtEpochMillis) }
            .filter { !it.isBefore(today) }
            .minOrNull()
            ?: today
        return CalendarSelection(day = day, month = YearMonth.from(day))
    }

    private fun observeCalendarUiState(): Flow<CalendarUiState> {
        return retryRequests
            .onStart { emit(Unit) }
            .flatMapLatest {
                observeEvents()
                    .map { events ->
                        CalendarUiState(
                            events = events.sortedBy { it.startAtEpochMillis },
                            isLoading = false,
                        )
                    }
                    .onStart { emit(CalendarUiState(isLoading = true)) }
                    .catch { emit(CalendarUiState(isLoading = false, hasError = true)) }
            }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
