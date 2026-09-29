package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.R
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

@Composable
fun GpBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GpSurfaceCard(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier.size(Dimens.BackButtonSize),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
            contentDescription = stringResource(R.string.back),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(start = Dimens.BackIconOpticalOffset)
                .padding(Dimens.SpacingSmall + Dimens.SpacingExtraSmall)
                .size(Dimens.IconSizeSmall),
        )
    }
}
