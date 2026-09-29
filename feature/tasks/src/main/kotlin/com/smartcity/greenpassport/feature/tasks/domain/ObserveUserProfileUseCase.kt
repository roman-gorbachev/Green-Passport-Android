package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.profile.UserProfile
import com.smartcity.greenpassport.core.model.profile.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
) {
    operator fun invoke(userId: String): Flow<UserProfile?> = userProfileRepository.observeProfile(userId)
}
