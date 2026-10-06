package com.smartcity.greenpassport.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.smartcity.greenpassport.R
import com.smartcity.greenpassport.core.designsystem.component.SearchBarContent
import com.smartcity.greenpassport.core.designsystem.component.SheetDialogProperties
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.core.navigation.TopLevelDestination
import com.smartcity.greenpassport.core.navigation.destination
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.ui.ProfileSetupScreen
import com.smartcity.greenpassport.feature.calendar.presentation.ui.CalendarScreen
import com.smartcity.greenpassport.feature.calendar.presentation.ui.EventDetailSheet
import com.smartcity.greenpassport.feature.community.presentation.ui.ArchivedChatsScreen
import com.smartcity.greenpassport.feature.community.presentation.ui.ChatMuteButton
import com.smartcity.greenpassport.feature.community.presentation.ui.CommunityAddButton
import com.smartcity.greenpassport.feature.community.presentation.ui.CommunityHubScreen
import com.smartcity.greenpassport.feature.community.presentation.ui.ForumScreen
import com.smartcity.greenpassport.feature.community.presentation.ui.GroupDetailScreen
import com.smartcity.greenpassport.feature.community.presentation.ui.GroupMenuButton
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.ChatMuteViewModel
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.CommunityHubViewModel
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.GroupDetailViewModel
import com.smartcity.greenpassport.feature.ecotips.presentation.ui.EcoTipBookmarkButton
import com.smartcity.greenpassport.feature.ecotips.presentation.ui.EcoTipDetailScreen
import com.smartcity.greenpassport.feature.ecotips.presentation.ui.EcoTipsListScreen
import com.smartcity.greenpassport.feature.ecotips.presentation.viewmodels.EcoTipDetailViewModel
import com.smartcity.greenpassport.feature.ecotips.presentation.viewmodels.EcoTipsListViewModel
import com.smartcity.greenpassport.feature.feedback.presentation.FeedbackScreen
import com.smartcity.greenpassport.feature.games.presentation.hub.ui.GamesHubScreen
import com.smartcity.greenpassport.feature.games.presentation.hub.viewmodels.GamesHubViewModel
import com.smartcity.greenpassport.feature.games.presentation.web.ui.GameWebScreen
import com.smartcity.greenpassport.feature.home.presentation.ui.HomeScreen
import com.smartcity.greenpassport.feature.map.presentation.ui.MapScreen
import com.smartcity.greenpassport.feature.moderation.presentation.ui.ModerationScreen
import com.smartcity.greenpassport.feature.profile.presentation.achievements.AchievementsScreen
import com.smartcity.greenpassport.feature.profile.presentation.favorites.FavoritesTabScreen
import com.smartcity.greenpassport.feature.profile.presentation.history.HistoryScreen
import com.smartcity.greenpassport.feature.profile.presentation.notifications.NotificationsScreen
import com.smartcity.greenpassport.feature.profile.presentation.profile.ProfileScreen
import com.smartcity.greenpassport.feature.shop.presentation.ui.CouponDetailSheet
import com.smartcity.greenpassport.feature.shop.presentation.ui.CouponsScreen
import com.smartcity.greenpassport.feature.shop.presentation.ui.ShopScreen
import com.smartcity.greenpassport.feature.tasks.presentation.ui.TaskDetailSheet
import com.smartcity.greenpassport.feature.tasks.presentation.ui.TasksFilterButton
import com.smartcity.greenpassport.feature.tasks.presentation.ui.TasksListScreen
import com.smartcity.greenpassport.feature.tasks.presentation.viewmodels.TasksListViewModel
import com.smartcity.greenpassport.feature.community.R as CommunityR
import com.smartcity.greenpassport.feature.ecotips.R as EcoTipsR
import com.smartcity.greenpassport.feature.games.R as GamesR
import com.smartcity.greenpassport.feature.moderation.R as ModerationR
import com.smartcity.greenpassport.feature.profile.R as ProfileR
import com.smartcity.greenpassport.feature.shop.R as ShopR
import com.smartcity.greenpassport.feature.tasks.R as TasksR

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home,
        enterTransition = {
            if (isTabSwitch()) EnterTransition.None else slideIntoContainer(SlideDirection.Start)
        },
        exitTransition = {
            if (isTabSwitch()) ExitTransition.None else ExitTransition.KeepUntilTransitionsFinished
        },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = {
            if (isTabSwitch()) ExitTransition.None else slideOutOfContainer(SlideDirection.End)
        },
        modifier = modifier,
    ) {
        mainRoutes(navController)
        profileRoutes(navController)
        communityRoutes(navController)
        chatRoutes(navController)
        contentRoutes(navController)
        gameRoutes(navController)
        couponRoutes(navController)
    }
}

