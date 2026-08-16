package com.smartcity.greenpassport.feature.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.feature.profile.R

@Composable
fun ExchangeScreen(modifier: Modifier = Modifier) {
    EmptyContent(
        message = stringResource(R.string.exchange_unavailable_message),
        modifier = modifier,
    )
}
