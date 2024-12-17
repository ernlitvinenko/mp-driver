package com.example.mpdriver.components.subtask.sheet.steps

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.R
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.DatePicker
import com.example.mpdriver.components.DateTimePickerState
import com.example.mpdriver.components.InformationPlaceholderSmall
import com.example.mpdriver.components.IteractionButton
import com.example.mpdriver.components.TimePicker
import com.example.mpdriver.components.rememberDateTimePickerState
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.variables.dateFormat
import com.example.mpdriver.variables.datetimeFormatFrom
import com.example.mpdriver.variables.timeFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.LocalTime

data class SuccessStepApiCallData(
    val dateTime: LocalDateTime,
    val subtaskId: Long
)

@Composable
fun SuccessStep(
    subtask: AppTask,
    apiCall: (SuccessStepApiCallData) -> Unit = {}
) {

    val now = Clock.System.now()


    var dtPickerState by rememberDateTimePickerState()


    var date by remember {
        mutableStateOf(
            now.toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
                .format(dateFormat.toKotlin())
        )
    }

    var time by remember {
        mutableStateOf(
            now.toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
                .format(timeFormat.toKotlin())
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
                Box(modifier = Modifier.weight(3f)) {
                    IteractionButton(onClick = { dtPickerState = DateTimePickerState.DATE }) {
                        InformationPlaceholderSmall(
                            Modifier.fillMaxWidth(),
                            mainText = date,
                            subText = "Дата"
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    IteractionButton(onClick = { dtPickerState = DateTimePickerState.TIME }) {
                        InformationPlaceholderSmall(
                            Modifier.fillMaxWidth(),
                            mainText = time,
                            subText = "Время"
                        )
                    }
                }

            }

        }

        when (dtPickerState) {
            DateTimePickerState.DATE -> {
                DatePicker(
                    modifier = Modifier.fillMaxWidth(),
                    startDate = LocalDate.parse(date, dateFormat.toJava())
                ) {
                    date = it.format(dateFormat.toJava())
                }
            }

            DateTimePickerState.TIME -> {
                TimePicker(
                    modifier = Modifier.fillMaxWidth(),
                    startTime = LocalTime.parse(time, timeFormat.toJava())
                ) {
                    time = it.format(timeFormat.toJava())
                }
            }

            else -> null
        }

        Spacer(modifier = Modifier.height(20.dp))
        ActiveButton(onClick = {
            apiCall(
                SuccessStepApiCallData(
                    dateTime = LocalDateTime.parse("${date} ${time}:00", datetimeFormatFrom),
                    subtaskId = subtask.id
                )
            )
        }, text = "Сохранить", modifier = Modifier.fillMaxWidth())
    }


}
