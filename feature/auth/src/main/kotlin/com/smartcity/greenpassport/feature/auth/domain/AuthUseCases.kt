package com.smartcity.greenpassport.feature.auth.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAuthSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}

class SignInAnonymouslyUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): AuthSession = authRepository.signInAnonymously()
}

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AuthSession =
        authRepository.signInWithEmail(email, password)
}

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AuthSession =
        authRepository.registerWithEmail(email, password)
}
