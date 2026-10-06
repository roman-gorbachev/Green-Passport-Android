package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.ui.text.input.KeyboardCapitalization

data class TextInputDialogContent(
    val title: String,
    val label: String,
    val confirmLabel: String,
    val errorMessage: String?,
    val isLoading: Boolean,
    val capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
)
