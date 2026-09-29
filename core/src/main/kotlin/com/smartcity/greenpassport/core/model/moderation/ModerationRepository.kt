package com.smartcity.greenpassport.core.model.moderation

import com.smartcity.greenpassport.core.model.ForumPost
import com.smartcity.greenpassport.core.model.verification.TaskSubmission
import kotlinx.coroutines.flow.Flow

interface ModerationRepository {
    fun observeIsAdmin(userId: String): Flow<Boolean>
    fun observePendingSubmissions(): Flow<List<TaskSubmission>>
    fun observeFlaggedPosts(): Flow<List<ForumPost>>
    suspend fun reviewSubmission(submissionId: String, approve: Boolean, reason: String?)
    suspend fun moderatePost(postId: String, action: ModerationAction)
    suspend fun reportPost(postId: String, reporterId: String, reason: ReportReason)
}
