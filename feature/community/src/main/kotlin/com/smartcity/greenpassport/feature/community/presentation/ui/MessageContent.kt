package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.community.ForwardOrigin
import com.smartcity.greenpassport.core.model.community.MessageQuote
import com.smartcity.greenpassport.feature.community.R

@Composable
fun MessageContent(
    text: String,
    isDeleted: Boolean,
    replyTo: MessageQuote?,
    forwardedFrom: ForwardOrigin?,
    onQuoteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isDeleted) {
        Text(
            text = stringResource(R.string.message_deleted),
            style = MaterialTheme.typography.bodyLarge,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier,
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall), modifier = modifier) {
        if (forwardedFrom != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Forward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.QuoteIconSize),
                )
                Text(
                    text = stringResource(
                        R.string.forwarded_from_format,
                        forwardedFrom.senderName ?: stringResource(R.string.guest),
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (replyTo != null) {
            MessageQuoteRow(quote = replyTo, onClick = { onQuoteClick(replyTo.messageId) })
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
