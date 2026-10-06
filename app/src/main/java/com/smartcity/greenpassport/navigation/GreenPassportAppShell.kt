package com.smartcity.greenpassport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartcity.greenpassport.core.designsystem.component.GlassBottomBarLayout
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.navigation.TopLevelDestination
import com.smartcity.greenpassport.core.navigation.destination

@Composable
fun GreenPassportAppShell(
    pendingChatId: String?,
    onChatOpened: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    LaunchedEffect(pendingChatId) {
        val chatId = pendingChatId ?: return@LaunchedEffect
        navController.navigate(ChatId.fromRawValue(chatId).destination)
        onChatOpened()
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = TopLevelDestination.entries.firstOrNull { tab ->
        backStackEntry?.destination?.hasRoute(tab.destination::class) == true
    }
    val visibleTab = currentTab ?: previousTabUnderDialog(navController)

    GlassBottomBarLayout(
        selected = visibleTab,
        onSelect = { tab -> navController.navigateToTopLevel(tab) },
        modifier = modifier,
        isOpaque = visibleTab == TopLevelDestination.MAP,
    ) {
        AppNavHost(navController = navController)
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
