package com.smartcity.greenpassport.feature.map.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class UserLocationDotRenderer(context: Context) {

    private val density = context.resources.displayMetrics.density

    fun render(dotColor: Int): Bitmap {
        val outerRadius = OUTER_RADIUS_DP * density
        val innerRadius = INNER_RADIUS_DP * density
        val shadowRadius = SHADOW_RADIUS_DP * density
        val shadowOffset = SHADOW_OFFSET_DP * density
        val size = ((outerRadius + shadowRadius + shadowOffset) * 2).toInt()
        val center = size / 2f
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            setShadowLayer(shadowRadius, 0f, shadowOffset, SHADOW_COLOR)
        }
        val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = dotColor }
        canvas.drawCircle(center, center, outerRadius, outerPaint)
        canvas.drawCircle(center, center, innerRadius, innerPaint)
        return bitmap
    }

    companion object {
        private const val OUTER_RADIUS_DP = 11f
        private const val INNER_RADIUS_DP = 8f
        private const val SHADOW_RADIUS_DP = 3f
        private const val SHADOW_OFFSET_DP = 1f
        private const val SHADOW_COLOR = 0x40000000
    }
}
