package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

private const val MAX_LINES = 5

@Composable
fun MessageComposer(
    draft: String,
    onDraftChange: (String) -> Unit,
    placeholder: String,
    sendLabel: String,
    isSending: Boolean,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    banner: ComposerBanner? = null,
    cancelBannerLabel: String = "",
    onCancelBanner: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
    ) {
        if (banner != null) {
            ComposerBannerRow(banner = banner, cancelLabel = cancelBannerLabel, onCancel = onCancelBanner)
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = Dimens.CardPadding),
            )
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        ) {
            TextField(
                value = draft,
                onValueChange = onDraftChange,
                placeholder = { Text(placeholder) },
                maxLines = MAX_LINES,
                shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f),
            )
            FilledIconButton(
                onClick = {
                    if (!isSending) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onSend()
                    }
                },
                enabled = draft.isNotBlank() || isSending,
                modifier = Modifier.size(Dimens.PrimaryButtonHeight),
            ) {
                LoadingLabel(isLoading = isSending, color = MaterialTheme.colorScheme.onPrimary) {
                    Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = sendLabel)
                }
            }
        }
    }
}

@Composable
private fun ComposerBannerRow(banner: ComposerBanner, cancelLabel: String, onCancel: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(Dimens.CornerRadiusLarge))
            .padding(start = Dimens.CardPadding),
    ) {
        Icon(imageVector = banner.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = banner.title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            if (banner.text != null) {
                Text(
                    text = banner.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = cancelLabel,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun MessageComposerPreview() {
    GreenPassportTheme {
        MessageComposer(
            draft = "",
            onDraftChange = {},
            placeholder = "Ваше сообщение",
            sendLabel = "Отправить",
            isSending = false,
            onSend = {},
            errorMessage = "Не удалось отправить",
            banner = ComposerBanner(
                icon = Icons.AutoMirrored.Filled.Reply,
                title = "Ответ Ане",
                text = "Кто идёт на субботник?"
            ),
        )
    }
}
