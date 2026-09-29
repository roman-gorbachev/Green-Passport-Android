package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

@Composable
fun GpPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.CornerRadiusPill),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dimens.SpacingNone,
            pressedElevation = Dimens.SpacingNone,
        ),
        contentPadding = PaddingValues(horizontal = Dimens.SpacingLarge),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.PrimaryButtonHeight),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview
@Composable
private fun GpPrimaryButtonPreview() {
    GreenPassportTheme {
        GpPrimaryButton(text = "Отметить выполненной", onClick = {})
    }
}
