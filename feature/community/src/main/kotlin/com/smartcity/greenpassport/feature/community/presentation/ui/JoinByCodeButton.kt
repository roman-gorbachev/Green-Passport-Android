package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.feature.community.R

@Composable
fun JoinByCodeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Tag,
            contentDescription = stringResource(R.string.join_by_code),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}
