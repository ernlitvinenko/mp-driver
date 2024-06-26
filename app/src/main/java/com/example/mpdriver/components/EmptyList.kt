package com.example.mpdriver.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.R


@Composable
fun EmptyList(modifier: Modifier = Modifier, text: String = "") {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.zoom_icon),
            contentDescription = "No Active Task",
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
    return
}