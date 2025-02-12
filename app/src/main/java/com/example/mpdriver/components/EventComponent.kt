package com.example.mpdriver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.dateFormat
import com.example.mpdriver.variables.datetimeFormatFrom
import com.example.mpdriver.variables.timeFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate

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

    companion object {
        val listOfEvents = listOf(REPAIR, LUNCH, DREAMS, ACCIDENT, FUEL)
    }
}

@Preview(showBackground = true)
@Composable
fun EventComponent(
    modifier: Modifier = Modifier,
    eventType: PersonalEvent = PersonalEvent.REPAIR,
    eventData: MutableMap<String, String> = remember {
        mutableStateMapOf()
    },
    readonly: Boolean = false,
    setError: (Boolean) -> Unit = {}
) {

    var activeField by remember {
        mutableStateOf<EventField?>(null)
    }

    var errorText by remember {
        mutableStateOf("")
    }


    val now = Clock.System.now()
    CardComponent(modifier) {
        Text(text = errorText, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = JDEColor.PRIMARY.color)
        Text(text = eventType.eventName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.padding(top = 20.dp))
        eventType.fields.forEach {

            if (readonly) {
                when (it.type) {
                    EventFieldTypes.TIME_RANGE -> {
                        InformationPlaceholderSmall(
                            Modifier.fillMaxWidth(),
                            subText = it.label,
                            mainText = "${eventData[it.name + "__from"] ?: ""} - ${eventData[it.name + "__till"] ?: ""}"
                        )
                    }

                    else -> {
                        InformationPlaceholderSmall(
                            Modifier.fillMaxWidth(),
                            subText = it.label,
                            mainText = eventData[it.name]
                                ?: ""
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                return@forEach
            }


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
                        DatePicker(
                            modifier = Modifier.fillMaxWidth(),
                            startDate = if (eventData[it.name] != null) LocalDate.parse(
                                eventData[it.name],
                                dateFormat.toJava()
                            ) else LocalDate.now()

                        ) { date ->
                            val formattedDate = date.format(dateFormat.toJava())
                            eventData[it.name] = formattedDate
                            if (date.until(
                                now.toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
                                    .toJavaLocalDateTime().toLocalDate()
                            ).isNegative) {
                                errorText = "Дата события должна быть раньше чем текущая"
                                setError(true)
                                eventData[it.name] = formattedDate

                                return@DatePicker
                            }
                            setError(false)
                            errorText = ""


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
                                    TimePicker(modifier = Modifier) { timeFrom ->
                                        eventData[it.name + "__from"] =
                                            timeFrom.format(timeFormat.toJava())
                                    }
                                }
                                Text(text = "-")
                                Column(Modifier.weight(1f)) {
                                    TimePicker(modifier = Modifier) { timeTill ->
                                        eventData[it.name + "__till"] =
                                            timeTill.format(timeFormat.toJava())
                                    }
                                }
                            }

                        }
                    }


                }

                EventFieldTypes.SELECT -> {
                    it as EventFieldWithSelect
                    Box {
                        IteractionButton(onClick = { activeField = it }) {
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
                            DropdownMenu(
                                expanded = true,
                                onDismissRequest = { activeField = null }) {
                                it.variants.forEach { item ->
                                    DropdownMenuItem(
                                        colors = MenuDefaults.itemColors(textColor = Color.Gray),
                                        text = { Text(text = item.label) },
                                        onClick = {
                                            eventData[it.name] = item.value
                                            activeField = null
                                        })
                                }
                            }
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
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
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