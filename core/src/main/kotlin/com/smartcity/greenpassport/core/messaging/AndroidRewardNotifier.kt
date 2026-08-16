package com.smartcity.greenpassport.core.messaging

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.smartcity.greenpassport.core.R
import com.smartcity.greenpassport.core.local.NotificationLogRepository
import com.smartcity.greenpassport.core.model.PointsEarnReason
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val REWARD_NOTIFICATION_ID_BASE = 10_000

class AndroidRewardNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationLogRepository: NotificationLogRepository,
) : RewardNotifier {

    override suspend fun notifyReward(reason: PointsEarnReason, points: Int, xp: Int) {
        val title = context.getString(rewardReasonTitleRes(reason))
        val body = context.getString(R.string.reward_notification_body_format, points, xp)

        notificationLogRepository.log(title = title, body = body)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, NotificationChannels.REWARDS_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(REWARD_NOTIFICATION_ID_BASE + reason.ordinal, notification)
    }
}

private fun rewardReasonTitleRes(reason: PointsEarnReason): Int = when (reason) {
    PointsEarnReason.TASK_COMPLETED -> R.string.reward_reason_task_completed
    PointsEarnReason.GAME_PLAYED -> R.string.reward_reason_game_played
    PointsEarnReason.ARTICLE_READ -> R.string.reward_reason_article_read
    PointsEarnReason.EVENT_ATTENDED -> R.string.reward_reason_event_attended
    PointsEarnReason.FEEDBACK_SUBMITTED -> R.string.reward_reason_feedback_submitted
}
