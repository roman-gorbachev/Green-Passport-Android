package com.smartcity.greenpassport

import android.app.Application
import com.smartcity.greenpassport.feature.map.MapKitInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GreenPassportApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitInitializer.initialize(this)
    }
}
