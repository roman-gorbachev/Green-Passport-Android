package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun ConfirmHapticOnSuccess(
    inProgress: Boolean,
    succeeded: Boolean,
) {
    val haptics = LocalHapticFeedback.current
    var wasInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(inProgress, succeeded) {
        if (wasInProgress && !inProgress && succeeded) {
            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        }
        wasInProgress = inProgress
    }
}
