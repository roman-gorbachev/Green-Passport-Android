package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(name: String, creatorId: String) =
        communityRepository.createGroup(name, creatorId)
}
