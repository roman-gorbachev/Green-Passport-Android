package com.smartcity.greenpassport.feature.auth.presentation.auth.state

import com.smartcity.greenpassport.core.auth.AuthFailure

data class AuthUiState(
    val mode: AuthMode = AuthMode.SIGN_IN,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isEmailInvalid: Boolean = false,
    val isPasswordTooShort: Boolean = false,
    val isPasswordMismatch: Boolean = false,
    val failure: AuthFailure? = null,
)
