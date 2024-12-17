package com.example.mpdriver.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mpdriver.R
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
import com.example.mpdriver.variables.datetimeFormatFrom
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import kotlin.math.abs

@Preview(showBackground = true)
@Composable
fun Subtask(
    modifier: Modifier = Modifier,
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
    footerButton: @Composable () -> Unit = {}
) {

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


    val dateFormat = LocalDateTime.Format {
        byUnicodePattern("d.MM.yyyy")
    }

    val timeFormat = LocalDateTime.Format {
        byUnicodePattern("HH:mm")
    }

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


    IteractionButton(onClick = {
        isActionVisible = true
    }) {
        Column(
            modifier
                .border(
                    2.dp, when (status) {
                        TaskColor.DEFAULT -> Color.Gray
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
                text = "Подзадача доожна быть выполнена: ",
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
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = Color.Transparent
                )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
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

    if (isActionVisible) {
        IsSubtaskCompletedAction(setStateAction = { isActionVisible = false }, subtaskData)
    }

}

enum class SheetState {
    HIDE,
    OPEN,
    FULL_SCREEN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IsSubtaskCompletedAction(
    setStateAction: () -> Unit = { },
    subtask: AppTask
) {
    var currentStep by remember {
        mutableStateOf(0)
    }
    var title by remember {
        mutableStateOf("Вам удалось выполнить подзадачу?")
    }

    val actionController = rememberNavController()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var sheetStateCurrent by remember {
        mutableStateOf(SheetState.OPEN)
    }



    ModalBottomSheet(
        onDismissRequest = {
            setStateAction()
        },

        containerColor = Color.White,
//        modifier = ,
        sheetState = sheetState
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .imePadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp), horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
            Column {
                NavHost(navController = actionController, startDestination = "initial") {
                    composable("initial") {
                        sheetStateCurrent = SheetState.OPEN
                        InitialStep(subtask = subtask, controller = actionController)
                    }
                    composable("success") {
                        title = "Когда вы выполнили подзадачу?"
                        sheetStateCurrent = SheetState.OPEN

                        SuccessStep(subtask = subtask, controller = actionController, cb = {
                            sheetStateCurrent = SheetState.HIDE
                        })
                    }

                    composable("failure") {
                        sheetStateCurrent = SheetState.OPEN
                        title = "Что помешало выполнить подзадачу?"
                        FailureStep(subtask = subtask, controller = actionController)
                    }
                }
            }


        }

    }

    LaunchedEffect(sheetStateCurrent) {
        when (sheetStateCurrent) {
            SheetState.OPEN -> sheetState.show()
            SheetState.HIDE -> {
                setStateAction()
                sheetState.hide()
            }

            SheetState.FULL_SCREEN -> sheetState.expand()
        }
    }
}


@Composable
fun InitialStep(subtask: AppTask, controller: NavHostController) {
    Column {
        JDEButton(type = ButtonType.SUCCESS, onClick = { controller.navigate("success") }) {
            Text(text = "Подзадача выполнена", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        JDEButton(type = ButtonType.WARNING, onClick = { controller.navigate("failure") }) {
            Text(text = "У меня возникла проблема", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }

}


@Composable
fun SuccessStep(
    subtask: AppTask,
    controller: NavHostController = rememberNavController(), cb: () -> Unit = {}
) {

    val now = Clock.System.now()
    val timeFormat = LocalDateTime.Format {
        hour()
        minute()
    }
    val dateFormat = LocalDateTime.Format {
        dayOfMonth()
        monthNumber()
        year()
    }


    var date by remember {
        mutableStateOf(
            now.toLocalDateTime(timeZone = TimeZone.currentSystemDefault()).format(dateFormat)
        )
    }

    var time by remember {
        mutableStateOf(
            now.toLocalDateTime(timeZone = TimeZone.currentSystemDefault()).format(timeFormat)
        )
    }



    Column {
        Column(
            Modifier
                .border(
                    2.dp, Color.Gray, RoundedCornerShape(10.dp)
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
                    text = "${subtask.text}",
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
            Spacer(modifier = Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth()) {
                TextField(
                    value = date, onValueChange = { date = it },
                    label = { Text(text = "Дата") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = JDEColor.BG_GRAY.color,
                        focusedContainerColor = JDEColor.BG_GRAY.color,
                        cursorColor = Color.Gray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Color.Gray,
                       focusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                TextField(value = time, onValueChange = { time = it })
            }

        }

        Spacer(modifier = Modifier.height(20.dp))
        ActiveButton(onClick = {

//            val task = db.taskDao().getTaskForSbt(sbtID = subtask.id.toLong())

            val dt = LocalDateTime.parse(date + "T" + time, LocalDateTime.Format {
                dayOfMonth()
                monthNumber()
                year()
                char('T')
                hour()
                minute()
            }).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

//            task?.let {
//                val sbts = db.taskDao().getSubtasksForTask(id = task!!.id)
//                var secondEvent: APP_EVENT

//                if (sbts.last().id == subtask.id.toLong()) {
//                    secondEvent = APP_EVENT(
//                        id = 0,
//                        recId = task.id,
//                        vidId = 8678,
//                        typeId = 0,
//                        dateTime = dt.toString(),
//                        data = """[{"8794":"8682"}]""",
//                        params = "",
//                        text = "Set status to Completed"
//                    )
//                } else {
//
//                    secondEvent = APP_EVENT(
//                        id = 0,
//                        recId = sbts.find { elem -> elem.id == subtask.id.toLong() + 1 }!!.id,
//                        vidId = 8678,
//                        typeId = 0,
//                        dateTime = dt.toString(),
//                        data = """[{"8794":"8681"}]""",
//                        params = "",
//                        text = "Set status to InProgress"
//                    )
//                }
//                db.eventDao().insert(
//                    mutableListOf(
//                        APP_EVENT(
//                            id = 0,
//                            recId = subtask.id.toLong(),
//                            vidId = 8678,
//                            typeId = 0,
//                            dateTime = dt.toString(),
//                            data = """[{"8794":"8682"}]""",
//                            params = "",
//                            text = "Set status to Completed"
//                        ),
//                        secondEvent
//                    )
//                )

//            }

//            cb()
        }, text = "Сохранить", modifier = Modifier.fillMaxWidth())
    }


}

fun transformText(it: String): String {
    var digits = it.filter { it.isDigit() }
    if (digits.length > 8) {
        digits = digits.slice(0..<8)
    }

    var data = when (digits.length) {
        in 0..2 -> "${digits}"
        in 3..4 -> "${digits.substring(0..<2)}.${digits.substring(2)}"
        in 5..8 -> "${digits.substring(0..<2)}.${digits.substring(2..<4)}.${digits.substring(4)}"
        else -> digits
    }
    println(data)
    return data

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailureStep(
    subtask: AppTask,
    controller: NavHostController = rememberNavController()
) {

    var failureDesk by remember {
        mutableStateOf("")
    }
    var date by remember {
        mutableStateOf("")
    }
    Column {
        Text(
            text = "Опишите причину, по которой вам не удалось выполнить подзадачу",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(40.dp))
        TextField(
            value = failureDesk,
            onValueChange = { failureDesk = it },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Введите текст") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                focusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Проблема возникла:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        TextField(
            value = date,
            onValueChange = { date = it },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Дата") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                focusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.calendar_default),
                    contentDescription = ""
                )
            }
        )
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = date,
            onValueChange = { date = it },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Дата") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                focusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.calendar_default),
                    contentDescription = ""
                )
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        ActiveButton(onClick = { /*TODO*/ }, text = "Отправить", modifier = Modifier.fillMaxWidth())
    }
}
