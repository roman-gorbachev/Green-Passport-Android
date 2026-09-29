package com.smartcity.greenpassport.feature.auth.presentation.auth.state

import androidx.annotation.StringRes
import com.smartcity.greenpassport.core.auth.AuthFailure
import com.smartcity.greenpassport.feature.auth.R

@StringRes
fun authFailureMessageRes(failure: AuthFailure): Int = when (failure) {
    AuthFailure.INVALID_CREDENTIALS -> R.string.wrong_email_or_password
    AuthFailure.INVALID_EMAIL -> R.string.enter_valid_email
    AuthFailure.EMAIL_ALREADY_IN_USE -> R.string.email_already_registered
    AuthFailure.WEAK_PASSWORD -> R.string.password_too_weak
    AuthFailure.NETWORK -> R.string.no_internet_connection
    AuthFailure.TOO_MANY_REQUESTS -> R.string.too_many_attempts_msg
    AuthFailure.SIGN_IN_METHOD_DISABLED -> R.string.sign_in_method_disabled_msg
    AuthFailure.GOOGLE_CANCELLED, AuthFailure.UNKNOWN -> R.string.could_not_sign_in_msg
    AuthFailure.GOOGLE_UNAVAILABLE -> R.string.google_sign_in_unavailable_msg
}
