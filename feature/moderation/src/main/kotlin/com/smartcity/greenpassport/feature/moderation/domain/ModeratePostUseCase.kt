package com.smartcity.greenpassport.feature.moderation.domain

import com.smartcity.greenpassport.core.model.moderation.ModerationAction
import com.smartcity.greenpassport.core.model.moderation.ModerationRepository
import javax.inject.Inject

class ModeratePostUseCase @Inject constructor(
    private val moderationRepository: ModerationRepository,
) {
    suspend operator fun invoke(postId: String, action: ModerationAction) =
        moderationRepository.moderatePost(postId, action)
}
