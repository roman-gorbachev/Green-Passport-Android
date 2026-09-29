package com.smartcity.greenpassport.feature.moderation.domain

import com.smartcity.greenpassport.core.model.moderation.ModerationRepository
import javax.inject.Inject

class ReviewSubmissionUseCase @Inject constructor(
    private val moderationRepository: ModerationRepository,
) {
    suspend operator fun invoke(submissionId: String, approve: Boolean, reason: String?) =
        moderationRepository.reviewSubmission(submissionId, approve, reason)
}
