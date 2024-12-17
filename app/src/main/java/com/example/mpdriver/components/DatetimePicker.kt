package com.example.mpdriver.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.example.mpdriver.variables.JDEColor
import java.time.LocalDate
import java.time.LocalTime


enum class DateTimePickerState {
    TIME,
    DATE,
    HIDE
}

@Composable
fun rememberDateTimePickerState(): MutableState<DateTimePickerState> {
   return remember {
        mutableStateOf(DateTimePickerState.HIDE)
    }
}

@Composable
fun DatePicker(
    modifier: Modifier,
    startDate: LocalDate = LocalDate.now(),
    onSnappedData: (LocalDate) -> Unit
) {
    WheelDatePicker(
        modifier = Modifier.fillMaxWidth(),
        startDate = startDate,
        selectorProperties = WheelPickerDefaults.selectorProperties(
            shape = RoundedCornerShape(10.dp),
            color = JDEColor.BG_GRAY.color,
            border = BorderStroke(2.dp, Color.Gray)
        )
    ) {
        onSnappedData(it)
    }
}

@Composable
fun TimePicker(
    modifier: Modifier,
    startTime: LocalTime = LocalTime.now(),
    onSnappedData: (LocalTime) -> Unit
) {
    WheelTimePicker(
        modifier = Modifier.fillMaxWidth(),
        startTime = startTime,
        selectorProperties = WheelPickerDefaults.selectorProperties(
            shape = RoundedCornerShape(10.dp),
            color = JDEColor.BG_GRAY.color,
            border = BorderStroke(2.dp, Color.Gray)
        ),
        onSnappedTime = {
            onSnappedData(it)
        }
    )
}