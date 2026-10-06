package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.create_group)) },
                leadingIcon = { Icon(imageVector = Icons.Filled.GroupAdd, contentDescription = null) },
                onClick = {
                    isExpanded = false
                    onCreateGroup()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.join_by_code)) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Pin, contentDescription = null) },
                onClick = {
                    isExpanded = false
                    onJoinByCode()
                },
            )
        }
    }
}