private fun NavGraphBuilder.mainRoutes(navController: NavHostController) {
    composable<Destination.Home> {
        TopLevelScreen(tab = TopLevelDestination.HOME, navController = navController) {
            HomeScreen(
                onProfileClick = { navController.navigate(Destination.Profile) },
                onEventSelected = { eventId -> navController.navigate(Destination.EventDetail(eventId)) },
                onTaskSelected = { taskId -> navController.navigate(Destination.TaskDetail(taskId)) },
                onAllTasksClick = { navController.navigate(Destination.Tasks) },
                onDestinationSelected = { destination -> navController.navigate(destination) },
            )
        }
    }
    composable<Destination.Tasks> {
        val viewModel: TasksListViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val query by viewModel.query.collectAsStateWithLifecycle()
        FeatureScaffold(
            title = stringResource(destinationTitleRes(Destination.Tasks)),
            onNavigateBack = navController::popBackStack,
            search = SearchBarContent(
                query = query,
                onQueryChange = viewModel::onQueryChanged,
                placeholder = stringResource(TasksR.string.search_tasks_placeholder),
            ),
            actions = {
                TasksFilterButton(
                    activeCount = uiState.filters.activeCount,
                    onClick = { viewModel.onFilterSheetVisibilityChanged(true) },
                )
            },
        ) { innerPadding ->
            TasksListScreen(
                onTaskSelected = { taskId -> navController.navigate(Destination.TaskDetail(taskId)) },
                contentPadding = innerPadding,
                viewModel = viewModel,
            )
        }
    }
    dialog<Destination.TaskDetail>(dialogProperties = SheetDialogProperties) {
        TaskDetailSheet(onDismiss = navController::popBackStack)
    }
    dialog<Destination.EventDetail>(dialogProperties = SheetDialogProperties) {
        EventDetailSheet(onDismiss = navController::popBackStack)
    }
    composable<Destination.Favorites> {
        TopLevelScreen(tab = TopLevelDestination.FAVORITES, navController = navController) {
            FeatureScaffold(
                title = stringResource(ProfileR.string.favorites_screen_title),
                onNavigateBack = null,
            ) { innerPadding ->
                FavoritesTabScreen(
                    onTaskSelected = { taskId -> navController.navigate(Destination.TaskDetail(taskId)) },
                    onTipSelected = { tipId -> navController.navigate(Destination.EcoTipDetail(tipId)) },
                    contentPadding = innerPadding,
                )
            }
        }
    }
    composable<Destination.Map> {
        TopLevelScreen(tab = TopLevelDestination.MAP, navController = navController) {
            MapScreen()
        }
    }
    composable<Destination.Shop> {
        TopLevelScreen(tab = TopLevelDestination.SHOP, navController = navController) {
            FeatureScaffold(
                title = stringResource(destinationTitleRes(Destination.Shop)),
                onNavigateBack = null,
            ) { innerPadding ->
                ShopScreen(
                    onCouponsClick = { navController.navigate(Destination.Coupons) },
                    onCouponSelected = { couponId -> navController.navigate(Destination.CouponDetail(couponId)) },
                    contentPadding = innerPadding,
                )
            }
        }
    }
}

