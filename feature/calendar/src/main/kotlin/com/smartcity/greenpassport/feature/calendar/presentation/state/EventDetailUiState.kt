package com.smartcity.greenpassport.feature.calendar.presentation.state

import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.rewards.RewardFailure

data class EventDetailUiState(
    val event: EcoEvent? = null,
    val isRegistered: Boolean = false,
    val isRegistering: Boolean = false,
    val isCheckedIn: Boolean = false,
    val isCheckingIn: Boolean = false,
    val checkInPoints: Int? = null,
    val streakBonus: Int = 0,
    val checkInFailure: RewardFailure? = null,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
) {
    fun isCheckInOpen(nowEpochMillis: Long): Boolean {
        val startAt = event?.startAtEpochMillis ?: return false
        return nowEpochMillis in (startAt - CHECK_IN_OPENS_BEFORE_MILLIS)..(startAt + CHECK_IN_CLOSES_AFTER_MILLIS)
    }

    companion object {
        private const val CHECK_IN_OPENS_BEFORE_MILLIS = 2 * 60 * 60 * 1000L
        private const val CHECK_IN_CLOSES_AFTER_MILLIS = 6 * 60 * 60 * 1000L
    }
}
