package com.example.mpdriver.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.toInput
import com.example.mpdriver.GetSubtaskByIDQuery
import com.example.mpdriver.GetTaskByIdQuery
import com.example.mpdriver.R
import com.example.mpdriver.api.Subtasks
import com.example.mpdriver.api.apolloClient
import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.storage.CreateUpdateTaskData
import com.example.mpdriver.storage.Database
import com.example.mpdriver.type.StatusEnumQl
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
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

        val sbtDB = Database.subtasks.find { it.id == subtaskID.toString() }

        sbtDB?.let {
            subtaskResponse = GetSubtaskByIDQuery.Subtask(
                sbtDB.id,
                sbtDB.text,
                startPln = sbtDB.startPln,
                endPln = sbtDB.endPln,
                status = sbtDB.status
            )

        }
//         subtaskResponse = apolloClient.query(GetSubtaskByIDQuery("1125904232173609", subtaskID.toString())).execute()
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
        subtaskResponse?.status == StatusEnumQl.COMPLETED && !isDelay -> TaskStatus.SUCCESS
        subtaskResponse?.status == StatusEnumQl.COMPLETED && isDelay -> TaskStatus.WARNING
        subtaskResponse?.status == StatusEnumQl.CANCELLED -> TaskStatus.WARNING
        (subtaskResponse?.status == StatusEnumQl.IN_PROGRESS || subtaskResponse?.status == StatusEnumQl.NOT_DEFINED) && isDelay -> TaskStatus.DANGER
        else -> TaskStatus.DEFAULT
    }


    IteractionButton(onClick = {
        isActionVisible = true
    }) {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IsSubtaskCompletedAction(
    setStateAction: (Boolean) -> Unit = { },
    subtask: GetSubtaskByIDQuery.Subtask
) {
    var currentStep by remember {
        mutableStateOf(0)
    }
    var title by remember {
        mutableStateOf("Вам удалось выполнить подзадачу?")
    }

    val actionController = rememberNavController()
    var isFullScreen by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    ModalBottomSheet(
        onDismissRequest = {
            setStateAction(false)
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
                        SuccessStep(subtask = subtask, controller = actionController)
                        title = "Когда вы выполнили подзадачу?"
                        isFullScreen = true
                        LaunchedEffect(sheetState) {
                            sheetState.expand()
                        }
                    }
                    composable("failure") {
                        isFullScreen = true
                        LaunchedEffect(sheetState) {
                            sheetState.expand()
                        }

                        title = "Что помешало выполнить подзадачу?"
                        FailureStep(subtask = subtask, controller = actionController)
                    }
                }
            }


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
    controller: NavHostController = rememberNavController()
) {

    var date by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
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
            val task = Database.tasks.find {
                it.subtasks.find { sbt ->
                    sbt.id == subtask.id
                } != null
            }

            val dt = LocalDateTime.parse(date + "T" + time, LocalDateTime.Format {
                dayOfMonth()
                monthNumber()
                year()
                char('T')
                hour()
                minute()
            }).toInstant(TimeZone.UTC).toEpochMilliseconds()

            task?.let {
                Database.createUpdateTaskDataLocally(
                    CreateUpdateTaskData(
                        subtask.id.toLong(),
                        dt = dt,
                        status = "Completed"
                    )
                )

                if (subtask.id == task.subtasks.last().id) {
                    Database.createUpdateTaskDataLocally(
                        CreateUpdateTaskData(
                            task.id.toLong(),
                            dt = dt,
                            status = "Completed"
                        )
                    )
                    return@let
                }
                val curr_sbt_idx = task.subtasks.indexOf(task.subtasks.find { it.id == subtask.id })

                Database.createUpdateTaskDataLocally(
                    CreateUpdateTaskData(
                        task.subtasks[curr_sbt_idx + 1].id.toLong(),
                        dt,
                        status = "InProgress"
                    )
                )

            }


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
                unfocusedContainerColor = Color(0xFFF2F2F2),
                focusedContainerColor = Color(0xFFF2F2F2),
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
                unfocusedContainerColor = Color(0xFFF2F2F2),
                focusedContainerColor = Color(0xFFF2F2F2),
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
                unfocusedContainerColor = Color(0xFFF2F2F2),
                focusedContainerColor = Color(0xFFF2F2F2),
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
                    .background(Color(0xFFF2F2F2))
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