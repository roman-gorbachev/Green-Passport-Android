package com.smartcity.greenpassport.feature.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.feature.calendar.domain.GetEventsUseCase
import com.smartcity.greenpassport.feature.calendar.domain.GetRegisteredEventIdsUseCase
import com.smartcity.greenpassport.feature.calendar.domain.ObserveCalendarSessionUseCase
import com.smartcity.greenpassport.feature.calendar.domain.RegisterForEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getEvents: GetEventsUseCase,
    private val getRegisteredEventIds: GetRegisteredEventIdsUseCase,
    private val registerForEvent: RegisterForEventUseCase,
    observeSession: ObserveCalendarSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                refresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = currentUserId
            val events = getEvents()
            val registeredIds = userId?.let { getRegisteredEventIds(it) } ?: emptySet()
            _uiState.update {
                it.copy(events = events, registeredEventIds = registeredIds, isLoading = false)
            }
        }
    }

    fun onRegister(event: EcoEvent) {
        val userId = currentUserId ?: return
        if (_uiState.value.registeredEventIds.contains(event.id)) return
        if (_uiState.value.registeringEventId != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(registeringEventId = event.id) }
            registerForEvent(userId, event)
            _uiState.update {
                it.copy(
                    registeringEventId = null,
                    registeredEventIds = it.registeredEventIds + event.id,
                )
            }
        }
    }
}
