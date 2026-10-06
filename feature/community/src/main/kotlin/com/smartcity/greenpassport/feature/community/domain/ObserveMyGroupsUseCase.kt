package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.CommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMyGroupsUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    operator fun invoke(userId: String): Flow<List<CommunityGroup>> = communityRepository.observeMyGroups(userId)
}
