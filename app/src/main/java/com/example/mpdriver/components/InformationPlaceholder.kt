package com.example.mpdriver.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview(showBackground = true)
@Composable
fun InformationPlaceholderBig(
    modifier: Modifier = Modifier,
    mainText: String = "main text",
    subText: String = "Subtext"
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEEEEEE))
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(10.dp)),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally

    ) {
        Text(
            text = mainText,
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Text(
            text = subText,
            fontSize = 15.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InformationPlaceholderSmall(
    modifier: Modifier = Modifier,
    mainText: String = "main text",
    subText: String = "Subtext",
    children: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEEEEEE))
            .padding(vertical = 12.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp)),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = subText,
                fontSize = 15.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                color = Color.Gray
            )
            Text(
                text = mainText,
                fontSize = 22.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

        }
        children()
    }

}


