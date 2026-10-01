package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.component.ListSectionRow
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.community.R

@Composable
fun CommunityHubScreen(
    onForumSelected: () -> Unit,
    onGroupsSelected: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    ListSection(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingMedium),
    ) {
        ListSectionRow(
            title = stringResource(R.string.community_forum_title),
            leading = { SymbolTile(icon = Icons.Filled.Forum) },
            onClick = onForumSelected,
            showDivider = true,
        )
        ListSectionRow(
            title = stringResource(R.string.community_groups_title),
            leading = { SymbolTile(icon = Icons.Filled.Groups) },
            onClick = onGroupsSelected,
        )
    }
}
