package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.profile.UserProfileRepository
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.core.moderation.TextModerator
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PostToForumUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val userProfileRepository: UserProfileRepository,
    private val textModerator: TextModerator,
) {
    suspend operator fun invoke(authorId: String, text: String) {
        if (!textModerator.isAllowed(text)) throw ContentRejectedException()
        val profile = userProfileRepository.observeProfile(authorId).catch { emit(null) }.first()
        communityRepository.postToForum(
            authorId = authorId,
            authorName = profile?.let { "${it.firstName} ${it.lastName}".trim() },
            authorAvatar = profile?.avatar,
            text = text,
        )
    }
}
