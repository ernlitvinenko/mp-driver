package com.example.mpdriver.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.EventComponent
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.PersonalEvent
import com.example.mpdriver.variables.Route
import com.example.mpdriver.variables.Routes
import com.example.mpdriver.variables.datetimeFormatFrom
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import kotlin.math.abs

@Composable
fun ListEventsScreen(model: MainViewModel = viewModel(), navigateTo: (Route) -> Unit) {

    val events = model.events.observeAsState()

    val nowTime = Clock.System.now()


    val filteredEvents = events.value?.filter {
        val dt = it.eventData[0]["date"]

        dt?.let {
            val days = nowTime.until(
                LocalDateTime.parse("$dt 00:00:00", datetimeFormatFrom)
                    .toInstant(offset = UtcOffset(3)),
                timeZone = UtcOffset(3).asTimeZone(), unit = DateTimeUnit.DAY
            )
            return@filter abs(days) < 7
        }
        false

    }?.sortedBy {
        val dt = it.eventData[0]["date"]
        dt?.let {
            -LocalDateTime.parse("$dt 00:00:00", datetimeFormatFrom)
                .toInstant(offset = UtcOffset(hours = 3)).epochSeconds
        }

    }


//    eventsSortedAndFiltered = events.value?.filter { LocalDateTime.parse(it.eventDatetime, datetimeFormatFrom).}

    Layout(dataList = filteredEvents ?: emptyList(), header = {
        ActiveButton(modifier = Modifier.fillMaxWidth(), onClick = {
            navigateTo(Routes.Home.Events.Add)
        }, text = "Добавить событие")
    }) { event ->
        val eventData = event.eventData.first().toMutableMap()
        val eventType = PersonalEvent.listOfEvents.find { it.eventName == eventData["type"] }

        eventType?.let {
            EventComponent(eventData = eventData, readonly = true, eventType = it)
        }
    }
}