package com.smartcity.greenpassport.feature.map

import android.content.Context
import com.yandex.mapkit.MapKitFactory

object MapKitInitializer {

    val isAvailable: Boolean
        get() = BuildConfig.YANDEX_MAPKIT_API_KEY.isNotBlank()

    fun initialize(context: Context) {
        if (!isAvailable) return
        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPKIT_API_KEY)
        MapKitFactory.initialize(context)
    }
}
