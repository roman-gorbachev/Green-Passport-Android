package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import javax.inject.Inject

class JoinGroupUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(groupId: String, userId: String) =
        communityRepository.joinGroup(groupId, userId)
}
