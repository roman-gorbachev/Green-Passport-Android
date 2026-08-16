package com.smartcity.greenpassport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val KEY_ONBOARDING_SEEN = "onboarding_seen"
private const val SUBSCRIPTION_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) : ViewModel() {

    val onboardingSeen: StateFlow<Boolean?> = settingsStore
        .getBoolean(KEY_ONBOARDING_SEEN, false)
        .map<Boolean, Boolean?> { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MILLIS),
            initialValue = null,
        )

    fun markOnboardingSeen() {
        viewModelScope.launch { settingsStore.setBoolean(KEY_ONBOARDING_SEEN, true) }
    }
}
