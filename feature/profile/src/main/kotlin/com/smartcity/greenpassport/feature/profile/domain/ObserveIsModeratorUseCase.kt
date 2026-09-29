package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.moderation.ModerationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsModeratorUseCase @Inject constructor(
    private val moderationRepository: ModerationRepository,
) {
    operator fun invoke(userId: String): Flow<Boolean> = moderationRepository.observeIsAdmin(userId)
}
