package com.smartcity.greenpassport.feature.shop.presentation.state

import androidx.annotation.StringRes
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.feature.shop.R

@StringRes
fun purchaseFailureMessageRes(failure: RewardFailure): Int = when (failure) {
    RewardFailure.NOT_ENOUGH_POINTS -> R.string.shop_insufficient_points
    RewardFailure.REWARD_SOLD_OUT -> R.string.reward_sold_out
    RewardFailure.NETWORK -> R.string.no_internet_connection
    else -> R.string.something_went_wrong_msg
}
