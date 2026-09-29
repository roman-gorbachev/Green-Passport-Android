package com.smartcity.greenpassport.feature.auth.presentation.profilesetup.ui

import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.profile.AvatarStyle

class ProfileStepActions(
    val onFirstNameChange: (String) -> Unit,
    val onLastNameChange: (String) -> Unit,
    val onCitySelected: (String) -> Unit,
    val onInterestToggled: (TaskCategory) -> Unit,
    val onAvatarSelected: (AvatarStyle) -> Unit,
    val onNext: () -> Unit,
)
