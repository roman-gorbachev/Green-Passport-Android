package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.functions.CloudFunctionNames
import com.smartcity.greenpassport.core.datasource.remote.functions.toRewardFailure
import com.smartcity.greenpassport.core.model.ForumPost
import com.smartcity.greenpassport.core.model.moderation.ModerationAction
import com.smartcity.greenpassport.core.model.moderation.ModerationRepository
import com.smartcity.greenpassport.core.model.moderation.ReportReason
import com.smartcity.greenpassport.core.model.rewards.RewardFailureException
import com.smartcity.greenpassport.core.model.verification.SubmissionStatus
import com.smartcity.greenpassport.core.model.verification.TaskSubmission
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_STATUS = "status"
private const val FIELD_HIDDEN = "hidden"
private const val FIELD_REPORT_COUNT = "reportCount"
private const val FIELD_POST_ID = "postId"
private const val FIELD_REPORTER_ID = "reporterId"
private const val FIELD_REASON = "reason"
private const val FIELD_CREATED_AT = "createdAtEpochMillis"
private const val PARAM_SUBMISSION_ID = "submissionId"
private const val PARAM_APPROVE = "approve"
private const val PARAM_REASON = "reason"
private const val PARAM_POST_ID = "postId"
private const val PARAM_ACTION = "action"

class FirebaseModerationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions,
) : ModerationRepository {

    override fun observeIsAdmin(userId: String): Flow<Boolean> = callbackFlow {
        val registration = FirestoreCollections.admins(firestore).document(userId)
            .addSnapshotListener { snapshot, error ->
                trySend(error == null && snapshot?.exists() == true)
            }
        awaitClose { registration.remove() }
    }

    override fun observePendingSubmissions(): Flow<List<TaskSubmission>> = callbackFlow {
        val registration = FirestoreCollections.taskSubmissions(firestore)
            .whereEqualTo(FIELD_STATUS, SubmissionStatus.PENDING.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                } else {
                    val submissions = snapshot?.documents.orEmpty()
                        .mapNotNull { it.toTaskSubmission() }
                        .sortedBy { it.createdAtEpochMillis }
                    trySend(submissions)
                }
            }
        awaitClose { registration.remove() }
    }

    override fun observeFlaggedPosts(): Flow<List<ForumPost>> {
        val posts = FirestoreCollections.posts(firestore)
        val hidden = observePosts(posts.whereEqualTo(FIELD_HIDDEN, true))
        val reported = observePosts(posts.whereGreaterThan(FIELD_REPORT_COUNT, 0))
        return combine(hidden, reported) { hiddenPosts, reportedPosts ->
            (hiddenPosts + reportedPosts)
                .distinctBy { it.id }
                .sortedByDescending { it.createdAtEpochMillis }
        }.catch { emit(emptyList()) }
    }

    override suspend fun reviewSubmission(submissionId: String, approve: Boolean, reason: String?) {
        val data = buildMap<String, Any> {
            put(PARAM_SUBMISSION_ID, submissionId)
            put(PARAM_APPROVE, approve)
            if (reason != null) put(PARAM_REASON, reason)
        }
        callFunction(CloudFunctionNames.REVIEW_SUBMISSION, data)
    }

    override suspend fun moderatePost(postId: String, action: ModerationAction) {
        callFunction(
            CloudFunctionNames.MODERATE_CONTENT,
            mapOf(PARAM_POST_ID to postId, PARAM_ACTION to action.wireName),
        )
    }

    override suspend fun reportPost(postId: String, reporterId: String, reason: ReportReason) {
        FirestoreCollections.reports(firestore).document("${postId}_$reporterId").set(
            mapOf(
                FIELD_POST_ID to postId,
                FIELD_REPORTER_ID to reporterId,
                FIELD_REASON to reason.name,
                FIELD_CREATED_AT to System.currentTimeMillis(),
            ),
        ).await()
    }

    private fun observePosts(query: Query): Flow<List<ForumPost>> = callbackFlow {
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
            } else {
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.toForumPost() })
            }
        }
        awaitClose { registration.remove() }
    }

    private suspend fun callFunction(name: String, data: Map<String, Any>) {
        runCatching { functions.getHttpsCallable(name).call(data).await() }
            .onFailure { error ->
                if (error is CancellationException) throw error
                throw RewardFailureException(error.toRewardFailure(), error)
            }
    }
}
