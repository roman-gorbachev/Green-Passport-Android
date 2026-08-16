package com.smartcity.greenpassport.feature.community.presentation

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
import com.smartcity.greenpassport.core.designsystem.component.PillListItem
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.community.R

@Composable
fun CommunityHubScreen(
    onForumSelected: () -> Unit,
    onGroupsSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
    ) {
        PillListItem(
            title = stringResource(R.string.community_forum_title),
            leadingIcon = Icons.Filled.Forum,
            onClick = onForumSelected,
        )
        PillListItem(
            title = stringResource(R.string.community_groups_title),
            leadingIcon = Icons.Filled.Groups,
            onClick = onGroupsSelected,
        )
    }
}
