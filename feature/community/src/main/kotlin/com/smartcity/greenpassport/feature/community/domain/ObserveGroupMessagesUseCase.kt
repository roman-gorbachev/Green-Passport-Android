package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.GroupMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGroupMessagesUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    operator fun invoke(groupId: String): Flow<List<GroupMessage>> = communityRepository.observeMessages(groupId)
}
