package com.smartcity.greenpassport.feature.map.presentation.ui

import android.graphics.Bitmap
import android.graphics.PointF
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider

class UserLocationDotListener(private val dot: Bitmap) : UserLocationObjectListener {

    override fun onObjectAdded(view: UserLocationView) {
        val icon = ImageProvider.fromBitmap(dot)
        val style = IconStyle().setAnchor(PointF(HALF, HALF)).setFlat(false)
        view.arrow.setIcon(icon, style)
        view.pin.setIcon(icon, style)
        view.accuracyCircle.isVisible = false
    }

    override fun onObjectRemoved(view: UserLocationView) = Unit

    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) = Unit

    companion object {
        private const val HALF = 0.5f
    }
}
