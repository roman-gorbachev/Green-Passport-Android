package com.smartcity.greenpassport.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.smartcity.greenpassport.R
import com.smartcity.greenpassport.core.designsystem.component.SheetDialogProperties
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.ui.ProfileSetupScreen
import com.smartcity.greenpassport.feature.calendar.presentation.ui.CalendarScreen
import com.smartcity.greenpassport.feature.calendar.presentation.ui.EventDetailSheet
import com.smartcity.greenpassport.feature.community.presentation.ui.CommunityHubScreen
import com.smartcity.greenpassport.feature.community.presentation.ui.ForumScreen
import com.smartcity.greenpassport.feature.community.presentation.ui.GroupsScreen
import com.smartcity.greenpassport.feature.ecotips.presentation.ui.EcoTipDetailScreen
import com.smartcity.greenpassport.feature.ecotips.presentation.ui.EcoTipsListScreen
import com.smartcity.greenpassport.feature.feedback.presentation.FeedbackScreen
import com.smartcity.greenpassport.feature.games.domain.GameId
import com.smartcity.greenpassport.feature.games.presentation.GamesHubScreen
import com.smartcity.greenpassport.feature.games.presentation.gameTitleRes
import com.smartcity.greenpassport.feature.games.presentation.maze.MazeScreen
import com.smartcity.greenpassport.feature.games.presentation.puzzle.PuzzleScreen
import com.smartcity.greenpassport.feature.games.presentation.quiz.QuizScreen
import com.smartcity.greenpassport.feature.games.presentation.sorting.WasteSortingScreen
import com.smartcity.greenpassport.feature.home.presentation.ui.HomeScreen
import com.smartcity.greenpassport.feature.map.presentation.ui.MapScreen
import com.smartcity.greenpassport.feature.profile.presentation.CardsScreen
import com.smartcity.greenpassport.feature.profile.presentation.ExchangeScreen
import com.smartcity.greenpassport.feature.profile.presentation.achievements.AchievementsScreen
import com.smartcity.greenpassport.feature.profile.presentation.bookmarks.BookmarksScreen
import com.smartcity.greenpassport.feature.profile.presentation.favorites.FavoritesTabScreen
import com.smartcity.greenpassport.feature.profile.presentation.history.HistoryScreen
import com.smartcity.greenpassport.feature.profile.presentation.notifications.NotificationsScreen
import com.smartcity.greenpassport.feature.profile.presentation.profile.ProfileScreen
import com.smartcity.greenpassport.feature.shop.presentation.ui.ShopScreen
import com.smartcity.greenpassport.feature.tasks.presentation.ui.TaskDetailSheet
import com.smartcity.greenpassport.feature.tasks.presentation.ui.TasksListScreen
import com.smartcity.greenpassport.feature.community.R as CommunityR
import com.smartcity.greenpassport.feature.profile.R as ProfileR

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home,
        modifier = modifier,
    ) {
        composable<Destination.Home> {
            HomeScreen(
                onProfileClick = { navController.navigate(Destination.Profile) },
                onEventSelected = { eventId -> navController.navigate(Destination.EventDetail(eventId)) },
                onTaskSelected = { taskId -> navController.navigate(Destination.TaskDetail(taskId)) },
                onAllTasksClick = { navController.navigate(Destination.Tasks) },
                onDestinationSelected = { destination -> navController.navigate(destination) },
            )
        }

        composable<Destination.Tasks> {
            FeatureScaffold(
                title = stringResource(destinationTitleRes(Destination.Tasks)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                TasksListScreen(
                    onTaskSelected = { taskId -> navController.navigate(Destination.TaskDetail(taskId)) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        dialog<Destination.TaskDetail>(dialogProperties = SheetDialogProperties) {
            TaskDetailSheet(onDismiss = navController::popBackStack)
        }
        dialog<Destination.EventDetail>(dialogProperties = SheetDialogProperties) {
            EventDetailSheet(onDismiss = navController::popBackStack)
        }
        composable<Destination.Profile> {
            FeatureScaffold(
                title = stringResource(destinationTitleRes(Destination.Profile)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                ProfileScreen(
                    onMenuEntrySelected = { destination -> navController.navigate(destination) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        composable<Destination.EditProfile> {
            FeatureScaffold(
                title = stringResource(R.string.edit_profile),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                ProfileSetupScreen(
                    isEditing = true,
                    onFinished = navController::popBackStack,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        composable<Destination.Achievements> {
            FeatureScaffold(
                title = stringResource(ProfileR.string.achievements_screen_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                AchievementsScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Cards> {
            FeatureScaffold(
                title = stringResource(ProfileR.string.cards_screen_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                CardsScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Exchange> {
            FeatureScaffold(
                title = stringResource(ProfileR.string.exchange_screen_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                ExchangeScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.History> {
            FeatureScaffold(
                title = stringResource(ProfileR.string.history_screen_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                HistoryScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Notifications> {
            FeatureScaffold(
                title = stringResource(ProfileR.string.notifications_screen_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                NotificationsScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Favorites> {
            FeatureScaffold(
                title = stringResource(ProfileR.string.favorites_screen_title),
                onNavigateBack = null,
            ) { innerPadding ->
                FavoritesTabScreen(
                    onTaskSelected = { taskId -> navController.navigate(Destination.TaskDetail(taskId)) },
                    onTipSelected = { tipId -> navController.navigate(Destination.EcoTipDetail(tipId)) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        composable<Destination.Bookmarks> {
            FeatureScaffold(
                title = stringResource(ProfileR.string.bookmarks_screen_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                BookmarksScreen(
                    onTipSelected = { tipId -> navController.navigate(Destination.EcoTipDetail(tipId)) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        composable<Destination.Calendar> {
            FeatureScaffold(
                title = stringResource(destinationTitleRes(Destination.Calendar)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                CalendarScreen(
                    onEventSelected = { eventId -> navController.navigate(Destination.EventDetail(eventId)) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        composable<Destination.Map> {
            MapScreen()
        }
        composable<Destination.Community> {
            FeatureScaffold(
                title = stringResource(destinationTitleRes(Destination.Community)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                CommunityHubScreen(
                    onForumSelected = { navController.navigate(Destination.Forum) },
                    onGroupsSelected = { navController.navigate(Destination.CommunityGroups) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        composable<Destination.Forum> {
            FeatureScaffold(
                title = stringResource(CommunityR.string.community_forum_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                ForumScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.CommunityGroups> {
            FeatureScaffold(
                title = stringResource(CommunityR.string.community_groups_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                GroupsScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.EcoTips> {
            FeatureScaffold(
                title = stringResource(destinationTitleRes(Destination.EcoTips)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                EcoTipsListScreen(
                    onTipSelected = { tipId -> navController.navigate(Destination.EcoTipDetail(tipId)) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
        composable<Destination.EcoTipDetail> {
            FeatureScaffold(
                title = stringResource(R.string.ecotip_detail_title),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                EcoTipDetailScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Games> {
            FeatureScaffold(
                title = stringResource(destinationTitleRes(Destination.Games)),
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
            FeatureScaffold(
                title = stringResource(gameTitleRes(GameId.ECO_MAZE)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                MazeScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.EcoQuizGame> {
            FeatureScaffold(
                title = stringResource(gameTitleRes(GameId.ECO_QUIZ)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                QuizScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Shop> {
            FeatureScaffold(
                title = stringResource(destinationTitleRes(Destination.Shop)),
                onNavigateBack = null,
            ) { innerPadding ->
                ShopScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        composable<Destination.Feedback> {
            FeatureScaffold(
                title = stringResource(destinationTitleRes(Destination.Feedback)),
                onNavigateBack = navController::popBackStack,
            ) { innerPadding ->
                FeedbackScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
