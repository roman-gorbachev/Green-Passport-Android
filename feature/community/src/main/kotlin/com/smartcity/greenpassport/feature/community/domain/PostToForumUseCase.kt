package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import javax.inject.Inject

class PostToForumUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(authorId: String, text: String) = communityRepository.postToForum(authorId, text)
}
