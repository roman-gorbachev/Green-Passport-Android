package com.smartcity.greenpassport.feature.auth.presentation.state

import com.smartcity.greenpassport.core.auth.AuthFailure

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isEmailInvalid: Boolean = false,
    val isPasswordTooShort: Boolean = false,
    val failure: AuthFailure? = null,
)
