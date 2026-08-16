package com.smartcity.greenpassport.feature.auth.presentation

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val isSignedIn: Boolean = false,
)
