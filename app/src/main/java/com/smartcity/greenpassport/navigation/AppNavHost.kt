package com.smartcity.greenpassport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.padding
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.feature.profile.presentation.ProfileScreen
import com.smartcity.greenpassport.home.HomeScreen
import com.smartcity.greenpassport.home.homeMenuLabelRes

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.Home,
        modifier = modifier,
    ) {
        composable<Destination.Home> {
            HomeScreen(onDestinationSelected = { destination -> navController.navigate(destination) })
        }

        composable<Destination.Tasks> {
            PlaceholderScreen(
                title = stringResource(homeMenuLabelRes(Destination.Tasks)),
                onNavigateBack = navController::popBackStack,
            )
        }
        composable<Destination.Profile> {
            FeatureScaffold(
                title = stringResource(homeMenuLabelRes(Destination.Profile)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                ProfileScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Calendar> {
            PlaceholderScreen(
                title = stringResource(homeMenuLabelRes(Destination.Calendar)),
                onNavigateBack = navController::popBackStack,
            )
        }
        composable<Destination.Map> {
            PlaceholderScreen(
                title = stringResource(homeMenuLabelRes(Destination.Map)),
                onNavigateBack = navController::popBackStack,
            )
        }
        composable<Destination.Community> {
            PlaceholderScreen(
                title = stringResource(homeMenuLabelRes(Destination.Community)),
                onNavigateBack = navController::popBackStack,
            )
        }
        composable<Destination.EcoTips> {
            PlaceholderScreen(
                title = stringResource(homeMenuLabelRes(Destination.EcoTips)),
                onNavigateBack = navController::popBackStack,
            )
        }
        composable<Destination.Games> {
            PlaceholderScreen(
                title = stringResource(homeMenuLabelRes(Destination.Games)),
                onNavigateBack = navController::popBackStack,
            )
        }
        composable<Destination.Shop> {
            PlaceholderScreen(
                title = stringResource(homeMenuLabelRes(Destination.Shop)),
                onNavigateBack = navController::popBackStack,
            )
        }
        composable<Destination.Feedback> {
            PlaceholderScreen(
                title = stringResource(homeMenuLabelRes(Destination.Feedback)),
                onNavigateBack = navController::popBackStack,
            )
        }
    }
}
