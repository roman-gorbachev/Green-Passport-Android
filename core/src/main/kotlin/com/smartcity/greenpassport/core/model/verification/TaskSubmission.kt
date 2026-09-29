package com.smartcity.greenpassport.core.model.verification

data class TaskSubmission(
    val id: String,
    val taskId: String,
    val userId: String,
    val userName: String?,
    val photoPath: String,
    val status: SubmissionStatus,
    val rejectionReason: String?,
    val createdAtEpochMillis: Long,
)
