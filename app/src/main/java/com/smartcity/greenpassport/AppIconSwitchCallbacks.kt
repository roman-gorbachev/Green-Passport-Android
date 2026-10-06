package com.smartcity.greenpassport

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.smartcity.greenpassport.core.model.settings.AppIconRepository

class AppIconSwitchCallbacks(
    private val appIconRepository: AppIconRepository,
) : Application.ActivityLifecycleCallbacks {

    private var startedActivities = 0

    override fun onActivityStarted(activity: Activity) {
        startedActivities += 1
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivities -= 1
        if (startedActivities == 0 && !activity.isChangingConfigurations) {
            appIconRepository.applyPending()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}
