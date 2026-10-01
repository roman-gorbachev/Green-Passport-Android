package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.messaging.helpers.ReminderScheduler
import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import javax.inject.Inject

class PurchaseRewardUseCase @Inject constructor(
    private val rewardsRepository: RewardsRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend operator fun invoke(reward: Reward): Coupon {
        val coupon = rewardsRepository.redeemReward(reward.id)
        coupon.expiresAtEpochMillis?.let { expiresAt ->
            reminderScheduler.scheduleCouponReminder(coupon.id, reward.title, expiresAt)
        }
        return coupon
    }
}
