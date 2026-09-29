package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.core.moderation.TextModerator
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val textModerator: TextModerator,
) {
    suspend operator fun invoke(name: String, creatorId: String) {
        if (!textModerator.isAllowed(name)) throw ContentRejectedException()
        communityRepository.createGroup(name, creatorId)
    }
}
