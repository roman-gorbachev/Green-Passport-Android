package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.GpDropdownMenu
import com.smartcity.greenpassport.core.designsystem.component.GpDropdownMenuItem
import com.smartcity.greenpassport.feature.community.R

@Composable
fun CommunityAddButton(
    onCreateGroup: () -> Unit,
    onJoinByCode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(onClick = { isExpanded = true }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.create_group),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        GpDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            GpDropdownMenuItem(
                text = stringResource(R.string.create_group),
                icon = Icons.Filled.GroupAdd,
                onClick = {
                    isExpanded = false
                    onCreateGroup()
                },
            )
            GpDropdownMenuItem(
                text = stringResource(R.string.join_by_code),
                icon = Icons.Filled.Pin,
                onClick = {
                    isExpanded = false
                    onJoinByCode()
                },
            )
        }
    }
}
