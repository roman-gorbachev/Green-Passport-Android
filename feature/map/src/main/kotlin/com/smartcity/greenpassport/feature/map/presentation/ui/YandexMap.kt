package com.smartcity.greenpassport.feature.map.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleStartEffect
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.feature.map.R
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import java.lang.ref.WeakReference

private const val DEFAULT_ZOOM = 11f
private const val DEFAULT_AZIMUTH = 0f
private const val DEFAULT_TILT = 0f

@Composable
fun YandexMap(
    points: List<MapPoint>,
    onPointClick: (String) -> Unit,
    logoBottomPaddingPx: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val pinIcon = remember { ImageProvider.fromBitmap(pinBitmap(context)) }
    val tapListeners = remember { mutableListOf<MapObjectTapListener>() }
    val currentOnPointClick by rememberUpdatedState(onPointClick)
    var isCameraPositioned by remember { mutableStateOf(false) }

    LifecycleStartEffect(mapView) {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
        onStopOrDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            val map = view.mapWindow.map
            map.logo.setPadding(Padding(0, logoBottomPaddingPx))

            val mapObjects = map.mapObjects
            mapObjects.clear()
            tapListeners.clear()
            points.forEach { point ->
                val listener = MapObjectTapListener { _, _ ->
                    currentOnPointClick(point.id)
                    true
                }
                tapListeners += listener
                mapObjects.addPlacemark().apply {
                    geometry = Point(point.latitude, point.longitude)
                    setIcon(pinIcon)
                    setText(point.name)
                    addTapListener(WeakReference(listener))
                }
            }

            if (!isCameraPositioned && points.isNotEmpty()) {
                map.move(
                    CameraPosition(
                        Point(points.map { it.latitude }.average(), points.map { it.longitude }.average()),
                        DEFAULT_ZOOM,
                        DEFAULT_AZIMUTH,
                        DEFAULT_TILT,
                    ),
                )
                isCameraPositioned = true
            }
        },
    )
}

private fun pinBitmap(context: Context): Bitmap {
    val drawable = checkNotNull(context.getDrawable(R.drawable.ic_map_pin))
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    drawable.setBounds(0, 0, bitmap.width, bitmap.height)
    drawable.draw(Canvas(bitmap))
    return bitmap
}
