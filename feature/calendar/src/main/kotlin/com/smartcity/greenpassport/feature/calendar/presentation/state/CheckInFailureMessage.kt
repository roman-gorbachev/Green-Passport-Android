package com.smartcity.greenpassport.feature.calendar.presentation.state

import androidx.annotation.StringRes
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.feature.calendar.R

@StringRes
fun checkInFailureMessageRes(failure: RewardFailure): Int = when (failure) {
    RewardFailure.INVALID_CODE -> R.string.event_code_does_not_match_msg
    RewardFailure.WRONG_VERIFICATION -> R.string.check_in_window_msg
    RewardFailure.ALREADY_COMPLETED -> R.string.checked_in_at_event
    RewardFailure.NETWORK -> R.string.no_internet_connection
    else -> R.string.something_went_wrong_msg
}
