package com.example.mpdriver.components.note

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.JDEButton


@Preview(showBackground = true)
@Composable
fun NoteComponent(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .border(2.dp, color = Color.Gray, shape = RoundedCornerShape(10.dp))
            .padding(15.dp)) {
        Text(text = "Изменился статус события", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "Изменился статус события\n“Прибыть на ПГР 20.12.2023 к 10:00”", fontSize = 15.sp, fontWeight = FontWeight.Normal, color = Color.Gray)
        Spacer(modifier = Modifier.height(10.dp))
        ActiveButton(onClick = { /*TODO*/ }, text = "Перейти к задаче", modifier = Modifier.fillMaxWidth())
    }
}