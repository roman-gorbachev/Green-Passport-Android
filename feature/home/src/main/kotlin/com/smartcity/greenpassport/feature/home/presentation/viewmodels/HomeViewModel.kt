package com.smartcity.greenpassport.feature.home.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.feature.home.domain.GetLevelUseCase
import com.smartcity.greenpassport.feature.home.domain.GetPendingTasksUseCase
import com.smartcity.greenpassport.feature.home.domain.GetUpcomingEventUseCase
import com.smartcity.greenpassport.feature.home.domain.ObserveHomeSessionUseCase
import com.smartcity.greenpassport.feature.home.presentation.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    observeSession: ObserveHomeSessionUseCase,
    private val getLevel: GetLevelUseCase,
    private val getUpcomingEvent: GetUpcomingEventUseCase,
    private val getPendingTasks: GetPendingTasksUseCase,
) : ViewModel() {

    private val refreshRequests = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val uiState = observeHomeUiState(observeSession()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        HomeUiState(),
    )

    fun refresh() {
        refreshRequests.tryEmit(Unit)
    }

    private fun observeHomeUiState(sessions: Flow<AuthSession?>): Flow<HomeUiState> {
        return combine(sessions.filterNotNull(), refreshRequests) { session, _ -> session }
            .mapLatest { session -> loadHomeUiState(session) }
    }

    private suspend fun loadHomeUiState(session: AuthSession): HomeUiState {
        val tasks = runCatching { getPendingTasks(session.userId) }
            .onFailure { error -> Log.w(TAG, "Failed to load tasks", error) }
        return HomeUiState(
            isLoading = false,
            hasTasksError = tasks.isFailure,
            displayName = session.displayName,
            level = runCatching { getLevel(session.userId) }.getOrNull(),
            upcomingEvent = runCatching { getUpcomingEvent() }.getOrNull(),
            tasks = tasks.getOrDefault(emptyList()),
        )
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
