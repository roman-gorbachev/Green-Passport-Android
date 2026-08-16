package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTasksSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}
