package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import com.smartcity.greenpassport.core.local.NotificationLogEntry
import com.smartcity.greenpassport.core.local.NotificationLogRepository
import com.smartcity.greenpassport.core.model.Achievement
import com.smartcity.greenpassport.core.model.AchievementsRepository
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import com.smartcity.greenpassport.core.model.Experience
import com.smartcity.greenpassport.core.model.FavoritesRepository
import com.smartcity.greenpassport.core.model.HistoryEntry
import com.smartcity.greenpassport.core.model.HistoryRepository
import com.smartcity.greenpassport.core.model.PointsBalance
import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TasksRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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

class GetAchievementsUseCase @Inject constructor(
    private val achievementsRepository: AchievementsRepository,
) {
    suspend operator fun invoke(userId: String): List<Achievement> = achievementsRepository.getAchievements(userId)
}

class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(userId: String): List<HistoryEntry> = historyRepository.getHistory(userId)
}

class ObserveNotificationLogUseCase @Inject constructor(
    private val notificationLogRepository: NotificationLogRepository,
) {
    operator fun invoke(): Flow<List<NotificationLogEntry>> = notificationLogRepository.observeAll()
}

class GetFavoriteTasksUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke(userId: String): List<Task> {
        val favoriteIds = favoritesRepository.observeFavoriteTaskIds(userId).first()
        return tasksRepository.getTasks().filter { favoriteIds.contains(it.id) }
    }
}

class GetBookmarkedTipsUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val ecoTipsRepository: EcoTipsRepository,
) {
    suspend operator fun invoke(userId: String): List<EcoTip> {
        val bookmarkedIds = favoritesRepository.observeBookmarkedTipIds(userId).first()
        return ecoTipsRepository.getTips().filter { bookmarkedIds.contains(it.id) }
    }
}
