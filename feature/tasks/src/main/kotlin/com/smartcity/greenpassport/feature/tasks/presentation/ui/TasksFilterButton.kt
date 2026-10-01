package com.smartcity.greenpassport.feature.tasks.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.feature.tasks.R

@Composable
fun TasksFilterButton(
    activeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        BadgedBox(
            badge = {
                if (activeCount > 0) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) { Text(text = activeCount.toString()) }
                }
            },
        ) {
            Icon(
                imageVector = if (activeCount > 0) Icons.Filled.FilterAlt else Icons.Outlined.FilterAlt,
                contentDescription = stringResource(R.string.filters),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
