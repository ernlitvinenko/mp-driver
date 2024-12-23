package com.example.mpdriver.screens

import android.util.Log
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
import com.example.mpdriver.viewmodels.MainViewModel

@Composable
fun ListEventsScreen(model: MainViewModel = viewModel(), navigateTo: (Route) -> Unit) {

    val events = model.events.observeAsState()

    Log.d("ListEventsScreenEvents", "ListEventsScreen: ${events}")

    Layout(dataList = events.value ?: emptyList(), header = {
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