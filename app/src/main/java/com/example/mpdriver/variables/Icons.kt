package com.example.mpdriver.variables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.mpdriver.R


sealed class Icons {
    data object CarTemperatureIcons {
        @Composable fun HOT(modifier: Modifier = Modifier) {
            Image(painterResource(id = R.drawable.hottransport), contentDescription = "", modifier = modifier)
        }
        @Composable fun COLD(modifier: Modifier = Modifier) {
            Image(painterResource(id = R.drawable.coltransport), contentDescription = "", modifier = modifier)
        }
    }
}
