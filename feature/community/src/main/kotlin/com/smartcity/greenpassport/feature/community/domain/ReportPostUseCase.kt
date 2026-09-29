package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.moderation.ModerationRepository
import com.smartcity.greenpassport.core.model.moderation.ReportReason
import javax.inject.Inject

class ReportPostUseCase @Inject constructor(
    private val moderationRepository: ModerationRepository,
) {
    suspend operator fun invoke(postId: String, reporterId: String, reason: ReportReason) =
        moderationRepository.reportPost(postId, reporterId, reason)
}
