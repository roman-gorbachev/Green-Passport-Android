package com.smartcity.greenpassport.feature.shop.presentation.state

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.model.CouponStatus
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.feature.shop.R
import java.text.DateFormat
import java.util.Date

@Composable
fun couponStatusText(item: CouponItem, nowEpochMillis: Long): String {
    val locale = LocalLocale.current.platformLocale
    val longDate = DateFormat.getDateInstance(DateFormat.LONG, locale)
    val mediumDate = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
    val coupon = item.coupon
    return when (coupon.status(nowEpochMillis)) {
        CouponStatus.ACTIVE -> {
            val daysLeft = item.daysLeft(nowEpochMillis)
            val expiresAt = coupon.expiresAtEpochMillis
            when {
                item.isExpiringSoon(nowEpochMillis) && daysLeft != null -> stringResource(R.string.days_left, daysLeft)
                expiresAt == null -> stringResource(R.string.no_expiry_date)
                else -> stringResource(R.string.valid_until, longDate.format(Date(expiresAt)))
            }
        }
        CouponStatus.USED -> stringResource(
            R.string.used_on,
            mediumDate.format(Date(coupon.usedAtEpochMillis ?: coupon.redeemedAtEpochMillis)),
        )
        CouponStatus.EXPIRED -> stringResource(
            R.string.expired_on,
            mediumDate.format(Date(coupon.expiresAtEpochMillis ?: coupon.redeemedAtEpochMillis)),
        )
    }
}

@StringRes
fun couponFailureMessageRes(failure: RewardFailure): Int = when (failure) {
    RewardFailure.ALREADY_COMPLETED -> R.string.coupon_already_used
    RewardFailure.WRONG_VERIFICATION -> R.string.coupon_expired
    RewardFailure.NETWORK -> R.string.no_internet_connection
    else -> R.string.something_went_wrong_msg
}

@StringRes
fun couponsTabLabelRes(status: CouponStatus): Int = when (status) {
    CouponStatus.ACTIVE -> R.string.coupons_active
    CouponStatus.USED -> R.string.coupons_used
    CouponStatus.EXPIRED -> R.string.coupons_expired
}

@StringRes
fun couponsEmptyMessageRes(status: CouponStatus): Int = when (status) {
    CouponStatus.ACTIVE -> R.string.no_active_coupons_msg
    CouponStatus.USED -> R.string.no_used_coupons
    CouponStatus.EXPIRED -> R.string.no_expired_coupons
}
