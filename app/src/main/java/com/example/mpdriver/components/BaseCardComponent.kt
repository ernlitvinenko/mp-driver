package com.example.mpdriver.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mpdriver.variables.JDEColor

@Preview(showBackground = true)
@Composable
fun CardComponent(modifier: Modifier = Modifier, containerColor: JDEColor = JDEColor.SECONDARY, content: @Composable () -> Unit = {}) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, containerColor.color, RoundedCornerShape(10.dp))
            .padding(horizontal = 15.dp, vertical = 15.dp)
    ) {
        content()
    }
}