package com.example.mpdriver.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

enum class EventFieldTypes {
    DATE,
    TIME,
    TIME_RANGE,
    TEXT,
    IMAGE,
    SELECT
}


data class SelectionVariant(val label: String, val value: String)

// TODO ADD NAME for field
sealed class EventField(val label: String, val type: EventFieldTypes, val minHeight: Int? = null) {
    data object Date : EventField("Дата", EventFieldTypes.DATE)
    data object Time : EventField("Время", EventFieldTypes.TIME)
    data object TimeRange : EventField("Временной диапазон", EventFieldTypes.TIME_RANGE)
    data object Description : EventField("Описание", EventFieldTypes.TEXT, minHeight = 150)
    data object RepairType : EventField("Что ремонтируется", EventFieldTypes.TEXT)
    data object RepairComplexity : EventFieldWithSelect(
        "Сложность ремонта", EventFieldTypes.SELECT, listOf(
            SelectionVariant("Легкий", "1"),
            SelectionVariant("Средний", "2"),
            SelectionVariant("Сложный", "3")
        )
    )

    data object IMAGE : EventField("Добавить фото", EventFieldTypes.IMAGE)
}

open class EventFieldWithSelect(
    label: String,
    type: EventFieldTypes,
    val variants: List<SelectionVariant>
) : EventField(label, type)

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
    CardComponent(modifier) {
        Text(text = eventType.eventName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.padding(top = 20.dp))
        eventType.fields.forEach {
            when (it.type) {
                EventFieldTypes.DATE -> {
                    InformationPlaceholderSmall(Modifier.fillMaxWidth(), subText = it.label, mainText = "")
                    DatePicker(modifier = Modifier.fillMaxWidth()) {

                    }
                }

                EventFieldTypes.TIME -> {
                    InformationPlaceholderSmall(Modifier.fillMaxWidth(), subText = it.label, mainText = "")
                    TimePicker(modifier = Modifier.fillMaxWidth()) {
                    }
                }

                EventFieldTypes.TIME_RANGE -> {
                    InformationPlaceholderSmall(Modifier.fillMaxWidth(), subText = it.label, mainText = "")
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
                                TimePicker(modifier = Modifier) {}
                            }
                            Text(text = "-")
                            Column(Modifier.weight(1f)) {
                                TimePicker(modifier = Modifier) {}
                            }
                        }

                    }
                }

                EventFieldTypes.SELECT -> {
                    InformationPlaceholderSmall(Modifier.fillMaxWidth(), subText = it.label, mainText = "")
                    it as EventFieldWithSelect
                    WheelTextPicker(
                        Modifier.fillMaxWidth(),
                        size = DpSize(256.dp, 120.dp),
                        rowCount = it.variants.count(),
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
                        index
                    }
                }

                EventFieldTypes.TEXT -> {
                    TextField(
                        value = "",
                        onValueChange = {  },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = it.minHeight?.dp ?: 50.dp)
                        ,
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