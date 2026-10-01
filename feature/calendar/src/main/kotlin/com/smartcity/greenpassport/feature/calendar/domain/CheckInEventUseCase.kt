package com.smartcity.greenpassport.feature.calendar.domain

import android.content.Context
import com.smartcity.greenpassport.core.model.rewards.RewardResult
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import com.smartcity.greenpassport.core.scanner.QrCodeScanner
import javax.inject.Inject

class CheckInEventUseCase @Inject constructor(
    private val qrCodeScanner: QrCodeScanner,
    private val rewardsRepository: RewardsRepository,
) {
    suspend operator fun invoke(activityContext: Context): RewardResult? {
        val code = qrCodeScanner.scan(activityContext) ?: return null
        return rewardsRepository.checkInEvent(code)
    }
}
