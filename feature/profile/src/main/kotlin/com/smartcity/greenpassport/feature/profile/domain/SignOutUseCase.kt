package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.messaging.repository.INotificationsRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationsRepository: INotificationsRepository,
) {
    suspend operator fun invoke() {
        runCatching { notificationsRepository.unregisterToken() }
        authRepository.signOut()
    }
}
