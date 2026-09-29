package com.smartcity.greenpassport.feature.calendar.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.calendar.domain.GetEventsUseCase
import com.smartcity.greenpassport.feature.calendar.presentation.state.CalendarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getEvents: GetEventsUseCase,
) : ViewModel() {

    private val refreshRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val uiState = observeCalendarUiState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        CalendarUiState(),
    )

    fun refresh() {
        refreshRequests.tryEmit(Unit)
    }

    private fun observeCalendarUiState(): Flow<CalendarUiState> {
        return refreshRequests
            .onStart { emit(Unit) }
            .flatMapLatest {
                flow {
                    emit(CalendarUiState(isLoading = true))
                    val events = runCatching { getEvents() }
                    emit(
                        CalendarUiState(
                            events = events.getOrDefault(emptyList()),
                            isLoading = false,
                            hasError = events.isFailure,
                        ),
                    )
                }
            }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
