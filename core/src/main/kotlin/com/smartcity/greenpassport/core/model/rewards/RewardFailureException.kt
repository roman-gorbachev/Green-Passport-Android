package com.smartcity.greenpassport.core.model.rewards

class RewardFailureException(
    val failure: RewardFailure,
    cause: Throwable,
) : Exception(cause)
