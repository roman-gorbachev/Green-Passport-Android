package com.smartcity.greenpassport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import com.smartcity.greenpassport.feature.auth.domain.ObserveAuthSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val KEY_ONBOARDING_SEEN = "onboarding_seen"
private const val SUBSCRIPTION_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsStore: LocalSettingsStore,
    observeAuthSession: ObserveAuthSessionUseCase,
) : ViewModel() {

    val startupState: StateFlow<AppStartupState> = combine(
        settingsStore.getBoolean(KEY_ONBOARDING_SEEN, false),
        observeAuthSession(),
    ) { onboardingSeen, authSession ->
        when {
            !onboardingSeen -> AppStartupState.NeedsOnboarding
            authSession == null -> AppStartupState.NeedsAuth
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
}
