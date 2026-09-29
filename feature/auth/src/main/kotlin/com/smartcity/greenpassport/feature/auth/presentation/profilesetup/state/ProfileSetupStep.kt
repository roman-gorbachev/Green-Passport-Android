package com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state

enum class ProfileSetupStep {
    NAME,
    CITY,
    INTERESTS,
    AVATAR,
    ;

    companion object {
        const val TOTAL_STEPS = 5
    }
}
