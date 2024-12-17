package com.example.mpdriver.components.subtask.sheet.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.R
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.DatePicker
import com.example.mpdriver.components.DateTimePickerState
import com.example.mpdriver.components.InformationPlaceholderSmall
import com.example.mpdriver.components.TimePicker
import com.example.mpdriver.components.rememberDateTimePickerState
import com.example.mpdriver.data.models.AppLocationResponse
import com.example.mpdriver.data.models.AppMarshResponse
import com.example.mpdriver.data.models.AppMstResponse
import com.example.mpdriver.data.models.AppTRSResponse
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.data.models.MarshTemperatureProperty
import com.example.mpdriver.data.models.TaskStatus
import com.example.mpdriver.data.models.TaskType
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.dateFormat
import com.example.mpdriver.variables.datetimeFormatFrom
import com.example.mpdriver.variables.timeFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime


data class FailureStepApiCall(
    val subtaskId: Long,
    val reason: String,
    val datetime: LocalDateTime
)

@Preview(showBackground = true)
@Composable
fun FailureStep(
    subtask: AppTask = AppTask(
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
    apiCall: (FailureStepApiCall) -> Unit = {}
) {
    var failureDesk by remember {
        mutableStateOf("")
    }

    val now = Clock.System.now()
    var date by remember {
        mutableStateOf(
            now.toLocalDateTime(TimeZone.currentSystemDefault()).format(
                dateFormat.toKotlin()
            )
        )
    }
    var time by remember {
        mutableStateOf(
            now.toLocalDateTime(TimeZone.currentSystemDefault()).format(
                timeFormat.toKotlin()
            )
        )
    }

    var dateTimePickerState by rememberDateTimePickerState()

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
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp),
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
        InformationPlaceholderSmall(Modifier.fillMaxWidth(), mainText = date, subText = "Дата") {
            Icon(
                painter = painterResource(id = R.drawable.calendar_default),
                contentDescription = ""
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        InformationPlaceholderSmall(Modifier.fillMaxWidth(), mainText = time, subText = "Время") {
            Icon(
                painter = painterResource(id = R.drawable.calendar_default),
                contentDescription = ""
            )
        }
        when (dateTimePickerState) {
            DateTimePickerState.DATE -> DatePicker(modifier = Modifier.fillMaxWidth()) {
                date = it.format(dateFormat.toJava())
            }

            DateTimePickerState.TIME -> TimePicker(modifier = Modifier.fillMaxWidth()) {
                time = it.format(timeFormat.toJava())
            }

            else -> null
        }
        Spacer(modifier = Modifier.height(20.dp))

        ActiveButton(onClick = {
            apiCall(
                FailureStepApiCall(
                    subtaskId = subtask.id,
                    reason = failureDesk,
                    datetime = LocalDateTime.parse("${date} ${time}:00", datetimeFormatFrom)
                )
            )
        }, text = "Отправить", modifier = Modifier.fillMaxWidth())
    }
}
