package com.smartcity.greenpassport.feature.auth.domain

import com.smartcity.greenpassport.core.model.profile.UserProfile
import com.smartcity.greenpassport.core.model.profile.UserProfileRepository
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.core.moderation.TextModerator
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val textModerator: TextModerator,
) {
    suspend operator fun invoke(profile: UserProfile) {
        if (!textModerator.isAllowed(profile.firstName) || !textModerator.isAllowed(profile.lastName)) {
            throw ContentRejectedException()
        }
        userProfileRepository.saveProfile(profile)
    }
}
