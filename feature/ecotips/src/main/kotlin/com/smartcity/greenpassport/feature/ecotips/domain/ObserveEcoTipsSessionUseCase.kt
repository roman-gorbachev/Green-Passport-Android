package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEcoTipsSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}
