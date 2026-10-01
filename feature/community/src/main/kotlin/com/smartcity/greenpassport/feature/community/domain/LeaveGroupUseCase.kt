package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import javax.inject.Inject

class LeaveGroupUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(groupId: String, userId: String) {
        communityRepository.leaveGroup(groupId, userId)
    }
}
