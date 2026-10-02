package com.smartcity.greenpassport.core.datasource.remote.functions

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.smartcity.greenpassport.core.messaging.helpers.RewardNotifier
import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.PointsEarnReason
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.core.model.rewards.RewardFailureException
import com.smartcity.greenpassport.core.model.rewards.RewardResult
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val PARAM_TASK_ID = "taskId"
private const val PARAM_CODE = "code"
private const val PARAM_TIP_ID = "tipId"
private const val PARAM_GAME_ID = "gameId"
private const val PARAM_SCORE = "score"
private const val PARAM_REWARD_ID = "rewardId"
private const val RESULT_POINTS = "points"
private const val RESULT_XP = "xp"
private const val RESULT_STREAK_BONUS = "streakBonus"
private const val RESULT_COUPON_ID = "couponId"
private const val RESULT_REDEEMED_AT = "redeemedAtEpochMillis"
private const val RESULT_EXPIRES_AT = "expiresAtEpochMillis"
private const val RESULT_CODE = "code"
private const val RESULT_USED_AT = "usedAtEpochMillis"
private const val PARAM_COUPON_ID = "couponId"
private const val PARAM_TYPE = "type"
private const val PARAM_MESSAGE = "message"
private const val PARAM_RATING = "rating"
private const val PARAM_SURVEY_ID = "surveyId"
private const val PARAM_OPTION_INDEX = "optionIndex"

class FirebaseRewardsRepository @Inject constructor(
    private val functions: FirebaseFunctions,
    private val rewardNotifier: RewardNotifier,
) : RewardsRepository {

    override suspend fun completeSelfTask(taskId: String): RewardResult =
        callForReward(
            CloudFunctionNames.COMPLETE_SELF_TASK,
            mapOf(PARAM_TASK_ID to taskId),
            PointsEarnReason.TASK_COMPLETED,
        )

    override suspend fun redeemTaskCode(code: String): RewardResult =
        callForReward(CloudFunctionNames.REDEEM_TASK_CODE, mapOf(PARAM_CODE to code), PointsEarnReason.TASK_COMPLETED)

    override suspend fun recordTipRead(tipId: String): RewardResult =
        callForReward(CloudFunctionNames.RECORD_TIP_READ, mapOf(PARAM_TIP_ID to tipId), PointsEarnReason.ARTICLE_READ)

    override suspend fun recordGameResult(gameId: String, score: Int): RewardResult =
        callForReward(
            CloudFunctionNames.RECORD_GAME_RESULT,
            mapOf(PARAM_GAME_ID to gameId, PARAM_SCORE to score),
            PointsEarnReason.GAME_PLAYED,
        )

    override suspend fun redeemReward(rewardId: String): Coupon {
        val result = call(CloudFunctionNames.REDEEM_REWARD, mapOf(PARAM_REWARD_ID to rewardId))
        return Coupon(
            id = result[RESULT_COUPON_ID] as? String ?: "",
            rewardId = rewardId,
            redeemedAtEpochMillis = (result[RESULT_REDEEMED_AT] as? Number)?.toLong() ?: System.currentTimeMillis(),
            expiresAtEpochMillis = (result[RESULT_EXPIRES_AT] as? Number)?.toLong(),
            code = result[RESULT_CODE] as? String,
        )
    }

    override suspend fun markCouponUsed(couponId: String): Long {
        val result = call(CloudFunctionNames.MARK_COUPON_USED, mapOf(PARAM_COUPON_ID to couponId))
        return (result[RESULT_USED_AT] as? Number)?.toLong() ?: System.currentTimeMillis()
    }

    override suspend fun checkInEvent(code: String): RewardResult =
        callForReward(CloudFunctionNames.CHECK_IN_EVENT, mapOf(PARAM_CODE to code), PointsEarnReason.EVENT_ATTENDED)

    override suspend fun submitFeedback(type: String, message: String?, rating: Int?): RewardResult {
        val data = buildMap<String, Any> {
            put(PARAM_TYPE, type)
            message?.let { put(PARAM_MESSAGE, it) }
            rating?.let { put(PARAM_RATING, it) }
        }
        return callForReward(CloudFunctionNames.SUBMIT_FEEDBACK, data, PointsEarnReason.FEEDBACK_SUBMITTED)
    }

    override suspend fun submitSurveyAnswer(surveyId: String, optionIndex: Int): RewardResult =
        callForReward(
            CloudFunctionNames.SUBMIT_SURVEY_ANSWER,
            mapOf(PARAM_SURVEY_ID to surveyId, PARAM_OPTION_INDEX to optionIndex),
            PointsEarnReason.FEEDBACK_SUBMITTED,
        )

    private suspend fun callForReward(name: String, data: Map<String, Any>, reason: PointsEarnReason): RewardResult {
        val result = call(name, data)
        val reward = RewardResult(
            points = (result[RESULT_POINTS] as? Number)?.toInt() ?: 0,
            xp = (result[RESULT_XP] as? Number)?.toInt() ?: 0,
            streakBonus = (result[RESULT_STREAK_BONUS] as? Number)?.toInt() ?: 0,
        )
        if (reward.points > 0 || reward.xp > 0) {
            rewardNotifier.notifyReward(reason = reason, points = reward.points, xp = reward.xp)
        }
        return reward
    }

    private suspend fun call(name: String, data: Map<String, Any>): Map<*, *> =
        runCatching { functions.getHttpsCallable(name).call(data).await().data as? Map<*, *> ?: emptyMap<Any, Any>() }
            .getOrElse { error ->
                if (error is CancellationException) throw error
                throw RewardFailureException(error.toRewardFailure(), error)
            }
}

internal fun Throwable.toRewardFailure(): RewardFailure = when {
    this is FirebaseNetworkException -> RewardFailure.NETWORK
    this !is FirebaseFunctionsException -> RewardFailure.UNKNOWN
    message.orEmpty().contains(QR_NOT_ACTIVE) -> RewardFailure.QR_CODE_NOT_ACTIVE
    message.orEmpty().contains(QR_LIMIT_REACHED) -> RewardFailure.QR_CODE_LIMIT_REACHED
    message.orEmpty().contains(REWARD_SOLD_OUT) -> RewardFailure.REWARD_SOLD_OUT
    code == FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED -> RewardFailure.DAILY_LIMIT_REACHED
    code == FirebaseFunctionsException.Code.ALREADY_EXISTS -> RewardFailure.ALREADY_COMPLETED
    code == FirebaseFunctionsException.Code.NOT_FOUND -> RewardFailure.INVALID_CODE
    code == FirebaseFunctionsException.Code.FAILED_PRECONDITION && message.orEmpty().contains(NOT_ENOUGH_POINTS) ->
        RewardFailure.NOT_ENOUGH_POINTS
    code == FirebaseFunctionsException.Code.FAILED_PRECONDITION -> RewardFailure.WRONG_VERIFICATION
    code == FirebaseFunctionsException.Code.UNAVAILABLE -> RewardFailure.NETWORK
    else -> RewardFailure.UNKNOWN
}

private const val NOT_ENOUGH_POINTS = "Not enough points"
private const val QR_NOT_ACTIVE = "qr_not_active"
private const val QR_LIMIT_REACHED = "qr_limit_reached"
private const val REWARD_SOLD_OUT = "reward_sold_out"
