package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.ForumPost
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveForumPostsUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    operator fun invoke(): Flow<List<ForumPost>> = communityRepository.observeForumPosts()
}
