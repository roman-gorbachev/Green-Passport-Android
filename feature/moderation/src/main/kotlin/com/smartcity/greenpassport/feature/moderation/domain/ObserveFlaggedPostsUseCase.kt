package com.smartcity.greenpassport.feature.moderation.domain

import com.smartcity.greenpassport.core.model.ForumPost
import com.smartcity.greenpassport.core.model.moderation.ModerationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFlaggedPostsUseCase @Inject constructor(
    private val moderationRepository: ModerationRepository,
) {
    operator fun invoke(): Flow<List<ForumPost>> = moderationRepository.observeFlaggedPosts()
}
