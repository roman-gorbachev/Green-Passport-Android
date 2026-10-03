package com.smartcity.greenpassport.feature.profile.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.messaging.repository.INotificationsRepository
import com.smartcity.greenpassport.core.model.settings.AppTheme
import com.smartcity.greenpassport.feature.profile.domain.ObserveAppThemeUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveIsModeratorUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveNotificationsEnabledUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveProfileProgressUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveProfileSessionUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveUserProfileUseCase
import com.smartcity.greenpassport.feature.profile.domain.SetAppThemeUseCase
import com.smartcity.greenpassport.feature.profile.domain.SetNotificationsEnabledUseCase
import com.smartcity.greenpassport.feature.profile.domain.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeSession: ObserveProfileSessionUseCase,
    private val observeProfileProgress: ObserveProfileProgressUseCase,
    private val observeUserProfile: ObserveUserProfileUseCase,
    private val observeIsModerator: ObserveIsModeratorUseCase,
    private val observeNotificationsEnabled: ObserveNotificationsEnabledUseCase,
    private val observeAppTheme: ObserveAppThemeUseCase,
    private val setNotificationsEnabled: SetNotificationsEnabledUseCase,
    private val setAppTheme: SetAppThemeUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val notificationsRepository: INotificationsRepository,
) : ViewModel() {

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val uiState = observeProfileUiState(observeSession()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        ProfileUiState(),
    )

    fun refresh() {
        retryRequests.tryEmit(Unit)
    }

    fun hasNotificationPermission(): Boolean = notificationsRepository.hasNotificationPermission()

    fun onNotificationsToggle(enabled: Boolean) {
        viewModelScope.launch {
            runCatching { setNotificationsEnabled(enabled) }
                .onFailure { error -> Log.w(TAG, "Failed to save notifications switch", error) }
        }
    }

    fun onThemeSelected(theme: AppTheme) {
        viewModelScope.launch {
            runCatching { setAppTheme(theme) }
                .onFailure { error -> Log.w(TAG, "Failed to save theme", error) }
        }
    }

    fun onSignOut() {
        viewModelScope.launch {
            runCatching { signOutUseCase() }
                .onFailure { error -> Log.w(TAG, "Failed to sign out", error) }
        }
    }

    private fun observeProfileUiState(sessions: Flow<AuthSession?>): Flow<ProfileUiState> {
        val settings = combine(observeNotificationsEnabled(), observeAppTheme()) { enabled, theme -> enabled to theme }
            .onStart { reconcileNotificationsPermission() }
        val account = combine(sessions, retryRequests.onStart { emit(Unit) }) { session, _ -> session }
            .flatMapLatest { session ->
                if (session == null) {
                    flowOf(
                        ProfileUiState(isLoading = false)
                    )
                } else {
                    observeAccount(session)
                }
            }
        return combine(account, settings) { state, (enabled, theme) ->
            state.copy(notificationsEnabled = enabled, theme = theme)
        }
    }

    private suspend fun reconcileNotificationsPermission() {
        if (!notificationsRepository.hasNotificationPermission()) {
            runCatching { setNotificationsEnabled(false) }
        }
    }

    private fun observeAccount(session: AuthSession): Flow<ProfileUiState> {
        val profile = observeUserProfile(session.userId).onStart { emit(null) }.catch { emit(null) }
        val isModerator = observeIsModerator(session.userId).onStart { emit(false) }.catch { emit(false) }
        val progress = observeProfileProgress(session.userId)
        return combine(profile, isModerator, progress) { currentProfile, moderator, currentProgress ->
            ProfileUiState(
                userId = session.userId,
                email = session.email,
                isAnonymous = session.isAnonymous,
                profile = currentProfile,
                isModerator = moderator,
                level = currentProgress.level,
                points = currentProgress.points,
                isLoading = false,
            )
        }.catch { error ->
            Log.w(TAG, "Failed to observe profile", error)
            emit(ProfileUiState(isLoading = false, hasError = true))
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
