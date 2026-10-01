package com.smartcity.greenpassport.feature.home.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.Level
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import com.smartcity.greenpassport.core.model.profile.UserProfile
import com.smartcity.greenpassport.feature.home.domain.ObserveHomeSessionUseCase
import com.smartcity.greenpassport.feature.home.domain.ObserveLevelUseCase
import com.smartcity.greenpassport.feature.home.domain.ObservePendingTasksUseCase
import com.smartcity.greenpassport.feature.home.domain.ObservePointsBalanceUseCase
import com.smartcity.greenpassport.feature.home.domain.ObserveStreakUseCase
import com.smartcity.greenpassport.feature.home.domain.ObserveUpcomingEventUseCase
import com.smartcity.greenpassport.feature.home.domain.ObserveUserProfileUseCase
import com.smartcity.greenpassport.feature.home.presentation.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    observeSession: ObserveHomeSessionUseCase,
    private val observeLevel: ObserveLevelUseCase,
    private val observeUpcomingEvent: ObserveUpcomingEventUseCase,
    private val observePendingTasks: ObservePendingTasksUseCase,
    private val observePointsBalance: ObservePointsBalanceUseCase,
    private val observeUserProfile: ObserveUserProfileUseCase,
    private val observeStreak: ObserveStreakUseCase,
) : ViewModel() {

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val uiState = observeHomeUiState(observeSession()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        HomeUiState(),
    )

    fun refresh() {
        retryRequests.tryEmit(Unit)
    }

    private fun observeHomeUiState(sessions: Flow<AuthSession?>): Flow<HomeUiState> {
        return combine(sessions.filterNotNull(), retryRequests.onStart { emit(Unit) }) { session, _ -> session }
            .flatMapLatest { session -> observeSessionUiState(session) }
    }

    private fun observeSessionUiState(session: AuthSession): Flow<HomeUiState> {
        val profile = observeUserProfile(session.userId)
            .onStart { emit(null) }
            .catch { emit(null) }
            .distinctUntilChanged()
        val tasks = profile.flatMapLatest { currentProfile ->
            observePendingTasks(session.userId, currentProfile)
                .map<List<Task>, TasksLoad> { TasksLoad.Loaded(it) }
                .catch { error ->
                    Log.w(TAG, "Failed to observe tasks", error)
                    emit(TasksLoad.Failed)
                }
        }
        val points = observePointsBalance(session.userId)
            .map<_, Int?> { it.availablePoints }
            .onStart { emit(null) }
            .catch { emit(null) }
        val level = observeLevel(session.userId)
            .map<Level, Level?> { it }
            .onStart { emit(null) }
            .catch { emit(null) }
        val event = observeUpcomingEvent()
            .onStart { emit(null) }
            .catch { emit(null) }
        val streakDays = observeStreak(session.userId)
            .map { it?.currentCount(System.currentTimeMillis()) ?: 0 }
            .onStart { emit(0) }
            .catch { emit(0) }
        val progress = combine(points, level, streakDays) { currentPoints, currentLevel, days ->
            HomeProgress(points = currentPoints, level = currentLevel, streakDays = days)
        }
        return combine(profile, tasks, progress, event) { currentProfile, tasksLoad, currentProgress, upcoming ->
            homeUiState(session, currentProfile, tasksLoad, currentProgress, upcoming)
        }
    }

    private fun homeUiState(
        session: AuthSession,
        profile: UserProfile?,
        tasks: TasksLoad,
        progress: HomeProgress,
        upcomingEvent: EcoEvent?,
    ) = HomeUiState(
        isLoading = false,
        hasTasksError = tasks is TasksLoad.Failed,
        displayName = profile?.firstName ?: session.displayName,
        avatar = profile?.avatar ?: AvatarStyle.LIME,
        points = progress.points ?: 0,
        level = progress.level,
        streakDays = progress.streakDays,
        upcomingEvent = upcomingEvent,
        tasks = (tasks as? TasksLoad.Loaded)?.tasks.orEmpty(),
    )

    private data class HomeProgress(
        val points: Int?,
        val level: Level?,
        val streakDays: Int,
    )

    private sealed interface TasksLoad {
        data class Loaded(val tasks: List<Task>) : TasksLoad

        data object Failed : TasksLoad
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
