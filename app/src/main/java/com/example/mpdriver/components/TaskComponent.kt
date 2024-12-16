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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.R
import com.example.mpdriver.data.models.*
import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.datetimeFormatFrom
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.until
import kotlin.math.abs

enum class TaskColor {
    DANGER,
    SUCCESS,
    WARNING,
    DEFAULT
}

@Preview(showBackground = true)
@Composable
fun TaskComponent(
    modifier: Modifier = Modifier,
    taskData: AppTask = AppTask(
        id = 1125900288324087,
        startPln = "10.04.2024 23:00:00",
        endPln = "13.04.2024 20:00:00",
        startFact = null,
        endFact = null,
        status = TaskStatus.COMPLETED,
        taskType = TaskType.MOV_MARSH,
        text = "Москва-Южный->Нижний Новгород->Чебоксары->Казань->Пермь->Екатеринбург",
        route = AppMarshResponse(
            id = 2252182222363240,
            temperatureProperty = MarshTemperatureProperty.COLD,
            name = "Москва-Южный->Екатеринбург",
            trailer = AppTRSResponse(id = 2252083434660096, gost = "М985АО550"),
            truck = AppTRSResponse(id = 2252096270239379, gost = "ХУ838177")
        ),
        events = null,
        subtasks = null,
        station = null
    ),
    children: @Composable() () -> Unit = {},
    footerButton: @Composable () -> Unit = {}
) {

    val dateFormat = LocalDateTime.Format {
        byUnicodePattern("d.MM.yyyy")
    }

    val timeFormat = LocalDateTime.Format {
        byUnicodePattern("HH:mm")
    }


    var expanded by remember {
        mutableStateOf(false)
    }

    var nowTime by remember {
        mutableStateOf(Clock.System.now())
    }

    TimeTickReciever.registerHandler {
        nowTime = Clock.System.now()
    }

    val startPln = LocalDateTime.parse(taskData.startPln, datetimeFormatFrom)
    val endPln = LocalDateTime.parse(taskData.endPln, datetimeFormatFrom)

    val leastBase = nowTime.until(
        endPln.toInstant(offset = UtcOffset(3)),
        timeZone = UtcOffset(3).asTimeZone(), unit = DateTimeUnit.MINUTE
    )

    val isDelay = leastBase != abs(leastBase)
    val leastDays = abs(leastBase) / 60 / 24
    val leastHours = abs(leastBase) / 60 - leastDays * 24
    val leastMinutes = abs(leastBase) - leastDays * 60 * 24 - leastHours * 60

    val status: TaskColor = when (taskData.status) {
        TaskStatus.COMPLETED -> if (!isDelay) TaskColor.SUCCESS else TaskColor.WARNING
        TaskStatus.CANCELLED -> TaskColor.WARNING
        TaskStatus.IN_PROGRESS -> if (isDelay) TaskColor.DANGER else TaskColor.DEFAULT
        TaskStatus.NOT_DEFINED -> if (isDelay) TaskColor.DANGER else TaskColor.DEFAULT
    }


    return Column(
        modifier
            .border(
                2.dp, when (status) {
                    TaskColor.DEFAULT -> JDEColor.SECONDARY.color
                    TaskColor.SUCCESS -> JDEColor.SUCCESS.color
                    TaskColor.DANGER -> JDEColor.PRIMARY.color
                    TaskColor.WARNING -> JDEColor.WARNING.color
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
                text = "Движение по маршруту\n${taskData.route?.name}",
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
                mainText = "${if (taskData.route?.truck != null) taskData.route.truck.gost else "-"}",
                subText = "Номер ТС"
            )
            Spacer(modifier = Modifier.width(10.dp))
            InformationPlaceholderSmall(
                Modifier.weight(1f),
                mainText = "${if (taskData.route?.trailer != null) taskData.route.trailer.gost else "-"}",
                subText = "Номер прицепа"
            )
        }
        InformationPlaceholderSmall(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            mainText = when (taskData.route?.temperatureProperty) {
                MarshTemperatureProperty.COLD -> "Холодная"
                MarshTemperatureProperty.HOT -> "Теплая"
                else -> "Не определен"
            },
            subText = "Тип перевозки"
        ) {
            when (taskData.route?.temperatureProperty) {
                MarshTemperatureProperty.HOT -> Image(
                    painter = painterResource(id = R.drawable.hottransport),
                    contentDescription = ""
                )

                else -> Image(
                    painter = painterResource(id = R.drawable.coltransport),
                    contentDescription = ""
                )
            }

        }
        children()
        AnimatedVisibility(visible = expanded) {
            Column {
                InformationPlaceholderSmall(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    mainText = taskData.text,
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
                    color = if (isDelay) JDEColor.PRIMARY.color else JDEColor.SECONDARY.color
                )
                Row(Modifier.padding(bottom = 20.dp)) {
                    InformationPlaceholderSmall(
                        Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                color = if (isDelay) JDEColor.PRIMARY.color else JDEColor.SECONDARY.color,
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
                                color = if (isDelay) JDEColor.PRIMARY.color else JDEColor.SECONDARY.color,
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
                                color = if (isDelay) JDEColor.PRIMARY.color else JDEColor.SECONDARY.color,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        mainText = leastMinutes.toString(),
                        subText = "Минуты"
                    )
                }

            }
        }
        HorizontalDivider(thickness = 2.dp, color = Color.Gray)
        TextButton(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = Color.Transparent
            )
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = if (expanded) "Скрыть" else "Развернуть", fontSize = 16.sp)
                Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Chevron")
            }
        }
        footerButton()


    }
}
