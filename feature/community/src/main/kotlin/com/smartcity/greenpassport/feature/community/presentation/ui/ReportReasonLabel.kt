package com.smartcity.greenpassport.feature.community.presentation.ui

import com.smartcity.greenpassport.core.model.moderation.ReportReason
import com.smartcity.greenpassport.feature.community.R

internal fun reportReasonLabelRes(reason: ReportReason): Int = when (reason) {
    ReportReason.OFFENSIVE -> R.string.insults_or_obscenity
    ReportReason.SPAM -> R.string.spam
    ReportReason.INAPPROPRIATE_IMAGE -> R.string.inappropriate_content
    ReportReason.OTHER -> R.string.other
}
