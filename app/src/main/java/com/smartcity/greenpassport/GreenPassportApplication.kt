package com.smartcity.greenpassport

import android.app.Application
import com.smartcity.greenpassport.core.messaging.repository.INotificationsRepository
import com.smartcity.greenpassport.feature.map.MapKitInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GreenPassportApplication : Application() {

    @Inject
    lateinit var notificationsRepository: INotificationsRepository

    override fun onCreate() {
        super.onCreate()
        MapKitInitializer.initialize(this)
        notificationsRepository.ensureNotificationChannels()
    }
}
