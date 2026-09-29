package com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state

import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.profile.AvatarStyle

data class ProfileSetupUiState(
    val isPrefilled: Boolean = false,
    val userId: String? = null,
    val step: ProfileSetupStep = ProfileSetupStep.NAME,
    val firstName: String = "",
    val lastName: String = "",
    val firstNameError: NameError? = null,
    val lastNameError: NameError? = null,
    val city: String? = null,
    val isCityMissing: Boolean = false,
    val interests: Set<TaskCategory> = emptySet(),
    val isInterestsMissing: Boolean = false,
    val avatar: AvatarStyle = AvatarStyle.LIME,
    val isSaving: Boolean = false,
    val hasSaveError: Boolean = false,
    val isSaved: Boolean = false,
)
