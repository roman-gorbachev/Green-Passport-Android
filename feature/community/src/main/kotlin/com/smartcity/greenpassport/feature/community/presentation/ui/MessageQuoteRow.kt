package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.community.MessageQuote
import com.smartcity.greenpassport.feature.community.R

@Composable
fun MessageQuoteRow(
    quote: MessageQuote,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .width(Dimens.QuoteBarWidth)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(Dimens.QuoteBarWidth)),
        )
        Column {
            Text(
                text = quote.senderName ?: stringResource(R.string.guest),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = if (quote.isDeleted) stringResource(R.string.message_deleted) else quote.text,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = if (quote.isDeleted) FontStyle.Italic else FontStyle.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
