package com.smartcity.greenpassport.core.auth

class AuthFailureException(
    val failure: AuthFailure,
    cause: Throwable,
) : Exception(cause)
