package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.GpListRow
import com.smartcity.greenpassport.core.designsystem.component.IconCircle
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.feature.community.R

@Composable
fun CommunityHubScreen(
    onForumSelected: () -> Unit,
    onGroupsSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sectionColors = GreenPassportTheme.sectionColors

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
    ) {
        GpListRow(
            title = stringResource(R.string.community_forum_title),
            leading = { IconCircle(icon = Icons.Filled.Forum, color = sectionColors.community) },
            onClick = onForumSelected,
        )
        GpListRow(
            title = stringResource(R.string.community_groups_title),
            leading = { IconCircle(icon = Icons.Filled.Groups, color = sectionColors.calendar) },
            onClick = onGroupsSelected,
        )
    }
}
