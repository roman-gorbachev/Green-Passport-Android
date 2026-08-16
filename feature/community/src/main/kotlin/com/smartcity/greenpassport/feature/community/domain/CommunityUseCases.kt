package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.ForumPost
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCommunitySessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}

class ObserveForumPostsUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    operator fun invoke(): Flow<List<ForumPost>> = communityRepository.observeForumPosts()
}

class PostToForumUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(authorId: String, text: String) = communityRepository.postToForum(authorId, text)
}

class ObserveGroupsUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    operator fun invoke(): Flow<List<CommunityGroup>> = communityRepository.observeGroups()
}

class CreateGroupUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(name: String, creatorId: String) =
        communityRepository.createGroup(name, creatorId)
}

class JoinGroupUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
) {
    suspend operator fun invoke(groupId: String, userId: String) =
        communityRepository.joinGroup(groupId, userId)
}
