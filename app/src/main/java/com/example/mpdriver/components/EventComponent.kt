package com.example.mpdriver.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.commandiron.wheel_picker_compose.core.WheelTextPicker
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.dateFormat
import com.example.mpdriver.variables.timeFormat
import java.time.LocalDate
import java.time.LocalTime

enum class EventFieldTypes {
    DATE,
    TIME,
    TIME_RANGE,
    TEXT,
    IMAGE,
    SELECT
}


data class SelectionVariant(val label: String, val value: String)

sealed class EventField(
    val name: String,
    val label: String,
    val type: EventFieldTypes,
    val minHeight: Int? = null
) {
    data object Date : EventField("date", "Дата", EventFieldTypes.DATE)
    data object Time : EventField("time", "Время", EventFieldTypes.TIME)
    data object TimeRange :
        EventField("time_range", "Временной диапазон", EventFieldTypes.TIME_RANGE)

    data object Description :
        EventField("description", "Описание", EventFieldTypes.TEXT, minHeight = 150)

    data object RepairType : EventField("repair_type", "Что ремонтируется", EventFieldTypes.TEXT)
    data object RepairComplexity : EventFieldWithSelect(
        "repair_complexity",
        "Сложность ремонта", listOf(
            SelectionVariant("Легкий", "1"),
            SelectionVariant("Средний", "2"),
            SelectionVariant("Сложный", "3")
        )
    )

    data object IMAGE : EventField("image", "Добавить фото", EventFieldTypes.IMAGE)
}

open class EventFieldWithSelect(
    name: String,
    label: String,
    val variants: List<SelectionVariant>
) : EventField(name, label, EventFieldTypes.SELECT)

sealed class PersonalEvent(val eventName: String, val fields: List<EventField>) {
    data object REPAIR : PersonalEvent(
        "Ремонт",
        listOf(
            EventField.Date,
            EventField.TimeRange,
            EventField.RepairType,
            EventField.Description,
            EventField.RepairComplexity
        )
    )

    data object LUNCH : PersonalEvent(
        "Обед",
        listOf(
            EventField.Date,
            EventField.TimeRange
        )
    )

    data object DREAMS : PersonalEvent(
        "Сон",
        listOf(
            EventField.Date,
            EventField.TimeRange
        )
    )

    data object ACCIDENT : PersonalEvent(
        "ДТП",
        listOf(
            EventField.Date,
            EventField.Time,
            EventField.Description,

//            EventField.IMAGE
        )
    )

    data object FUEL : PersonalEvent(
        "Заправка",
        listOf(
            EventField.Date,
            EventField.Time,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun EventComponent(modifier: Modifier = Modifier, eventType: PersonalEvent = PersonalEvent.REPAIR) {

    val eventData = remember {
        mutableStateMapOf<String, String>()
    }

    var activeField by remember {
        mutableStateOf<EventField?>(null)
    }

    CardComponent(modifier) {
        Text(text = eventType.eventName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.padding(top = 20.dp))
        eventType.fields.forEach {
            when (it.type) {
                EventFieldTypes.DATE -> {
                    IteractionButton(onClick = { activeField = it }) {
                        InformationPlaceholderSmall(
                            Modifier.fillMaxWidth(),
                            subText = it.label,
                            mainText = eventData[it.name]
                                ?: ""
                        )
                    }

                    if (activeField == it) {
                        DatePicker(modifier = Modifier.fillMaxWidth(), startDate = if(eventData[it.name] != null) LocalDate.parse(eventData[it.name], dateFormat.toJava()) else LocalDate.now()) { date ->
                            eventData[it.name] = date.format(dateFormat.toJava())
                        }
                    }

                }

                EventFieldTypes.TIME -> {
                    IteractionButton(onClick = { activeField = it }) {
                        InformationPlaceholderSmall(
                            Modifier.fillMaxWidth(),
                            subText = it.label,
                            mainText = eventData[it.name] ?: ""
                        )
                    }
                    if (activeField == it) {
                        TimePicker(modifier = Modifier.fillMaxWidth()) { time ->
                            eventData[it.name] = time.format(timeFormat.toJava())
                        }
                    }

                }

                EventFieldTypes.TIME_RANGE -> {
                    IteractionButton(onClick = { activeField = it }) {
                        InformationPlaceholderSmall(
                            Modifier.fillMaxWidth(),
                            subText = it.label,
                            mainText = "${eventData[it.name + "__from"] ?: ""} - ${eventData[it.name + "__till"] ?: ""}"
                        )
                    }
                    if (activeField == it) {
                        Column(Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween

                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    text = "Начало",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = JDEColor.SECONDARY.color
                                )
                                Text(
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    text = "Окончание",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = JDEColor.SECONDARY.color
                                )
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    TimePicker(modifier = Modifier) {timeFrom ->
                                        eventData[it.name + "__from"] = timeFrom.format(timeFormat.toJava())
                                    }
                                }
                                Text(text = "-")
                                Column(Modifier.weight(1f)) {
                                    TimePicker(modifier = Modifier) { timeTill ->
                                        eventData[it.name + "__till"] = timeTill.format(timeFormat.toJava())
                                    }
                                }
                            }

                        }
                    }


                }

                EventFieldTypes.SELECT -> {
                    it as EventFieldWithSelect
                    IteractionButton(onClick = { activeField = it}) {
                        InformationPlaceholderSmall(
                            Modifier.fillMaxWidth(),
                            subText = it.label,
                            mainText = it.variants.firstOrNull { variant ->
                                variant.value == eventData[it.name]
                            }?.label ?: ""
                        )
                    }

                    if (activeField == it) {
                        Spacer(modifier = Modifier.height(10.dp))
                        WheelTextPicker(
                            Modifier.fillMaxWidth(),
                            rowCount = it.variants.count(),
                            size = DpSize(256.dp, 128.dp),
                            startIndex = if (eventData[it.name] != null) it.variants.indexOfFirst { variant ->
                                variant.value == eventData[it.name]
                            } else 0,
                            texts = it.variants.map { variant ->
                                variant.label
                            },
                            selectorProperties = WheelPickerDefaults.selectorProperties(
                                shape = RoundedCornerShape(
                                    10.dp
                                ),
                                border = BorderStroke(1.dp, JDEColor.SECONDARY.color),
                                color = JDEColor.BG_GRAY.color
                            )
                        ) { index ->

                            if (index == 0) {
                                eventData[it.name] = it.variants[index].value
                            }
                            else {
                                eventData[it.name] = it.variants[index - 1].value
                            }
                            val variant = it.variants.find { variant ->
                                variant.value == eventData[it.name]
                            }
                            it.variants.indexOf(variant)
                        }
                    }
                }

                EventFieldTypes.TEXT -> {
                    TextField(
                        value = eventData[it.name] ?: "",
                        onValueChange = { newVal ->
                            eventData[it.name] = newVal
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = it.minHeight?.dp ?: 50.dp)
                            .onFocusEvent { state ->
                                if (state.isFocused) {
                                    activeField = it
                                }
                            },
                        placeholder = { Text(text = it.label) },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                            focusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Gray,
                            unfocusedTextColor = Color.Gray,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        )
                    )
                }


                else -> {}
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }

    }
}