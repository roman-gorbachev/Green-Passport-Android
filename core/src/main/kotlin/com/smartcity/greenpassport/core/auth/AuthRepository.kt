package com.smartcity.greenpassport.core.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<AuthSession?>

    suspend fun signInAnonymously(): AuthSession
    suspend fun signInWithEmail(email: String, password: String): AuthSession
    suspend fun registerWithEmail(email: String, password: String): AuthSession
    suspend fun signOut()
}
