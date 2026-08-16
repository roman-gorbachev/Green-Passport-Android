package com.smartcity.greenpassport.core.auth

data class AuthSession(
    val userId: String,
    val isAnonymous: Boolean,
)
