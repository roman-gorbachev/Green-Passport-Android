package com.smartcity.greenpassport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.padding
import com.smartcity.greenpassport.R
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.feature.calendar.presentation.CalendarScreen
import com.smartcity.greenpassport.feature.games.domain.GameId
import com.smartcity.greenpassport.feature.games.presentation.GamesHubScreen
import com.smartcity.greenpassport.feature.games.presentation.gameTitleRes
import com.smartcity.greenpassport.feature.games.presentation.puzzle.PuzzleScreen
import com.smartcity.greenpassport.feature.games.presentation.sorting.WasteSortingScreen
import com.smartcity.greenpassport.feature.map.presentation.MapScreen
import com.smartcity.greenpassport.feature.profile.presentation.ProfileScreen
import com.smartcity.greenpassport.feature.shop.presentation.ShopScreen
import com.smartcity.greenpassport.feature.tasks.presentation.TaskDetailScreen
import com.smartcity.greenpassport.feature.tasks.presentation.TasksListScreen
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
            FeatureScaffold(
                title = stringResource(homeMenuLabelRes(Destination.Tasks)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                TasksListScreen(
                    onTaskSelected = { taskId -> navController.navigate(Destination.TaskDetail(taskId)) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        composable<Destination.TaskDetail> {
            FeatureScaffold(
                title = stringResource(R.string.task_detail_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                TaskDetailScreen(modifier = Modifier.padding(innerPadding))
            }
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
            FeatureScaffold(
                title = stringResource(homeMenuLabelRes(Destination.Calendar)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                CalendarScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Map> {
            FeatureScaffold(
                title = stringResource(homeMenuLabelRes(Destination.Map)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                MapScreen(modifier = Modifier.padding(innerPadding))
            }
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
            FeatureScaffold(
                title = stringResource(homeMenuLabelRes(Destination.Games)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                GamesHubScreen(
                    onGameSelected = { gameId ->
                        val destination = when (gameId) {
                            GameId.ECO_PUZZLE -> Destination.EcoPuzzleGame
                            GameId.WASTE_SORTING -> Destination.WasteSortingGame
                            GameId.ECO_MAZE -> Destination.EcoMazeGame
                            GameId.ECO_QUIZ -> Destination.EcoQuizGame
                        }
                        navController.navigate(destination)
                    },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        composable<Destination.EcoPuzzleGame> {
            FeatureScaffold(
                title = stringResource(gameTitleRes(GameId.ECO_PUZZLE)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                PuzzleScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.WasteSortingGame> {
            FeatureScaffold(
                title = stringResource(gameTitleRes(GameId.WASTE_SORTING)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                WasteSortingScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.EcoMazeGame> {
            PlaceholderScreen(
                title = stringResource(gameTitleRes(GameId.ECO_MAZE)),
                onNavigateBack = navController::popBackStack,
            )
        }
        composable<Destination.EcoQuizGame> {
            PlaceholderScreen(
                title = stringResource(gameTitleRes(GameId.ECO_QUIZ)),
                onNavigateBack = navController::popBackStack,
            )
        }
        composable<Destination.Shop> {
            FeatureScaffold(
                title = stringResource(homeMenuLabelRes(Destination.Shop)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                ShopScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Feedback> {
            PlaceholderScreen(
                title = stringResource(homeMenuLabelRes(Destination.Feedback)),
                onNavigateBack = navController::popBackStack,
            )
        }
    }
}
