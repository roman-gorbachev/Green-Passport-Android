package com.smartcity.greenpassport.feature.tasks.presentation.state

import androidx.annotation.StringRes
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.feature.tasks.R

@StringRes
fun rewardFailureMessageRes(failure: RewardFailure): Int = when (failure) {
    RewardFailure.DAILY_LIMIT_REACHED -> R.string.daily_limit_reached_msg
    RewardFailure.ALREADY_COMPLETED -> R.string.task_already_completed
    RewardFailure.INVALID_CODE -> R.string.code_does_not_match_msg
    RewardFailure.WRONG_VERIFICATION -> R.string.task_needs_other_confirmation_msg
    RewardFailure.NETWORK -> R.string.no_internet_connection
    RewardFailure.NOT_ENOUGH_POINTS, RewardFailure.UNKNOWN -> R.string.something_went_wrong_msg
}
