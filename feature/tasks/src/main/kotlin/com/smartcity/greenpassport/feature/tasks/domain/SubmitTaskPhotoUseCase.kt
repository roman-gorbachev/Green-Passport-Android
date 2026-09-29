package com.smartcity.greenpassport.feature.tasks.domain

import android.net.Uri
import com.smartcity.greenpassport.core.media.JpegCompressor
import com.smartcity.greenpassport.core.model.profile.UserProfileRepository
import com.smartcity.greenpassport.core.model.verification.TaskSubmission
import com.smartcity.greenpassport.core.model.verification.TaskSubmissionsRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SubmitTaskPhotoUseCase @Inject constructor(
    private val jpegCompressor: JpegCompressor,
    private val userProfileRepository: UserProfileRepository,
    private val taskSubmissionsRepository: TaskSubmissionsRepository,
) {
    suspend operator fun invoke(userId: String, taskId: String, photo: Uri): TaskSubmission {
        val jpegBytes = jpegCompressor.compress(photo)
        val profile = userProfileRepository.observeProfile(userId).catch { emit(null) }.first()
        val userName = profile?.let { "${it.firstName} ${it.lastName}".trim() }
        return taskSubmissionsRepository.submitPhoto(userId, userName, taskId, jpegBytes)
    }
}
