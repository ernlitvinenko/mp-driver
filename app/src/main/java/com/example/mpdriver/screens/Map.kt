package com.example.mpdriver.screens
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.mpdriver.components.ComposableLifecycle
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView as YaMapView



@Composable
fun MapScreen() {
    val ctx = LocalContext.current
    val map: YaMapView = YaMapView(ctx)


    ComposableLifecycle { _, event ->
        when(event) {
            Lifecycle.Event.ON_START -> {
                MapKitFactory.getInstance().onStart()
                Log.d("MapScreen","MAP STARTED")
                map.onStart()
            }
            Lifecycle.Event.ON_STOP -> {
                MapKitFactory.getInstance().onStop()
                Log.d("MapScreen","MAP STOPPED")
                map.onStop()
            }
            Lifecycle.Event.ON_CREATE -> {
                MapKitFactory.initialize(ctx)
                Log.d("MapScreen", "MAP CREATED")
            }
            else -> {}
        }

    }
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { _ ->
            map.map.move(CameraPosition(Point(55.777586, 37.737731), 18.0f, 0.0f, 0.0f))
            return@AndroidView map
        }

    )
}

