package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.verification.SubmissionStatus
import com.smartcity.greenpassport.core.model.verification.TaskSubmission
import com.smartcity.greenpassport.core.model.verification.TaskSubmissionsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

private const val FIELD_TASK_ID = "taskId"
private const val FIELD_USER_ID = "userId"
private const val FIELD_USER_NAME = "userName"
private const val FIELD_PHOTO_PATH = "photoPath"
private const val FIELD_STATUS = "status"
private const val FIELD_REJECTION_REASON = "rejectionReason"
private const val FIELD_CREATED_AT = "createdAtEpochMillis"
private const val SUBMISSIONS_STORAGE_FOLDER = "greenpassport/submissions"
private const val JPEG_CONTENT_TYPE = "image/jpeg"

class FirebaseTaskSubmissionsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : TaskSubmissionsRepository {

    override fun observeUserSubmissions(userId: String): Flow<List<TaskSubmission>> = callbackFlow {
        val registration = FirestoreCollections.taskSubmissions(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                } else {
                    trySend(snapshot?.documents.orEmpty().mapNotNull { it.toTaskSubmission() })
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun submitPhoto(
        userId: String,
        userName: String?,
        taskId: String,
        jpegBytes: ByteArray,
    ): TaskSubmission {
        val photoPath = "$SUBMISSIONS_STORAGE_FOLDER/$userId/${UUID.randomUUID()}.jpg"
        storage.reference.child(photoPath)
            .putBytes(jpegBytes, storageMetadata { contentType = JPEG_CONTENT_TYPE })
            .await()
        val submission = TaskSubmission(
            id = "${userId}_$taskId",
            taskId = taskId,
            userId = userId,
            userName = userName,
            photoPath = photoPath,
            status = SubmissionStatus.PENDING,
            rejectionReason = null,
            createdAtEpochMillis = System.currentTimeMillis(),
        )
        FirestoreCollections.taskSubmissions(firestore).document(submission.id).set(
            mapOf(
                FIELD_TASK_ID to submission.taskId,
                FIELD_USER_ID to submission.userId,
                FIELD_USER_NAME to submission.userName,
                FIELD_PHOTO_PATH to submission.photoPath,
                FIELD_STATUS to submission.status.name,
                FIELD_REJECTION_REASON to null,
                FIELD_CREATED_AT to submission.createdAtEpochMillis,
            ),
        ).await()
        return submission
    }

    override suspend fun getPhotoUrl(photoPath: String): String =
        storage.reference.child(photoPath).downloadUrl.await().toString()
}

internal fun DocumentSnapshot.toTaskSubmission(): TaskSubmission? {
    val taskId = getString(FIELD_TASK_ID) ?: return null
    val userId = getString(FIELD_USER_ID) ?: return null
    val photoPath = getString(FIELD_PHOTO_PATH) ?: return null
    val status = SubmissionStatus.entries.firstOrNull { it.name == getString(FIELD_STATUS) } ?: return null
    return TaskSubmission(
        id = id,
        taskId = taskId,
        userId = userId,
        userName = getString(FIELD_USER_NAME),
        photoPath = photoPath,
        status = status,
        rejectionReason = getString(FIELD_REJECTION_REASON),
        createdAtEpochMillis = getLong(FIELD_CREATED_AT) ?: 0L,
    )
}
