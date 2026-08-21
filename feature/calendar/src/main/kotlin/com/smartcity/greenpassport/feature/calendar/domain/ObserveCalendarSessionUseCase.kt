package com.smartcity.greenpassport.feature.calendar.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCalendarSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}
