package com.smartcity.greenpassport.feature.shop.presentation.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

private const val QR_SIZE_PX = 512
private const val QUIET_ZONE_MODULES = 1
private const val DARK_PIXEL = 0xFF000000.toInt()
private const val LIGHT_PIXEL = 0xFFFFFFFF.toInt()

@Composable
fun QrCodeImage(
    payload: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val bitmap = remember(payload) { qrBitmap(payload) }
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = contentDescription,
        filterQuality = FilterQuality.None,
        modifier = modifier,
    )
}

private fun qrBitmap(payload: String): Bitmap {
    val hints = mapOf(
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
        EncodeHintType.MARGIN to QUIET_ZONE_MODULES,
    )
    val matrix = QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, QR_SIZE_PX, QR_SIZE_PX, hints)
    val pixels = IntArray(matrix.width * matrix.height) { index ->
        if (matrix.get(index % matrix.width, index / matrix.width)) DARK_PIXEL else LIGHT_PIXEL
    }
    return Bitmap.createBitmap(pixels, matrix.width, matrix.height, Bitmap.Config.ARGB_8888)
}