private fun NavGraphBuilder.profileRoutes(navController: NavHostController) {
    composable<Destination.Profile> {
        FeatureScaffold(
            title = stringResource(destinationTitleRes(Destination.Profile)),
            onNavigateBack = navController::popBackStack,
        ) { innerPadding ->
            ProfileScreen(
                onMenuEntrySelected = { destination -> navController.navigate(destination) },
                contentPadding = innerPadding,
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
                contentPadding = innerPadding,
            )
        }
    }
    composable<Destination.Achievements> {
        FeatureScaffold(
            title = stringResource(ProfileR.string.achievements_screen_title),
            onNavigateBack = navController::popBackStack,
        ) { innerPadding ->
            AchievementsScreen(contentPadding = innerPadding)
        }
    }
    composable<Destination.History> {
        FeatureScaffold(
            title = stringResource(ProfileR.string.history_screen_title),
            onNavigateBack = navController::popBackStack,
        ) { innerPadding ->
            HistoryScreen(contentPadding = innerPadding)
        }
    }
    composable<Destination.Notifications> {
        FeatureScaffold(
            title = stringResource(ProfileR.string.notifications_screen_title),
            onNavigateBack = navController::popBackStack,
        ) { innerPadding ->
            NotificationsScreen(contentPadding = innerPadding)
        }
    }
    composable<Destination.Moderation> {
        FeatureScaffold(
            title = stringResource(ModerationR.string.moderation),
            onNavigateBack = navController::popBackStack,
        ) { innerPadding ->
            ModerationScreen(contentPadding = innerPadding)
        }
    }
}

private fun NavGraphBuilder.chatRoutes(navController: NavHostController) {
    composable<Destination.ArchivedChats> {
        FeatureScaffold(
            title = stringResource(CommunityR.string.archived_chats),
            onNavigateBack = navController::popBackStack,
        ) { innerPadding ->
            ArchivedChatsScreen(
                onChatSelected = { chatId -> navController.navigate(chatId.destination) },
                contentPadding = innerPadding,
            )
        }
    }
    composable<Destination.Forum> {
        val muteViewModel: ChatMuteViewModel = hiltViewModel()
        val isMuted by muteViewModel.isMuted.collectAsStateWithLifecycle()
        val canMute by muteViewModel.canMute.collectAsStateWithLifecycle()
        FeatureScaffold(
            title = stringResource(CommunityR.string.community_forum_title),
            onNavigateBack = navController::popBackStack,
            actions = {
                if (canMute) ChatMuteButton(isMuted = isMuted, onToggle = muteViewModel::onToggle)
            },
        ) { innerPadding ->
            ForumScreen(contentPadding = innerPadding)
        }
    }
    composable<Destination.CommunityGroup> {
        val viewModel: GroupDetailViewModel = hiltViewModel()
        val muteViewModel: ChatMuteViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val isMuted by muteViewModel.isMuted.collectAsStateWithLifecycle()
        val group = uiState.group
        FeatureScaffold(
            title = group?.name.orEmpty(),
            onNavigateBack = navController::popBackStack,
            actions = {
                if (group != null && uiState.isMember) {
                    GroupMenuButton(
                        group = group,
                        isMuted = isMuted,
                        onToggleMute = muteViewModel::onToggle,
                        onShowMembers = { viewModel.onMembersVisibilityChanged(true) },
                        onLeave = { viewModel.onLeaveConfirmationVisibilityChanged(true) },
                    )
                }
            },
        ) { innerPadding ->
            GroupDetailScreen(contentPadding = innerPadding, viewModel = viewModel)
        }
    }
}

