package com.example.mpdriver.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


enum class ButtonType {
    DANGER,
    WARNING,
    SUCCESS,
    DEFAULT
}

@Composable
fun ActiveButton(onClick: () -> Unit, modifier: Modifier = Modifier, text: String) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun StaleButton(onClick: () -> Unit, modifier: Modifier = Modifier, text: String) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF2F2F2),
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = text)
    }
}


@Composable
fun IteractionButton(onClick: () -> Unit, child: @Composable () -> Unit) {
    Button(onClick = onClick, shape = RectangleShape, contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(contentColor = Color.Black, containerColor = Color.Transparent)) {
        child()
    }
}


@Preview(showBackground = true)
@Composable
fun JDEButton(modifier: Modifier = Modifier,
              onClick: () -> Unit = {},
              type: ButtonType = ButtonType.DEFAULT,
              content: @Composable () -> Unit = { Text(
    text = "Button",
)}) {
    Button(onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black), contentPadding = PaddingValues(0.dp), shape = RectangleShape
        ) {
        Row(modifier
            .fillMaxWidth()
            .border(2.dp,
                when(type) {
                    ButtonType.DANGER -> Color(0xFFE5332A)
                    ButtonType.WARNING -> Color(0xFFFFC700)
                    ButtonType.SUCCESS -> Color(0xFF45900B)
                    ButtonType.DEFAULT -> Color.Gray
                },
                RoundedCornerShape(10.dp))
            .padding(15.dp)) {
            content()
        }
    }
}