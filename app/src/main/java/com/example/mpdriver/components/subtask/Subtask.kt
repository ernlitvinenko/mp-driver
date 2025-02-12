package com.example.mpdriver.components.subtask

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.mpdriver.R
import com.example.mpdriver.components.CardComponent
import com.example.mpdriver.components.InformationPlaceholderSmall
import com.example.mpdriver.components.IteractionButton
import com.example.mpdriver.components.TaskColor
import com.example.mpdriver.components.subtask.sheet.SubtaskSheet
import com.example.mpdriver.components.subtask.sheet.steps.ApiCalls
import com.example.mpdriver.data.models.AppLocationResponse
import com.example.mpdriver.data.models.AppMarshResponse
import com.example.mpdriver.data.models.AppMstResponse
import com.example.mpdriver.data.models.AppTRSResponse
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.data.models.MarshTemperatureProperty
import com.example.mpdriver.data.models.TaskStatus
import com.example.mpdriver.data.models.TaskType
import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.dateFormat
import com.example.mpdriver.variables.datetimeFormatFrom
import com.example.mpdriver.variables.timeFormat
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toInstant
import kotlinx.datetime.until
import kotlin.math.abs


@Composable
fun Subtask(
    modifier: Modifier = Modifier,
    apiCalls: ApiCalls,
    children: @Composable () -> Unit = {},
    subtaskData: AppTask = AppTask(
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
        station = AppMstResponse(
            id = 2252083418031070,
            name = "Москва-Южный",
            location = AppLocationResponse(lat = 55.48954f, lon = 37.75279f)
        )
    ),
    model: MainViewModel,
    footerButton: @Composable () -> Unit = {}
) {


    val context = LocalContext.current
    var isActionVisible by remember {
        mutableStateOf(false)
    }


    var nowTime by remember {
        mutableStateOf(Clock.System.now())
    }

    TimeTickReciever.registerHandler {
        nowTime = Clock.System.now()
    }


    val endPln = LocalDateTime.parse(subtaskData.endPln, datetimeFormatFrom)

    val leastBase = nowTime.until(
        endPln.toInstant(offset = UtcOffset(3)),
        timeZone = UtcOffset(3).asTimeZone(), unit = DateTimeUnit.MINUTE
    )

    val isDelay = leastBase != abs(leastBase)
    val leastDays = abs(leastBase) / 60 / 24
    val leastHours = abs(leastBase) / 60 - leastDays * 24
    val leastMinutes = abs(leastBase) - leastDays * 60 * 24 - leastHours * 60

//
    val status: TaskColor = when (subtaskData.status) {
        TaskStatus.COMPLETED -> if (!isDelay) TaskColor.SUCCESS else TaskColor.WARNING
        TaskStatus.CANCELLED -> TaskColor.WARNING
        TaskStatus.IN_PROGRESS -> if (isDelay) TaskColor.DANGER else TaskColor.DEFAULT
        TaskStatus.NOT_DEFINED -> if (isDelay) TaskColor.DANGER else TaskColor.DEFAULT
    }

    val statusColor = when (status) {
        TaskColor.DEFAULT -> JDEColor.SECONDARY
        TaskColor.SUCCESS -> JDEColor.SUCCESS
        TaskColor.DANGER -> JDEColor.PRIMARY
        TaskColor.WARNING -> JDEColor.WARNING
    }


    IteractionButton(onClick = {
        isActionVisible = true
    }) {
        CardComponent(modifier, statusColor) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = subtaskData.text,
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

            Text(
                modifier = Modifier.padding(bottom = 5.dp),
                text = "Подзадача должна быть выполнена: ",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Row(Modifier.padding(bottom = 20.dp)) {
                InformationPlaceholderSmall(
                    Modifier.weight(2f),
                    mainText = endPln.format(dateFormat.toKotlin()),
                    subText = "Дата"
                )
                Spacer(modifier = Modifier.width(10.dp))
                InformationPlaceholderSmall(
                    Modifier.weight(1f),
                    mainText = endPln.format(timeFormat.toKotlin()),
                    subText = "Время"
                )
            }
            Text(
                modifier = Modifier.padding(bottom = 5.dp),
                text = if (isDelay) "Задержка" else "Осталось времени:",
                fontWeight = FontWeight.Bold,
                fontSize = if (isDelay) 20.sp else 14.sp,
                color = if (isDelay) JDEColor.PRIMARY.color else Color.Black
            )
            Row(Modifier.padding(bottom = 20.dp)) {
                InformationPlaceholderSmall(
                    Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            color = if (isDelay) JDEColor.PRIMARY.color else Color.Gray,
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
                            color = if (isDelay) JDEColor.PRIMARY.color else Color.Gray,
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
                            color = if (isDelay) JDEColor.PRIMARY.color else Color.Gray,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    mainText = leastMinutes.toString(),
                    subText = "Минуты"
                )
            }

            TextButton(
                onClick = {
                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse("yandexnavi://build_route_on_map?lat_to=${subtaskData.station?.location?.lat}&lon_to=${subtaskData.station?.location?.lon}"))
                    intent.setPackage("ru.yandex.yandexnavi")
                    val pm: PackageManager = context.packageManager
                    val infos = pm.queryIntentActivities(intent, 0)

                    if (infos.size == 0) {
                        // Если нет - будем открывать страничку Навигатора в Google Play
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ru.yandex.yandexnavi"));
                        context.startActivity(intent)
                    } else {
                        context.startActivity(intent);
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = Color.Transparent
                )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .border(1.dp, statusColor.color, RoundedCornerShape(10.dp))
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.yanavi),
                        contentDescription = "yanavi"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Открыть в Яндекс Навигатор")
                }

            }

            footerButton()
        }
    }

    if (subtaskData.status == TaskStatus.IN_PROGRESS) {
        if (isActionVisible) {
            SubtaskSheet(setStateAction = { isActionVisible = false }, subtaskData, apiCalls = apiCalls, model = model)
        }
    }



}
