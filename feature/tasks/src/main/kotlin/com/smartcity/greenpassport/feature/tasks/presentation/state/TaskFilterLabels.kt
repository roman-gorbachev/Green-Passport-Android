package com.smartcity.greenpassport.feature.tasks.presentation.state

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.core.model.verification.TaskVerification
import com.smartcity.greenpassport.feature.tasks.R

@StringRes
fun statusFilterLabelRes(status: TaskStatusFilter): Int = when (status) {
    TaskStatusFilter.AVAILABLE -> R.string.available_tasks
    TaskStatusFilter.UNDER_REVIEW -> R.string.under_review
    TaskStatusFilter.COMPLETED -> R.string.completed_tasks
    TaskStatusFilter.ALL -> R.string.tasks_filter_all
}

fun verificationIcon(verification: TaskVerification): ImageVector = when (verification) {
    TaskVerification.SELF -> Icons.Filled.PanTool
    TaskVerification.PHOTO -> Icons.Filled.PhotoCamera
    TaskVerification.QR -> Icons.Filled.QrCodeScanner
}
