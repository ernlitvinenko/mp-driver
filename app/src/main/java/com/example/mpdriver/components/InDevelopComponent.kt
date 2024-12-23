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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.R


@Composable
fun InDevelopmentComponent(modifier: Modifier = Modifier, navigateHome: () -> Unit) {

    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 80.dp), painter = painterResource(id = R.drawable.in_development), contentDescription = "In development icon")
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Данный раздел находится в разработке", fontWeight = FontWeight.Bold, fontSize = 30.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Просим прощение за доставленные неудобства")
        Spacer(modifier = Modifier.height(10.dp))
        ActiveButton(modifier = Modifier.fillMaxWidth(), text = "На главную", onClick = {navigateHome()})
    }
}