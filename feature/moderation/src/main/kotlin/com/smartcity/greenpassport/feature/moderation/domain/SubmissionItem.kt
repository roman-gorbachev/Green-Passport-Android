package com.smartcity.greenpassport.feature.moderation.domain

import com.smartcity.greenpassport.core.model.LocalizedText
import com.smartcity.greenpassport.core.model.verification.TaskSubmission

data class SubmissionItem(
    val submission: TaskSubmission,
    val taskTitle: LocalizedText?,
    val photoUrl: String?,
)
