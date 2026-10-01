package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.GroupMember
import javax.inject.Inject

class FetchGroupMembersUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(
        memberIds: List<String>
    ): List<GroupMember> = communityRepository.fetchMembers(memberIds)
}
