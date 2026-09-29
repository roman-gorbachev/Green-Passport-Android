package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.smartcity.greenpassport.core.R
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

@Composable
fun MascotWidget(
    modifier: Modifier = Modifier,
    size: Dp = Dimens.ListRowMascotSize,
) {
    Image(
        painter = painterResource(R.drawable.mascot),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size),
    )
}

@Preview
@Composable
private fun MascotWidgetPreview() {
    GreenPassportTheme {
        MascotWidget(size = Dimens.EmptyStateMascotSize)
    }
}
