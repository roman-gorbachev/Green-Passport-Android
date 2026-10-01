package com.smartcity.greenpassport.feature.tasks.presentation.state

import androidx.annotation.StringRes
import com.smartcity.greenpassport.core.model.verification.SubmissionStatus
import com.smartcity.greenpassport.core.model.verification.TaskVerification
import com.smartcity.greenpassport.feature.tasks.R

@StringRes
fun verificationLabelRes(verification: TaskVerification): Int = when (verification) {
    TaskVerification.SELF -> R.string.no_proof_needed
    TaskVerification.PHOTO -> R.string.photo_confirmation
    TaskVerification.QR -> R.string.qr_code_on_site
}

@StringRes
fun verificationHintRes(verification: TaskVerification): Int = when (verification) {
    TaskVerification.SELF -> R.string.up_to_3_such_tasks_per_day_msg
    TaskVerification.PHOTO -> R.string.moderator_checks_photo_msg
    TaskVerification.QR -> R.string.organizer_shows_qr_code_msg
}

@StringRes
fun confirmButtonRes(verification: TaskVerification, submissionStatus: SubmissionStatus?): Int = when (verification) {
    TaskVerification.SELF -> R.string.mark_as_done
    TaskVerification.QR -> R.string.scan_qr_code
    TaskVerification.PHOTO -> if (submissionStatus == SubmissionStatus.REJECTED) {
        R.string.send_another_photo
    } else {
        R.string.attach_photo
    }
}
