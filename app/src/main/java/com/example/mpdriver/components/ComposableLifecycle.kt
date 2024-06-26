package com.example.mpdriver.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun ComposableLifecycle(modifier: Modifier = Modifier, lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current, onEvent: (LifecycleOwner, Lifecycle.Event) ->  Unit) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver {source, event ->
            onEvent(source, event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

