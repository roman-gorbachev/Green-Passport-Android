package com.smartcity.greenpassport.feature.map.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleStartEffect
import com.smartcity.greenpassport.core.common.resolveForDeviceLanguage
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.map.GeoPoint
import com.smartcity.greenpassport.core.model.map.MapFocus
import com.smartcity.greenpassport.feature.map.R
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import java.lang.ref.WeakReference

private const val CITY_ZOOM = 11f
private const val USER_ZOOM = 14f
private const val DEFAULT_AZIMUTH = 0f
private const val DEFAULT_TILT = 0f

@Composable
fun YandexMap(
    points: List<MapPoint>,
    focus: MapFocus?,
    onPointClick: (String) -> Unit,
    bottomPadding: Dp,
    logoBottomPaddingPx: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val pinRenderer = remember { MapPinRenderer(context) }
    val userLocationLayer = remember { MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow) }
    val state = remember { YandexMapState() }
    val currentOnPointClick by rememberUpdatedState(onPointClick)
    val showsUserLocation = focus is MapFocus.UserLocation

    LifecycleStartEffect(mapView) {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
        onStopOrDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { mapView },
            update = { view ->
                val map = view.mapWindow.map
                map.logo.setPadding(Padding(0, logoBottomPaddingPx))
                userLocationLayer.isVisible = showsUserLocation

                if (state.appliedPoints != points) {
                    state.appliedPoints = points
                    map.mapObjects.clear()
                    state.tapListeners.clear()
                    points.forEach { point ->
                        val listener = MapObjectTapListener { _, _ ->
                            currentOnPointClick(point.id)
                            true
                        }
                        state.tapListeners += listener
                        val pin = pinRenderer.render(point.name.resolveForDeviceLanguage())
                        map.mapObjects.addPlacemark().apply {
                            geometry = Point(point.latitude, point.longitude)
                            setIcon(ImageProvider.fromBitmap(pin.bitmap), IconStyle().setAnchor(pin.anchor))
                            addTapListener(WeakReference(listener))
                        }
                    }
                }

                if (focus != null && !state.isCameraPositioned) {
                    state.isCameraPositioned = true
                    map.move(cameraPosition(focus.point, if (showsUserLocation) USER_ZOOM else CITY_ZOOM))
                }
            },
        )
        if (showsUserLocation) {
            FilledTonalIconButton(
                onClick = {
                    val target = userLocationLayer.cameraPosition()?.target
                        ?: focus?.point?.let { Point(it.latitude, it.longitude) }
                    if (target != null) {
                        mapView.mapWindow.map.move(CameraPosition(target, USER_ZOOM, DEFAULT_AZIMUTH, DEFAULT_TILT))
                    }
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimens.ScreenHorizontalPadding, bottom = bottomPadding + Dimens.SpacingMedium)
                    .size(Dimens.BackButtonSize),
            ) {
                Icon(imageVector = Icons.Filled.MyLocation, contentDescription = stringResource(R.string.my_location))
            }
        }
    }
}

private class YandexMapState {
    var appliedPoints: List<MapPoint>? = null
    var isCameraPositioned = false
    val tapListeners = mutableListOf<MapObjectTapListener>()
}

private fun cameraPosition(point: GeoPoint, zoom: Float): CameraPosition =
    CameraPosition(Point(point.latitude, point.longitude), zoom, DEFAULT_AZIMUTH, DEFAULT_TILT)
