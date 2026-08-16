package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import com.smartcity.greenpassport.core.model.Experience
import com.smartcity.greenpassport.core.model.PointsBalance
import com.smartcity.greenpassport.core.model.PointsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

class ObserveProfileSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() = authRepository.signOut()
}

class GetPointsBalanceUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String): PointsBalance = pointsRepository.getBalance(userId)
}

class GetExperienceUseCase @Inject constructor(
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String): Experience = pointsRepository.getExperience(userId)
}

class ObserveNotificationsEnabledUseCase @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) {
    operator fun invoke(): Flow<Boolean> = settingsStore.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
}

class SetNotificationsEnabledUseCase @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) {
    suspend operator fun invoke(enabled: Boolean) =
        settingsStore.setBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
}
