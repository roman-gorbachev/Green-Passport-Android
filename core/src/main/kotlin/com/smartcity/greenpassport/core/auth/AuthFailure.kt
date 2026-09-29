package com.smartcity.greenpassport.core.auth

enum class AuthFailure {
    INVALID_CREDENTIALS,
    INVALID_EMAIL,
    EMAIL_ALREADY_IN_USE,
    WEAK_PASSWORD,
    NETWORK,
    TOO_MANY_REQUESTS,
    SIGN_IN_METHOD_DISABLED,
    UNKNOWN,
}
