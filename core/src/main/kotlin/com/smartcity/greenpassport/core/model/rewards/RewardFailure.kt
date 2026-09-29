package com.smartcity.greenpassport.core.model.rewards

enum class RewardFailure {
    DAILY_LIMIT_REACHED,
    ALREADY_COMPLETED,
    INVALID_CODE,
    NOT_ENOUGH_POINTS,
    WRONG_VERIFICATION,
    NETWORK,
    UNKNOWN,
}
