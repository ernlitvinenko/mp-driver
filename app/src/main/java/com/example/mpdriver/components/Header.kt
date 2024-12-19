package com.example.mpdriver.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String = "Лента",
    backLink: Boolean = false,
    openSettingsAction: () -> Unit = {},
    navigateUp: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .height(100.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (backLink) {
                IconButton(onClick = { navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
            Text(
                text = title,
                modifier = Modifier,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (!backLink) {
                TextButton(
                    onClick = { openSettingsAction() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black,
                    )
                ) {
                    Icon(
                        Icons.Sharp.Settings,
                        contentDescription = "Settings",
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }


        }
    }
}
