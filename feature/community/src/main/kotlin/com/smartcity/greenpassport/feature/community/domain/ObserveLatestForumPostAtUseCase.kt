package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLatestForumPostAtUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    operator fun invoke(): Flow<Long?> = communityRepository.observeLatestForumPostAt()
}
