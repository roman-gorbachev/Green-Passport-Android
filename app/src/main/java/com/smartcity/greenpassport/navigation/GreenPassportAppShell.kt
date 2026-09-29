package com.smartcity.greenpassport.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartcity.greenpassport.core.designsystem.component.GpBottomBar
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.navigation.TopLevelDestination
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun GreenPassportAppShell(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = TopLevelDestination.entries.firstOrNull { tab ->
        backStackEntry?.destination?.hasRoute(tab.destination::class) == true
    }
    val visibleTab = currentTab ?: previousTabUnderDialog(navController)
    val hazeState = rememberHazeState()

    Box(modifier = modifier.fillMaxSize()) {
        AppNavHost(navController = navController, modifier = Modifier.hazeSource(hazeState))
        if (visibleTab != null) {
            GpBottomBar(
                selected = visibleTab,
                onSelect = { tab -> navController.navigateToTopLevel(tab) },
                hazeState = hazeState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
            )
        }
    }
}

private fun previousTabUnderDialog(navController: NavController): TopLevelDestination? {
    val current = navController.currentBackStackEntry?.destination ?: return null
    if (current.navigatorName != DIALOG_NAVIGATOR_NAME) return null
    val previous = navController.previousBackStackEntry?.destination ?: return null
    return TopLevelDestination.entries.firstOrNull { tab -> previous.hasRoute(tab.destination::class) }
}

private fun NavController.navigateToTopLevel(tab: TopLevelDestination) {
    navigate(tab.destination) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private const val DIALOG_NAVIGATOR_NAME = "dialog"
