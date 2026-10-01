package com.smartcity.greenpassport.feature.profile.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.component.ListSectionRow
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.ProfileAvatar
import com.smartcity.greenpassport.core.designsystem.component.ProgressHeroCard
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.component.SymbolTileStyle
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.designsystem.theme.SectionColors
import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import com.smartcity.greenpassport.core.model.settings.AppLanguage
import com.smartcity.greenpassport.core.model.settings.AppTheme
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.feature.profile.R
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun ProfileScreen(
    onMenuEntrySelected: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.hasError) {
        ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::refresh,
            modifier = modifier.padding(contentPadding),
        )
        return
    }

    if (uiState.isLoading) {
        LoadingContent(modifier = modifier.padding(contentPadding))
        return
    }

    ProfileContent(
        uiState = uiState,
        onNotificationsToggle = viewModel::onNotificationsToggle,
        onThemeSelected = viewModel::onThemeSelected,
        onSignOut = viewModel::onSignOut,
        onMenuEntrySelected = onMenuEntrySelected,
        contentPadding = contentPadding,
        modifier = modifier,
    )
}

private data class ProfileMenuEntry(
    val labelRes: Int,
    val icon: ImageVector,
    val destination: Destination?,
    val color: (SectionColors) -> Color,
)

private val profileMenuEntries = listOf(
    ProfileMenuEntry(R.string.profile_achievements, Icons.Filled.EmojiEvents, Destination.Achievements) { it.tips },
    ProfileMenuEntry(R.string.profile_cards, Icons.Filled.Style, Destination.Cards) { it.games },
    ProfileMenuEntry(R.string.my_coupons, Icons.Filled.ConfirmationNumber, Destination.Coupons) { it.community },
    ProfileMenuEntry(R.string.profile_history, Icons.Filled.History, Destination.History) { it.calendar },
    ProfileMenuEntry(
        R.string.profile_notifications_label,
        Icons.Filled.Notifications,
        Destination.Notifications,
    ) { it.feedback },
    ProfileMenuEntry(R.string.profile_favorites, Icons.Filled.Favorite, Destination.Favorites) { it.feedback },
    ProfileMenuEntry(R.string.profile_bookmarks, Icons.Filled.Bookmark, Destination.Bookmarks) { it.community },
    ProfileMenuEntry(R.string.profile_exchange, Icons.Filled.SwapHoriz, Destination.Exchange) { it.games },
)

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onNotificationsToggle: (Boolean) -> Unit,
    onThemeSelected: (AppTheme) -> Unit,
    onSignOut: () -> Unit,
    onMenuEntrySelected: (Destination) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val sectionColors = GreenPassportTheme.sectionColors
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding + PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingMedium,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)) {
                ProfileHeader(uiState = uiState)
                ProgressHeroCard(points = uiState.points, level = uiState.level)
            }
        }

        item {
            SettingsSection(
                uiState = uiState,
                onNotificationsToggle = onNotificationsToggle,
                onThemeSelected = onThemeSelected,
                onMenuEntrySelected = onMenuEntrySelected,
            )
        }

        item {
            ListSection {
                profileMenuEntries.forEachIndexed { index, entry ->
                    ListSectionRow(
                        title = stringResource(entry.labelRes),
                        leading = { ProfileMenuIcon(icon = entry.icon, color = entry.color(sectionColors)) },
                        onClick = { entry.destination?.let(onMenuEntrySelected) },
                        showDivider = index < profileMenuEntries.lastIndex,
                    )
                }
            }
        }

        item {
            ListSection {
                ListSectionRow(
                    title = stringResource(R.string.profile_sign_out),
                    leading = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(Dimens.TileSizeSmall),
                        )
                    },
                    trailing = {},
                    onClick = onSignOut,
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    uiState: ProfileUiState,
    onNotificationsToggle: (Boolean) -> Unit,
    onThemeSelected: (AppTheme) -> Unit,
    onMenuEntrySelected: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListSection(modifier = modifier) {
        if (uiState.isModerator) {
            ListSectionRow(
                title = stringResource(R.string.moderation),
                leading = {
                    ProfileMenuIcon(
                        icon = Icons.Filled.Shield,
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = { onMenuEntrySelected(Destination.Moderation) },
                showDivider = true,
            )
        }
        if (!uiState.isAnonymous) {
            ListSectionRow(
                title = stringResource(R.string.edit_profile),
                leading = {
                    ProfileMenuIcon(
                        icon = Icons.Filled.Edit,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = { onMenuEntrySelected(Destination.EditProfile) },
                showDivider = true,
            )
        }
        ListSectionRow(
            title = stringResource(R.string.profile_notifications_label),
            leading = {
                ProfileMenuIcon(
                    icon = Icons.Filled.NotificationsActive,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            trailing = {
                Switch(
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = onNotificationsToggle,
                )
            },
            onClick = { onNotificationsToggle(!uiState.notificationsEnabled) },
            showDivider = true,
        )
        ThemeRow(selected = uiState.theme, onSelect = onThemeSelected)
        LanguageRow()
    }
}

@Composable
private fun ProfileHeader(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val profile = uiState.profile
        ProfileAvatar(style = profile?.avatar ?: AvatarStyle.LIME, size = Dimens.ProfileHeaderAvatarSize)
        Column(modifier = Modifier.padding(start = Dimens.SpacingMedium)) {
            Text(
                text = when {
                    profile != null -> "${profile.firstName} ${profile.lastName}".trim()
                    uiState.isAnonymous -> stringResource(R.string.profile_anonymous_label)
                    else -> uiState.email.orEmpty()
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = if (profile != null && profile.city.isNotBlank()) {
                    stringResource(R.string.city_and_points, profile.city, uiState.points)
                } else {
                    stringResource(R.string.points_balance, uiState.points)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProfileMenuIcon(
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    SymbolTile(
        icon = icon,
        style = SymbolTileStyle.Tinted(color),
        size = Dimens.TileSizeSmall,
        modifier = modifier,
    )
}

@Composable
private fun ThemeRow(
    selected: AppTheme,
    onSelect: (AppTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    ListSectionRow(
        title = stringResource(R.string.theme),
        leading = { ProfileMenuIcon(icon = themeIcon(selected), color = GreenPassportTheme.sectionColors.games) },
        trailing = {
            Box {
                Text(
                    text = stringResource(themeLabelRes(selected)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                DropdownMenu(expanded = isMenuOpen, onDismissRequest = { isMenuOpen = false }) {
                    AppTheme.entries.forEach { theme ->
                        DropdownMenuItem(
                            text = { Text(stringResource(themeLabelRes(theme))) },
                            leadingIcon = { Icon(imageVector = themeIcon(theme), contentDescription = null) },
                            onClick = {
                                isMenuOpen = false
                                onSelect(theme)
                            },
                        )
                    }
                }
            }
        },
        onClick = { isMenuOpen = true },
        showDivider = true,
        modifier = modifier,
    )
}

private fun themeLabelRes(theme: AppTheme): Int = when (theme) {
    AppTheme.SYSTEM -> R.string.system_theme
    AppTheme.LIGHT -> R.string.light_theme
    AppTheme.DARK -> R.string.dark_theme
}

private fun themeIcon(theme: AppTheme): ImageVector = when (theme) {
    AppTheme.SYSTEM -> Icons.Filled.BrightnessMedium
    AppTheme.LIGHT -> Icons.Filled.LightMode
    AppTheme.DARK -> Icons.Filled.DarkMode
}

@Composable
private fun LanguageRow(
    modifier: Modifier = Modifier,
    viewModel: AppLanguageViewModel = hiltViewModel(),
) {
    val selected by viewModel.language.collectAsStateWithLifecycle()
    var isMenuOpen by remember { mutableStateOf(false) }
    ListSectionRow(
        title = stringResource(R.string.language),
        leading = {
            ProfileMenuIcon(icon = Icons.Filled.Translate, color = GreenPassportTheme.sectionColors.calendar)
        },
        trailing = {
            Box {
                Text(
                    text = stringResource(appLanguageLabelRes(selected)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                DropdownMenu(expanded = isMenuOpen, onDismissRequest = { isMenuOpen = false }) {
                    AppLanguage.entries.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(stringResource(appLanguageLabelRes(language))) },
                            onClick = {
                                isMenuOpen = false
                                viewModel.onLanguageSelected(language)
                            },
                        )
                    }
                }
            }
        },
        onClick = { isMenuOpen = true },
        modifier = modifier,
    )
}
