package com.smartcity.greenpassport

sealed interface AppStartupState {
    data object Loading : AppStartupState
    data object NeedsOnboarding : AppStartupState
    data object NeedsAuth : AppStartupState
    data object Ready : AppStartupState
}