private fun NavGraphBuilder.communityRoutes(navController: NavHostController) {
    composable<Destination.Community> {
        val viewModel: CommunityHubViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        FeatureScaffold(
            title = stringResource(destinationTitleRes(Destination.Community)),
            onNavigateBack = navController::popBackStack,
            search = SearchBarContent(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChanged,
                placeholder = stringResource(CommunityR.string.search_groups_placeholder),
            ),
            actions = {
                if (uiState.currentUserId != null) {
                    CommunityAddButton(
                        onCreateGroup = { viewModel.onCreateDialogVisibilityChanged(true) },
                        onJoinByCode = { viewModel.onCodeDialogVisibilityChanged(true) },
                    )
                }
            },
        ) { innerPadding ->
            CommunityHubScreen(
                onChatSelected = { chatId -> navController.navigate(chatId.destination) },
                onArchiveSelected = { navController.navigate(Destination.ArchivedChats) },
                contentPadding = innerPadding,
                viewModel = viewModel,
            )
        }
    }
    composable<Destination.Feedback> {
        FeatureScaffold(
            title = stringResource(destinationTitleRes(Destination.Feedback)),
            onNavigateBack = navController::popBackStack,
        ) { innerPadding ->
            FeedbackScreen(contentPadding = innerPadding)
        }
    }
}

private fun NavGraphBuilder.contentRoutes(navController: NavHostController) {
    composable<Destination.Calendar> {
        FeatureScaffold(
            title = stringResource(destinationTitleRes(Destination.Calendar)),
            onNavigateBack = navController::popBackStack,
        ) { innerPadding ->
            CalendarScreen(
                onEventSelected = { eventId -> navController.navigate(Destination.EventDetail(eventId)) },
                contentPadding = innerPadding,
            )
        }
    }
    composable<Destination.EcoTips> {
        val viewModel: EcoTipsListViewModel = hiltViewModel()
        val query by viewModel.query.collectAsStateWithLifecycle()
        FeatureScaffold(
            title = stringResource(destinationTitleRes(Destination.EcoTips)),
            onNavigateBack = navController::popBackStack,
            search = SearchBarContent(
                query = query,
                onQueryChange = viewModel::onQueryChanged,
                placeholder = stringResource(EcoTipsR.string.search_ecotips_placeholder),
            ),
        ) { innerPadding ->
            EcoTipsListScreen(
                onTipSelected = { tipId -> navController.navigate(Destination.EcoTipDetail(tipId)) },
                contentPadding = innerPadding,
                viewModel = viewModel,
            )
        }
    }
    composable<Destination.EcoTipDetail> {
        val viewModel: EcoTipDetailViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        FeatureScaffold(
            title = stringResource(R.string.ecotip_detail_title),
            onNavigateBack = navController::popBackStack,
            actions = {
                if (uiState.tip != null) {
                    EcoTipBookmarkButton(isBookmarked = uiState.isBookmarked, onClick = viewModel::onToggleBookmark)
                }
            },
        ) { innerPadding ->
            EcoTipDetailScreen(contentPadding = innerPadding, viewModel = viewModel)
        }
    }
}

private fun NavGraphBuilder.gameRoutes(navController: NavHostController) {
    composable<Destination.Games> {
        val viewModel: GamesHubViewModel = hiltViewModel()
        val query by viewModel.query.collectAsStateWithLifecycle()
        FeatureScaffold(
            title = stringResource(destinationTitleRes(Destination.Games)),
            onNavigateBack = navController::popBackStack,
            search = SearchBarContent(
                query = query,
                onQueryChange = viewModel::onQueryChanged,
                placeholder = stringResource(GamesR.string.search_games_placeholder),
            ),
        ) { innerPadding ->
            GamesHubScreen(
                onGameSelected = { gameId -> navController.navigate(Destination.GameWeb(gameId)) },
                contentPadding = innerPadding,
                viewModel = viewModel,
            )
        }
    }
    composable<Destination.GameWeb> {
        GameWebScreen(onClose = navController::popBackStack)
    }
}

private fun NavGraphBuilder.couponRoutes(navController: NavHostController) {
    composable<Destination.Coupons> {
        FeatureScaffold(
            title = stringResource(ShopR.string.my_coupons),
            onNavigateBack = navController::popBackStack,
        ) { innerPadding ->
            CouponsScreen(
                onCouponSelected = { couponId -> navController.navigate(Destination.CouponDetail(couponId)) },
                contentPadding = innerPadding,
            )
        }
    }
    dialog<Destination.CouponDetail>(dialogProperties = SheetDialogProperties) {
        CouponDetailSheet(onDismiss = navController::popBackStack)
    }
}
