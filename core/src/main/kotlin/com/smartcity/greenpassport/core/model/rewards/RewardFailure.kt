package com.smartcity.greenpassport.core.model.rewards

enum class RewardFailure {
    DAILY_LIMIT_REACHED,
    ALREADY_COMPLETED,
    INVALID_CODE,
    NOT_ENOUGH_POINTS,
    WRONG_VERIFICATION,
    QR_CODE_NOT_ACTIVE,
    QR_CODE_LIMIT_REACHED,
    REWARD_SOLD_OUT,
    NETWORK,
    UNKNOWN,
}
