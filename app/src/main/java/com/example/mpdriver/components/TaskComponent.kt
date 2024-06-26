package com.example.mpdriver.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.R
import com.example.mpdriver.api.Subtasks
import com.example.mpdriver.api.TaskResponse
import com.example.mpdriver.recievers.TimeTickReciever
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.until
import kotlin.math.abs


enum class TaskStatus {
    DANGER,
    SUCCESS,
    WARNING,
    DEFAULT
}

@Preview(showBackground = true)
@Composable
fun Task(modifier: Modifier = Modifier,
         children: @Composable() ()->Unit = {},
         taskResponse: TaskResponse = TaskResponse(1, "2024-06-01T00:00", "2024-06-10T00:00"),
         footerButton: @Composable ()-> Unit = {}) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var nowTime by remember {
        mutableStateOf(Clock.System.now())
    }

    TimeTickReciever.registerHandler {
        nowTime = Clock.System.now()
    }

    val startPln = LocalDateTime.parse(taskResponse.startPln!!)
    val endPln = LocalDateTime.parse(taskResponse.endPln!!)

    val dateFormat = LocalDateTime.Format {
        byUnicodePattern("d.MM.yyyy")
    }

    val timeFormat = LocalDateTime.Format {
        byUnicodePattern("HH:mm")
    }

    val leastBase = nowTime.until(
        endPln.toInstant(offset = UtcOffset(3)),
        timeZone = UtcOffset(3).asTimeZone(), unit = DateTimeUnit.MINUTE)

    val isDelay = leastBase != abs(leastBase)
    val leastDays = abs( leastBase) / 60 / 24
    val leastHours =  abs(leastBase) / 60 - leastDays * 24
    val leastMinutes = abs(leastBase) - leastDays * 60 * 24 - leastHours * 60


    val status = when {
        taskResponse.status == "Completed" && !isDelay -> TaskStatus.SUCCESS
        taskResponse.status == "Completed" && isDelay -> TaskStatus.WARNING
        taskResponse.status == "Cancelled" -> TaskStatus.WARNING
        taskResponse.status == "InProgress" || taskResponse.status == "NotDefined"  && isDelay -> TaskStatus.DANGER
        else -> TaskStatus.DEFAULT
    }



    Column(
        modifier
            .border(
                2.dp, when (status) {
                    TaskStatus.DEFAULT -> Color.Gray
                    TaskStatus.SUCCESS -> Color(0xFF45900B)
                    TaskStatus.DANGER -> Color(0xFFE5332A)
                    TaskStatus.WARNING -> Color(0xFFFFC700)
                }, RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Движение по маршруту\n${taskResponse.route?.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(10.dp))

            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(0.1f)) {
                Image(
                    painter = painterResource(id = R.drawable.tick_default),
                    contentDescription = "tick"
                )
            }
        }
        Row(Modifier.padding(bottom = 20.dp)) {
            InformationPlaceholderSmall(
                Modifier.weight(1f),
                mainText = "${taskResponse.route?.truck?.gost}",
                subText = "Номер ТС"
            )
            Spacer(modifier = Modifier.width(10.dp))
            InformationPlaceholderSmall(
                Modifier.weight(1f),
                mainText = "${if (taskResponse.route!!.trailer != null) taskResponse.route!!.trailer!!.gost else "-"}",
                subText = "Номер прицепа"
            )
        }
        InformationPlaceholderSmall(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            mainText = if (taskResponse.route!!.temperatureProperty == 1) "Горячая" else "Холодная",
            subText = "Тип перевозки"
        ) {
            when (taskResponse.route!!.temperatureProperty) {
                1 -> Image(painter = painterResource(id = R.drawable.hottransport), contentDescription = "" )
                else -> Image(painter = painterResource(id = R.drawable.coltransport), contentDescription = "" )
            }

        }
        children()
        AnimatedVisibility(visible = expanded) {
            Column {
                InformationPlaceholderSmall(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    mainText = "${taskResponse.text}",
                    subText = "Полный маршрут"
                )

                Text(
                    modifier = Modifier.padding(bottom = 5.dp),
                    text = "Начало маршрута",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Row(Modifier.padding(bottom = 20.dp)) {
                    InformationPlaceholderSmall(
                        Modifier.weight(2f),
                        mainText = dateFormat.format(startPln),
                        subText = "Дата"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    InformationPlaceholderSmall(
                        Modifier.weight(1f),
                        mainText = timeFormat.format(startPln),
                        subText = "Время"
                    )
                }

                Text(
                    modifier = Modifier.padding(bottom = 5.dp),
                    text = "Конец маршрута",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Row(Modifier.padding(bottom = 20.dp)) {
                    InformationPlaceholderSmall(
                        Modifier.weight(2f),
                        mainText = dateFormat.format(endPln),
                        subText = "Дата"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    InformationPlaceholderSmall(
                        Modifier.weight(1f),
                        mainText = timeFormat.format(endPln),
                        subText = "Время"
                    )
                }

                Text(
                    modifier = Modifier.padding(bottom = 5.dp),
                    text = if (isDelay) "Задержка" else "Осталось времени до конца маршрута",
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isDelay) 20.sp else 14.sp,
                    color = if (isDelay) Color(0xFFE5332A) else Color.Black
                )
                Row(Modifier.padding(bottom = 20.dp)) {
                    InformationPlaceholderSmall(
                        Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                color = if (isDelay) Color(0xFFE5332A) else Color.Gray,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        mainText = leastDays.toString(),
                        subText = "Дни"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    InformationPlaceholderSmall(
                        Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                color = if (isDelay) Color(0xFFE5332A) else Color.Gray,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        mainText = leastHours.toString(),
                        subText = "Часы"
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    InformationPlaceholderSmall(
                        Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                color = if (isDelay) Color(0xFFE5332A) else Color.Gray,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        mainText = leastMinutes.toString(),
                        subText = "Минуты"
                    )
                }

            }
        }
        Divider(thickness = 2.dp, color = Color.Gray)
        TextButton(onClick = { expanded = !expanded }, colors = ButtonDefaults.buttonColors(contentColor = Color.Black, containerColor = Color.Transparent)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = if (expanded) "Скрыть" else "Развернуть", fontSize = 16.sp)
                Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Chevron")
            }
        }
        footerButton()
    }
}


