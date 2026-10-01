package com.smartcity.greenpassport.feature.map.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.util.TypedValue
import com.smartcity.greenpassport.feature.map.R

class MapPinRenderer(private val context: Context) {

    private val density = context.resources.displayMetrics.density
    private val pin: Bitmap by lazy { drawablePin() }
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = LABEL_TEXT_COLOR
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            LABEL_TEXT_SIZE_SP,
            context.resources.displayMetrics,
        )
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        setShadowLayer(SHADOW_RADIUS_DP * density, 0f, SHADOW_OFFSET_DP * density, SHADOW_COLOR)
    }

    fun render(title: String): RenderedPin {
        val label = TextUtils.ellipsize(
            title,
            textPaint,
            LABEL_MAX_WIDTH_DP * density,
            TextUtils.TruncateAt.END
        ).toString()
        val horizontalPadding = LABEL_HORIZONTAL_PADDING_DP * density
        val verticalPadding = LABEL_VERTICAL_PADDING_DP * density
        val shadowMargin = (SHADOW_RADIUS_DP + SHADOW_OFFSET_DP) * density
        val textWidth = textPaint.measureText(label)
        val metrics = textPaint.fontMetrics
        val labelWidth = textWidth + horizontalPadding * 2
        val labelHeight = metrics.descent - metrics.ascent + verticalPadding * 2
        val gap = LABEL_GAP_DP * density

        val width = maxOf(pin.width.toFloat(), labelWidth + shadowMargin * 2)
        val height = pin.height + gap + labelHeight + shadowMargin * 2
        val bitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawBitmap(pin, (width - pin.width) / 2, 0f, null)
        val labelTop = pin.height + gap
        val labelRect = RectF((width - labelWidth) / 2, labelTop, (width + labelWidth) / 2, labelTop + labelHeight)
        val radius = labelHeight / 2
        canvas.drawRoundRect(labelRect, radius, radius, labelPaint)
        canvas.drawText(
            label,
            labelRect.left + horizontalPadding,
            labelRect.top + verticalPadding - metrics.ascent,
            textPaint
        )

        return RenderedPin(bitmap = bitmap, anchor = PointF(HALF, pin.height / 2f / height))
    }

    private fun drawablePin(): Bitmap {
        val drawable = checkNotNull(context.getDrawable(R.drawable.ic_map_pin))
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)
        drawable.draw(Canvas(bitmap))
        return bitmap
    }

    class RenderedPin(val bitmap: Bitmap, val anchor: PointF)

    companion object {
        private const val HALF = 0.5f
        private const val LABEL_TEXT_SIZE_SP = 12f
        private const val LABEL_MAX_WIDTH_DP = 140f
        private const val LABEL_HORIZONTAL_PADDING_DP = 8f
        private const val LABEL_VERTICAL_PADDING_DP = 4f
        private const val LABEL_GAP_DP = 4f
        private const val SHADOW_RADIUS_DP = 3f
        private const val SHADOW_OFFSET_DP = 1f
        private const val SHADOW_COLOR = 0x33000000
        private const val LABEL_TEXT_COLOR = 0xFF1C1C1E.toInt()
    }
}
