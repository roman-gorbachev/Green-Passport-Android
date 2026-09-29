package com.smartcity.greenpassport.feature.moderation.domain

import com.smartcity.greenpassport.core.model.verification.TaskSubmission

data class SubmissionItem(
    val submission: TaskSubmission,
    val taskTitle: String?,
    val photoUrl: String?,
)
