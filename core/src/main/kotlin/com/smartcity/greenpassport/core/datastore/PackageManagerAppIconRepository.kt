package com.smartcity.greenpassport.core.datastore

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.smartcity.greenpassport.core.model.settings.AppIcon
import com.smartcity.greenpassport.core.model.settings.AppIconRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val ALIAS_PACKAGE = "com.smartcity.greenpassport"
private const val ALIAS_PREFIX = "Icon"

@Singleton
class PackageManagerAppIconRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppIconRepository {

    @Volatile
    private var pending: AppIcon? = null

    override val current: AppIcon
        get() = pending ?: enabledIcon()

    override fun set(icon: AppIcon) {
        pending = icon.takeIf { it != enabledIcon() }
    }

    override fun applyPending() {
        val icon = pending ?: return
        pending = null
        val packageManager = context.packageManager
        packageManager.setComponentEnabledSetting(
            componentName(icon),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )
        AppIcon.entries.filter { it != icon }.forEach { other ->
            packageManager.setComponentEnabledSetting(
                componentName(other),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP,
            )
        }
    }

    private fun enabledIcon(): AppIcon = AppIcon.entries.firstOrNull { isEnabled(it) } ?: AppIcon.STANDARD

    private fun isEnabled(icon: AppIcon): Boolean =
        when (context.packageManager.getComponentEnabledSetting(componentName(icon))) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> icon == AppIcon.STANDARD
            else -> false
        }

    private fun componentName(icon: AppIcon): ComponentName {
        val suffix = icon.name.lowercase().replaceFirstChar { it.uppercase() }
        return ComponentName(context.packageName, "$ALIAS_PACKAGE.$ALIAS_PREFIX$suffix")
    }
}
