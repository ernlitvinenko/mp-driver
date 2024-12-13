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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
import com.example.mpdriver.GetSubtaskByIDQuery
import com.example.mpdriver.R
import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.type.StatusEnumQl
import com.example.mpdriver.variables.JDEColor
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
    subtaskID: Long = 0,
    footerButton: @Composable () -> Unit = {}
) {

    var isActionVisible by remember {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(true)
    }

    var nowTime by remember {
        mutableStateOf(Clock.System.now())
    }
    var subtaskResponse by remember {
        mutableStateOf<GetSubtaskByIDQuery.Subtask?>(null)
    }

    LaunchedEffect(Unit) {
//         subtaskResponse = apolloClient.query(GetSubtaskByIDQuery("1125904232173609", subtaskID.toString())).execute().data?.subtask
    }

    TimeTickReciever.registerHandler {
        nowTime = Clock.System.now()
    }

    var startPln: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
    var endPln: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)

    subtaskResponse?.let {
        startPln = LocalDateTime.parse(subtaskResponse?.startPln.toString())
        endPln = LocalDateTime.parse(subtaskResponse?.endPln.toString())
    }


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


    val status = when {
        subtaskResponse?.status == StatusEnumQl.COMPLETED && !isDelay -> TaskColor.SUCCESS
        subtaskResponse?.status == StatusEnumQl.COMPLETED && isDelay -> TaskColor.WARNING
        subtaskResponse?.status == StatusEnumQl.CANCELLED -> TaskColor.WARNING
        (subtaskResponse?.status == StatusEnumQl.IN_PROGRESS || subtaskResponse?.status == StatusEnumQl.NOT_DEFINED) && isDelay -> TaskColor.DANGER
        else -> TaskColor.DEFAULT
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
                    text = "${subtaskResponse?.text}",
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

    if (isActionVisible && subtaskResponse != null) {
        IsSubtaskCompletedAction(setStateAction = { isActionVisible = false }, subtaskResponse!!)
    }

}

enum class SheetState{
    HIDE,
    OPEN,
    FULL_SCREEN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IsSubtaskCompletedAction(
    setStateAction: () -> Unit = { },
    subtask: GetSubtaskByIDQuery.Subtask
) {
    var currentStep by remember {
        mutableStateOf(0)
    }
    var title by remember {
        mutableStateOf("Вам удалось выполнить подзадачу?")
    }

    val actionController = rememberNavController()
    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = false,
    )
    var sheetStateCurrent by remember {
        mutableStateOf(SheetState.OPEN)
    }



    ModalBottomSheet(
        onDismissRequest = {
            setStateAction()
        },

        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
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
                        InitialStep(subtask = subtask, controller = actionController)
                    }
                    composable("success") {
                        title = "Когда вы выполнили подзадачу?"
                        if (sheetStateCurrent == SheetState.OPEN) {
                            sheetStateCurrent = SheetState.FULL_SCREEN
                        }
                        SuccessStep(subtask = subtask, controller = actionController, cb = {
                            sheetStateCurrent = SheetState.HIDE
                        })
                    }
                    composable("failure") {
                        if (sheetStateCurrent == SheetState.OPEN) {
                            sheetStateCurrent = SheetState.FULL_SCREEN
                        }
                        title = "Что помешало выполнить подзадачу?"
                        FailureStep(subtask = subtask, controller = actionController)
                    }
                }
            }


        }

    }

    LaunchedEffect(sheetStateCurrent) {
        when(sheetStateCurrent) {
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
fun InitialStep(subtask: GetSubtaskByIDQuery.Subtask, controller: NavHostController) {
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
    subtask: GetSubtaskByIDQuery.Subtask,
    controller: NavHostController = rememberNavController(), cb: () -> Unit = {}
) {

    val now = Clock.System.now()
    val ctx = LocalContext.current
//    val db = (ctx.applicationContext as NotificationApplication).db
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
                TextInput(modifier = Modifier.weight(.66f), date, {
                    date = it.filter { it.isDigit() }
                }, "Дата")
                Spacer(modifier = Modifier.width(10.dp))
                TextInput(
                    modifier = Modifier.weight(.33f),
                    time,
                    { time = it.filter { it.isDigit() } },
                    "Время"
                )
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
    subtask: GetSubtaskByIDQuery.Subtask,
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

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    value: String = "24.12.2023",
    onValueChange: (String) -> Unit = {},
    placeholder: String = "Дата"
) {

    var isFocused by remember {
        mutableStateOf(true)
    }

    val kbController = LocalSoftwareKeyboardController.current


    BasicTextField(
        modifier = modifier
            .onFocusEvent {
                isFocused = it.isFocused
            },
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            kbController?.hide()
        }),
        decorationBox = {
            Box(
                Modifier
                    .height(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(JDEColor.TEXT_FIELD_BG_COLOR.color)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    modifier = Modifier.align(if (isFocused || value != "") Alignment.TopStart else Alignment.CenterStart),
                    text = placeholder,
                    fontWeight = FontWeight.Normal,
                    fontSize = if (isFocused || value != "") 13.sp else 18.sp,
                    color = Color.Gray
                )
                if (isFocused || value != "") {
                    Text(
                        modifier = Modifier.align(Alignment.BottomStart),
                        text = transformText(value),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        })
}