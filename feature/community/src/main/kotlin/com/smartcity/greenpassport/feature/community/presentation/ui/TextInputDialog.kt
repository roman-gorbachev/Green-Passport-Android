package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.GpTextField
import com.smartcity.greenpassport.core.designsystem.component.LoadingLabel
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun TextInputDialog(
    content: TextInputDialogContent,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val errorMessage = content.errorMessage
    val isLoading = content.isLoading
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(content.title) },
        text = {
            GpTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(content.label) },
                isError = errorMessage != null,
                supportingText = errorMessage?.let { message -> { Text(message) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = content.capitalization),
            )
        },
        confirmButton = {
            TextButton(onClick = { if (!isLoading) onConfirm() }, enabled = value.isNotBlank() || isLoading) {
                LoadingLabel(isLoading = isLoading) {
                    Text(content.confirmLabel)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(CoreR.string.cancel))
            }
        },
    )
}
