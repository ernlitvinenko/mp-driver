package com.example.mpdriver.components.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mpdriver.components.InformationPlaceholderBig


@Composable
fun FeedTaskDataCard(
    modifier: Modifier = Modifier,
    title: String = "Запланированные задачи",
    count: Int = 2,
    date: String = "30.11.2023",
    dateDescription: String = "Ближайшая",
    buttonLabel: String = "Смотреть запланированные задачи",
    navigateTo: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            InformationPlaceholderBig(
                mainText = "$count",
                subText = "Количество",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(0.1f))
            InformationPlaceholderBig(
                mainText = date,
                subText = dateDescription,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = { navigateTo() },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = buttonLabel, fontSize = 15.sp)
                Spacer(modifier = Modifier.weight(0.1f))
                Icon(Icons.Filled.ArrowForward, contentDescription = "Arrow Forward")
            }
        }
    }
}