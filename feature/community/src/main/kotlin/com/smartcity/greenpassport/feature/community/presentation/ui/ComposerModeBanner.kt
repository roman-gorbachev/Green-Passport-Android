package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.ComposerBanner
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.state.ComposerMode

@Composable
fun composerBanner(mode: ComposerMode): ComposerBanner? = when (mode) {
    ComposerMode.New -> null
    is ComposerMode.Reply -> ComposerBanner(
        icon = Icons.AutoMirrored.Filled.Reply,
        title = stringResource(R.string.replying_to_format, mode.quote.senderName ?: stringResource(R.string.guest)),
        text = if (mode.quote.isDeleted) stringResource(R.string.message_deleted) else mode.quote.text,
    )
    is ComposerMode.Edit -> ComposerBanner(
        icon = Icons.Filled.Edit,
        title = stringResource(R.string.editing_message),
        text = null,
    )
}
