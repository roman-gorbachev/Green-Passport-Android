package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.CommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGroupUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    operator fun invoke(groupId: String): Flow<CommunityGroup?> = communityRepository.observeGroup(groupId)
}
