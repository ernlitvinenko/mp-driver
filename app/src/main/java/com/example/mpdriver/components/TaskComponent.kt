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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.GetTaskByIdQuery
import com.example.mpdriver.NotificationApplication
import com.example.mpdriver.R
//import com.example.mpdriver.storage.api.apolloClient
import com.example.mpdriver.recievers.TimeTickReciever
//import com.example.mpdriver.storage.Database
import com.example.mpdriver.type.MarshTemperaturePropertyQL
import com.example.mpdriver.type.StatusEnumQl
import com.example.mpdriver.variables.JDEColor
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
fun Task(
    modifier: Modifier = Modifier,
    children: @Composable() () -> Unit = {},
    task_id: Long = 1,
    footerButton: @Composable () -> Unit = {}
) {
    var taskResponse by remember {
        mutableStateOf<GetTaskByIdQuery.Data?>(null)
    }

    val ctx = LocalContext.current
//    val database = (ctx.applicationContext as NotificationApplication).db
    LaunchedEffect(Unit) {
        var task: GetTaskByIdQuery.Task
//        taskResponse = apolloClient.query(GetTaskByIdQuery(task_id.toString(), "1125904232173609")).execute().data

    }

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

    taskResponse?.let {
        val startPln = LocalDateTime.parse(it.task!!.startPln.toString())
        val endPln = LocalDateTime.parse(it.task.endPln.toString())
        val leastBase = nowTime.until(
            endPln.toInstant(offset = UtcOffset(3)),
            timeZone = UtcOffset(3).asTimeZone(), unit = DateTimeUnit.MINUTE
        )

        val isDelay = leastBase != abs(leastBase)
        val leastDays = abs(leastBase) / 60 / 24
        val leastHours = abs(leastBase) / 60 - leastDays * 24
        val leastMinutes = abs(leastBase) - leastDays * 60 * 24 - leastHours * 60


        val status = when {
            it.task.status == StatusEnumQl.COMPLETED && !isDelay -> TaskStatus.SUCCESS
            it.task.status == StatusEnumQl.COMPLETED && isDelay -> TaskStatus.WARNING
            it.task.status == StatusEnumQl.CANCELLED -> TaskStatus.WARNING
            (it.task.status == StatusEnumQl.IN_PROGRESS || it.task.status == StatusEnumQl.NOT_DEFINED) && isDelay -> TaskStatus.DANGER
            else -> TaskStatus.DEFAULT
        }


        return Column(
            modifier
                .border(
                    2.dp, when (status) {
                        TaskStatus.DEFAULT -> JDEColor.SECONDARY.color
                        TaskStatus.SUCCESS -> JDEColor.SUCCESS.color
                        TaskStatus.DANGER -> JDEColor.PRIMARY.color
                        TaskStatus.WARNING -> JDEColor.WARNING.color
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
                    text = "Движение по маршруту\n${it.task.route.name}",
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
                    mainText = "${if (it.task.route.truck != null) it.task.route.truck.gost else "-"}",
                    subText = "Номер ТС"
                )
                Spacer(modifier = Modifier.width(10.dp))
                InformationPlaceholderSmall(
                    Modifier.weight(1f),
                    mainText = "${if (it.task.route.trailer != null) it.task.route.trailer.gost else "-"}",
                    subText = "Номер прицепа"
                )
            }
            InformationPlaceholderSmall(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                mainText = if (it.task.route.temperatureProperty == MarshTemperaturePropertyQL.HOT) "Горячая" else "Холодная",
                subText = "Тип перевозки"
            ) {
                when (it.task.route.temperatureProperty) {
                    MarshTemperaturePropertyQL.HOT -> Image(
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
                        mainText = it.task.text,
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

    return Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = JDEColor.PRIMARY.color)
    }

}


