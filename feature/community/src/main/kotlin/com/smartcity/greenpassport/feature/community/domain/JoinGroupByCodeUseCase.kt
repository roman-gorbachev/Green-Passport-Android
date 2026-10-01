package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.GroupNotFoundException
import javax.inject.Inject

class JoinGroupByCodeUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(code: String, userId: String): CommunityGroup {
        val normalizedCode = code.trim().uppercase()
        if (normalizedCode.length != INVITE_CODE_LENGTH) throw GroupNotFoundException()
        val group = communityRepository.findGroup(normalizedCode) ?: throw GroupNotFoundException()
        if (userId !in group.memberIds) communityRepository.joinGroup(group.id, userId)
        return group
    }

    companion object {
        private const val INVITE_CODE_LENGTH = 6
    }
}
