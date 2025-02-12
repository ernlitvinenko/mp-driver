package com.example.mpdriver.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpdriver.components.HeaderTabs
import com.example.mpdriver.components.HeaderTabsData
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.TaskComponent
import com.example.mpdriver.data.models.TaskStatus
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


enum class ActiveTab {
    PLANNED,
    COMPLETED
}

@Composable
fun TasksList(modifier: Modifier = Modifier,
              mainViewModel: MainViewModel = viewModel(),
              activeTabDefault: ActiveTab = ActiveTab.PLANNED) {
    var isLoading by remember {
        mutableStateOf(true)
    }

    var activeTab by remember {
        mutableStateOf(activeTabDefault)
    }


    LaunchedEffect(activeTab) {
        isLoading = false
    }

    val plannedTasks = mainViewModel.plannedTasksLiveData.observeAsState(emptyList())
    val completedTasks = mainViewModel.completedTaskLiveData.observeAsState(emptyList())
    val activeTask = mainViewModel.activeTaskLiveData.observeAsState()
    val coroutineScope = rememberCoroutineScope()


    if (isLoading) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 60.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = JDEColor.PRIMARY.color)
        }
        return
    }
    Layout(dataList = when (activeTab) {
        ActiveTab.PLANNED -> plannedTasks.value
        ActiveTab.COMPLETED -> completedTasks.value
    }, header = {
        HeaderTabs(
            tabsData = listOf(
                HeaderTabsData(ActiveTab.PLANNED.ordinal, "Запланированные"),
                HeaderTabsData(ActiveTab.COMPLETED.ordinal, "Завершенные")
            ), activeTab = activeTab.ordinal
        ) {
            when (it) {
                0 -> activeTab = ActiveTab.PLANNED
                1 -> activeTab = ActiveTab.COMPLETED
            }
        }
    }) {
        TaskComponent(Modifier.padding(bottom = 10.dp), taskData = it) {
            when (activeTab) {
                ActiveTab.PLANNED -> Button(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color.Black,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            mainViewModel.changeTask(
                                it,
                                TaskStatus.IN_PROGRESS,
                                datetime = Clock.System.now().toLocalDateTime(
                                    TimeZone.currentSystemDefault()
                                )
                            )
                            mainViewModel.fetchTaskData()
                        }
                    },
                    enabled = activeTask.value == null
                ) {
                    Text(text = "Приступить к выполнению")
                }

                else -> {}
            }
        }
    }
}


