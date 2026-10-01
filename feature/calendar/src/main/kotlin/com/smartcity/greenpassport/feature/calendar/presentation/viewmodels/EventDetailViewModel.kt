package com.smartcity.greenpassport.feature.calendar.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.core.model.rewards.RewardFailureException
import com.smartcity.greenpassport.feature.calendar.domain.CheckInEventUseCase
import com.smartcity.greenpassport.feature.calendar.domain.ObserveAttendedEventIdsUseCase
import com.smartcity.greenpassport.feature.calendar.domain.ObserveCalendarSessionUseCase
import com.smartcity.greenpassport.feature.calendar.domain.ObserveEventsUseCase
import com.smartcity.greenpassport.feature.calendar.domain.ObserveRegisteredEventIdsUseCase
import com.smartcity.greenpassport.feature.calendar.domain.RegisterForEventUseCase
import com.smartcity.greenpassport.feature.calendar.presentation.state.EventDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeEvents: ObserveEventsUseCase,
    private val observeRegisteredEventIds: ObserveRegisteredEventIdsUseCase,
    private val observeAttendedEventIds: ObserveAttendedEventIdsUseCase,
    private val registerForEvent: RegisterForEventUseCase,
    private val checkInEvent: CheckInEventUseCase,
    private val observeSession: ObserveCalendarSessionUseCase,
) : ViewModel() {

    private val eventId: String = checkNotNull(savedStateHandle[EVENT_ID_KEY])

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val actions = MutableStateFlow(ActionState())

    val uiState = observeEventDetailUiState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        EventDetailUiState(),
    )

    fun retry() {
        retryRequests.tryEmit(Unit)
    }

    fun onSignUp() {
        val state = uiState.value
        val event = state.event ?: return
        if (state.isRegistered || state.isRegistering) return
        viewModelScope.launch {
            val userId = observeSession().first()?.userId ?: return@launch
            actions.update { it.copy(isRegistering = true) }
            runCatching { registerForEvent(userId, event) }
                .onFailure { error -> Log.w(TAG, "Failed to register for event", error) }
            actions.update { it.copy(isRegistering = false) }
        }
    }

    fun onCheckIn(activityContext: Context) {
        val state = uiState.value
        if (state.isCheckedIn || state.isCheckingIn) return
        viewModelScope.launch {
            actions.update { it.copy(isCheckingIn = true, checkInFailure = null) }
            runCatching { checkInEvent(activityContext) }
                .onSuccess { reward ->
                    actions.update {
                        it.copy(
                            isCheckingIn = false,
                            checkInPoints = reward?.points ?: it.checkInPoints,
                            streakBonus = reward?.streakBonus ?: it.streakBonus,
                            isCheckedIn = it.isCheckedIn || reward != null,
                        )
                    }
                }
                .onFailure { error ->
                    Log.w(TAG, "Failed to check in", error)
                    val failure = (error as? RewardFailureException)?.failure ?: RewardFailure.UNKNOWN
                    actions.update { it.copy(isCheckingIn = false, checkInFailure = failure) }
                }
        }
    }

    private fun observeEventDetailUiState(): Flow<EventDetailUiState> {
        val data = combine(observeSession(), retryRequests.onStart { emit(Unit) }) { session, _ -> session }
            .flatMapLatest { session -> observeEventData(session) }
        return combine(data, actions) { state, action ->
            state.copy(
                isRegistering = action.isRegistering,
                isCheckingIn = action.isCheckingIn,
                isCheckedIn = state.isCheckedIn || action.isCheckedIn,
                isRegistered = state.isRegistered || action.isCheckedIn,
                checkInPoints = action.checkInPoints,
                streakBonus = action.streakBonus,
                checkInFailure = action.checkInFailure,
            )
        }
    }

    private fun observeEventData(session: AuthSession?): Flow<EventDetailUiState> {
        val userId = session?.userId
        val registered = if (userId == null) {
            flowOf(emptySet())
        } else {
            observeRegisteredEventIds(userId).catch { emit(emptySet()) }
        }
        val attended = if (userId == null) {
            flowOf(emptySet())
        } else {
            observeAttendedEventIds(userId).catch { emit(emptySet()) }
        }
        return combine(observeEvents(), registered, attended) { events, registeredIds, attendedIds ->
            val event = events.firstOrNull { it.id == eventId }
            EventDetailUiState(
                event = event,
                isRegistered = eventId in registeredIds,
                isCheckedIn = eventId in attendedIds,
                isLoading = false,
                hasError = event == null,
            )
        }
            .onStart { emit(EventDetailUiState()) }
            .catch { emit(EventDetailUiState(isLoading = false, hasError = true)) }
    }

    private data class ActionState(
        val isRegistering: Boolean = false,
        val isCheckingIn: Boolean = false,
        val isCheckedIn: Boolean = false,
        val checkInPoints: Int? = null,
        val streakBonus: Int = 0,
        val checkInFailure: RewardFailure? = null,
    )

    companion object {
        private const val TAG = "EventDetailViewModel"
        private const val EVENT_ID_KEY = "eventId"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
