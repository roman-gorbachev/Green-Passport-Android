package com.smartcity.greenpassport

import android.app.Application
import com.smartcity.greenpassport.core.messaging.repository.INotificationsRepository
import com.smartcity.greenpassport.core.model.settings.AppIconRepository
import com.smartcity.greenpassport.feature.map.MapKitInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GreenPassportApplication : Application() {

    @Inject
    lateinit var notificationsRepository: INotificationsRepository

    @Inject
    lateinit var appIconRepository: AppIconRepository

    override fun onCreate() {
        super.onCreate()
        MapKitInitializer.initialize(this)
        notificationsRepository.ensureNotificationChannels()
        registerActivityLifecycleCallbacks(AppIconSwitchCallbacks(appIconRepository))
    }
}
