package com.smartcity.greenpassport.core.model.verification

import kotlinx.coroutines.flow.Flow

interface TaskSubmissionsRepository {
    fun observeUserSubmissions(userId: String): Flow<List<TaskSubmission>>
    suspend fun submitPhoto(userId: String, userName: String?, taskId: String, jpegBytes: ByteArray): TaskSubmission
    suspend fun getPhotoUrl(photoPath: String): String
}
