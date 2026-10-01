package com.smartcity.greenpassport.feature.calendar.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.calendar.domain.ObserveEventsUseCase
import com.smartcity.greenpassport.feature.calendar.presentation.state.CalendarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val observeEvents: ObserveEventsUseCase,
) : ViewModel() {

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val uiState = observeCalendarUiState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        CalendarUiState(),
    )

    fun refresh() {
        retryRequests.tryEmit(Unit)
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
