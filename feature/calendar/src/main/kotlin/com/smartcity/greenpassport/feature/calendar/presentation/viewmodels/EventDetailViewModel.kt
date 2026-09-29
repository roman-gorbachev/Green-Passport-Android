package com.smartcity.greenpassport.feature.calendar.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.feature.calendar.domain.GetEventsUseCase
import com.smartcity.greenpassport.feature.calendar.domain.GetRegisteredEventIdsUseCase
import com.smartcity.greenpassport.feature.calendar.domain.ObserveCalendarSessionUseCase
import com.smartcity.greenpassport.feature.calendar.domain.RegisterForEventUseCase
import com.smartcity.greenpassport.feature.calendar.presentation.state.EventDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getEvents: GetEventsUseCase,
    private val getRegisteredEventIds: GetRegisteredEventIdsUseCase,
    private val registerForEvent: RegisterForEventUseCase,
    private val observeSession: ObserveCalendarSessionUseCase,
) : ViewModel() {

    private val eventId: String = checkNotNull(savedStateHandle[EVENT_ID_KEY])

    private val refreshRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val registration = MutableStateFlow(RegistrationState())

    val uiState = observeEventDetailUiState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        EventDetailUiState(),
    )

    fun retry() {
        refreshRequests.tryEmit(Unit)
    }

    fun onSignUp() {
        val state = uiState.value
        val event = state.event ?: return
        if (state.isRegistered || state.isRegistering) return

        viewModelScope.launch {
            val userId = observeSession().first()?.userId ?: return@launch
            registration.update { it.copy(isRegistering = true) }
            runCatching { registerForEvent(userId, event) }
                .onSuccess { registration.update { RegistrationState(isRegistered = true) } }
                .onFailure { error ->
                    Log.w(TAG, "Failed to register for event", error)
                    registration.update { it.copy(isRegistering = false) }
                }
        }
    }

    private fun observeEventDetailUiState(): Flow<EventDetailUiState> {
        val loaded = refreshRequests
            .onStart { emit(Unit) }
            .flatMapLatest { observeSession() }
            .flatMapLatest { session -> loadEvent(session) }
        return combine(loaded, registration) { state, registrationState ->
            state.copy(
                isRegistered = state.isRegistered || registrationState.isRegistered,
                isRegistering = registrationState.isRegistering,
            )
        }
    }

    private fun loadEvent(session: AuthSession?): Flow<EventDetailUiState> = flow {
        emit(EventDetailUiState(isLoading = true))
        val result = runCatching {
            val event = getEvents().firstOrNull { it.id == eventId }
            val registeredIds = session?.let { getRegisteredEventIds(it.userId) }.orEmpty()
            EventDetailUiState(
                event = event,
                isRegistered = eventId in registeredIds,
                isLoading = false,
                hasError = event == null,
            )
        }
        emit(result.getOrDefault(EventDetailUiState(isLoading = false, hasError = true)))
    }

    private data class RegistrationState(
        val isRegistered: Boolean = false,
        val isRegistering: Boolean = false,
    )

    companion object {
        private const val TAG = "EventDetailViewModel"
        private const val EVENT_ID_KEY = "eventId"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
