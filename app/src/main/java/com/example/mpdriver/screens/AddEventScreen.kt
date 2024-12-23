package com.example.mpdriver.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.EventComponent
import com.example.mpdriver.components.InformationPlaceholderSmall
import com.example.mpdriver.components.IteractionButton
import com.example.mpdriver.components.PersonalEvent
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.variables.Route
import com.example.mpdriver.variables.Routes
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun AddEventScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(),
    task: AppTask? = null,
    navigateTo: (Route) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedOption by remember {
        mutableStateOf<PersonalEvent?>(null)
    }

    val events: List<PersonalEvent> = PersonalEvent.listOfEvents

    val eventData = remember {
        mutableStateMapOf<String, String>()
    }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 20.dp)
    ) {
        item {
            Text(
                text = "Добавить событие",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Spacer(modifier = Modifier.height(15.dp))
        }
        item {
            Box {
                IteractionButton(onClick = { expanded = true }) {
                    InformationPlaceholderSmall(
                        Modifier.fillMaxWidth(),
                        mainText = selectedOption?.eventName ?: "",
                        subText = "Выбранное событие"
                    )
                }
                DropdownMenu(expanded = expanded, onDismissRequest = {
                    expanded = !expanded
                }) {
                    events.forEach { event ->
                        DropdownMenuItem(text = {
                            Text(text = event.eventName)
                        }, onClick = {
                            selectedOption = event
                            expanded = false
                        })
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(15.dp))
        }

        item {
            selectedOption?.let {
                EventComponent(eventType = it, eventData = eventData)
            }
        }

        item {
            selectedOption?.let {
                Spacer(modifier = Modifier.height(10.dp))
                ActiveButton(modifier = Modifier.fillMaxWidth(), text = "Сохранить", onClick = {
                    coroutineScope.launch {
                        eventData["type"] = it.eventName
                        mainViewModel.addEvent(
                            task,
                            eventData = eventData
                        )
                        mainViewModel.fetchTaskData()
                        withContext(Dispatchers.Main) {
                            navigateTo(Routes.Home.Feed)
                        }
                    }
                })
            }
        }
    }
}