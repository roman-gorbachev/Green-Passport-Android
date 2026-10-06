package com.smartcity.greenpassport.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.smartcity.greenpassport.core.designsystem.component.GlassBottomBarLayout
import com.smartcity.greenpassport.core.navigation.TopLevelDestination

@Composable
fun TopLevelScreen(
    tab: TopLevelDestination,
    navController: NavController,
    content: @Composable () -> Unit,
) {
    GlassBottomBarLayout(
        selected = tab,
        onSelect = navController::navigateToTopLevel,
        isOpaque = tab == TopLevelDestination.MAP,
        content = content,
    )
}
