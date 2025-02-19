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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.timeFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime


@Preview(showBackground = true)
@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String = "Лента",
    backLink: Boolean = false,
    openSettingsAction: () -> Unit = {},
    navigateUp: () -> Unit = {},
) {


     var now by remember {
         mutableStateOf("")
     }
    TimeTickReciever.registerHandler {
        now = Clock.System.now().toLocalDateTime(timeZone = UtcOffset(hours = 3).asTimeZone()).format(
            timeFormat.toKotlin()
        )
    }

    LaunchedEffect(Unit) {
        now = Clock.System.now().toLocalDateTime(timeZone = UtcOffset(hours = 3).asTimeZone()).format(
            timeFormat.toKotlin()
        )
    }

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
        Column {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                        text = if (title.length < 11)  title else title.slice(0..8) + "...",
                        modifier = Modifier,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!backLink) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "$now МСК", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = JDEColor.PRIMARY.color)

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
                        }

                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }


                }
                if (!backLink) {


                }
            }

        }

    }
}
