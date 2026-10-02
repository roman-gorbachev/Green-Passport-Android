package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
    ) {
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
                onClick = { if (!isSending) onSend() },
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
        )
    }
}
