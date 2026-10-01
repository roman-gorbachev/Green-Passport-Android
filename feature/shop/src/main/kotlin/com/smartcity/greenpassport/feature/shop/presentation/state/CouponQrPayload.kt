package com.smartcity.greenpassport.feature.shop.presentation.state

import android.net.Uri
import com.smartcity.greenpassport.core.model.Coupon

private const val COUPON_SCAN_URL = "https://chatroom-85fb8.web.app/coupon"
private const val ID_PARAMETER = "id"
private const val CODE_PARAMETER = "code"

fun couponQrPayload(coupon: Coupon): String? {
    val code = coupon.code ?: return null
    return Uri.parse(COUPON_SCAN_URL).buildUpon()
        .appendQueryParameter(ID_PARAMETER, coupon.id)
        .appendQueryParameter(CODE_PARAMETER, code)
        .build()
        .toString()
}
