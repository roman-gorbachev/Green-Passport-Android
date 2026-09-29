package com.smartcity.greenpassport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import com.smartcity.greenpassport.feature.auth.domain.ObserveAuthSessionUseCase
import com.smartcity.greenpassport.feature.auth.domain.ObserveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsStore: LocalSettingsStore,
    private val observeAuthSession: ObserveAuthSessionUseCase,
    private val observeUserProfile: ObserveUserProfileUseCase,
) : ViewModel() {

    val startupState = combine(
        settingsStore.getBoolean(KEY_ONBOARDING_SEEN, false),
        observeProfileStatus(),
    ) { onboardingSeen, profileStatus ->
        when {
            !onboardingSeen -> AppStartupState.NeedsOnboarding
            profileStatus == ProfileStatus.SIGNED_OUT -> AppStartupState.NeedsAuth
            profileStatus == ProfileStatus.INCOMPLETE -> AppStartupState.NeedsProfile
            else -> AppStartupState.Ready
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MILLIS),
        initialValue = AppStartupState.Loading,
    )

    fun markOnboardingSeen() {
        viewModelScope.launch { settingsStore.setBoolean(KEY_ONBOARDING_SEEN, true) }
    }

    private fun observeProfileStatus(): Flow<ProfileStatus> =
        observeAuthSession()
            .distinctUntilChanged { old, new -> old?.userId == new?.userId }
            .flatMapLatest { session ->
                when {
                    session == null -> flowOf(ProfileStatus.SIGNED_OUT)
                    session.isAnonymous -> flowOf(ProfileStatus.COMPLETE)
                    else -> observeUserProfile(session.userId)
                        .map { profile -> if (profile == null) ProfileStatus.INCOMPLETE else ProfileStatus.COMPLETE }
                        .catch { emit(ProfileStatus.COMPLETE) }
                }
            }

    companion object {
        private const val KEY_ONBOARDING_SEEN = "onboarding_seen"
        private const val SUBSCRIPTION_TIMEOUT_MILLIS = 5_000L
    }
}
