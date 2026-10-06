package com.smartcity.greenpassport.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.smartcity.greenpassport.core.navigation.TopLevelDestination

fun NavController.navigateToTopLevel(tab: TopLevelDestination) {
    navigate(tab.destination) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.isTabSwitch(): Boolean =
    initialState.destination.isTopLevel() && targetState.destination.isTopLevel()

private fun NavDestination.isTopLevel(): Boolean =
    TopLevelDestination.entries.any { tab -> hasRoute(tab.destination::class) }
