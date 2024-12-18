package com.example.mpdriver.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpdriver.components.HeaderTabs
import com.example.mpdriver.components.HeaderTabsData
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.subtask.Subtask
import com.example.mpdriver.components.subtask.sheet.steps.ApiCalls
import com.example.mpdriver.components.subtask.sheet.steps.FailureStepApiCallData
import com.example.mpdriver.components.subtask.sheet.steps.SuccessStepApiCallData
import com.example.mpdriver.data.models.AppEventResponse
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.data.models.TaskStatus
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


enum class SubtaskAndEventsTab {
    SUBTASKS,
    EVENTS
}

@Preview(showBackground = true)
@Composable
fun SubtaskScreen(taskId: Long = 0, mainViewModel: MainViewModel = viewModel()) {
    var isLoading by remember {
        mutableStateOf(true)
    }

    var floatingActionShowOffset by remember {
        mutableStateOf(0f)
    }

    val coroutineScope = rememberCoroutineScope()



    var activeTab by remember {
        mutableStateOf<SubtaskAndEventsTab>(SubtaskAndEventsTab.SUBTASKS)
    }


    val tabsData =
        listOf(
            HeaderTabsData(0, "Подзадачи"),
            HeaderTabsData(1, "События")
        )

    LaunchedEffect(Unit) {
        isLoading = false
    }


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


    when (activeTab) {
        SubtaskAndEventsTab.SUBTASKS -> {
            val dataList = mainViewModel.tasks.value?.find { task ->
                task.id == taskId
            }?.subtasks
            val listState = rememberLazyListState()

            Layout(state = listState, dataList = dataList ?: emptyList(), header = {
                HeaderTabs(modifier = Modifier.onGloballyPositioned { lc ->
                    val cords = lc.boundsInParent()
                    floatingActionShowOffset = cords.bottom
                }, tabsData = tabsData, activeTab = activeTab.ordinal) {
                    activeTab = when (it) {
                        0 -> SubtaskAndEventsTab.SUBTASKS
                        else -> SubtaskAndEventsTab.EVENTS
                    }
                }
            }) {
                Subtask(subtaskData = it, apiCalls = object: ApiCalls {
                    override fun success(data: SuccessStepApiCallData) {
                        coroutineScope.launch {
                            mainViewModel.changeTask(data.subtaskId, TaskStatus.COMPLETED, data.dateTime)
                        }
                    }

                    override fun failure(data: FailureStepApiCallData) {
                        coroutineScope.launch {
                            mainViewModel.changeTask(data.subtaskId, TaskStatus.CANCELLED, data.datetime, data.reason)
                        }
                    }

                })
            }

        }
        SubtaskAndEventsTab.EVENTS -> {

            val dataList = mainViewModel.tasks.value?.find { task ->
                task.id == taskId
            }?.events

            val listState = rememberLazyListState()
            Layout(state = listState, dataList = dataList ?: emptyList(), header = {
                HeaderTabs(modifier = Modifier.onGloballyPositioned { lc ->
                    val cords = lc.boundsInParent()
                    floatingActionShowOffset = cords.bottom
                }, tabsData = tabsData, activeTab = activeTab.ordinal) {
                    activeTab = when (it) {
                        0 -> SubtaskAndEventsTab.SUBTASKS
                        else -> SubtaskAndEventsTab.EVENTS
                    }
                }
            }) {
                Text(text = it.text ?: "-")
            }
        }
    }



//    AnimatedVisibility(
//        visible = offset + 200 > floatingActionShowOffset + 100,
//        enter = slideInVertically() + fadeIn(),
//        exit = slideOutVertically() + fadeOut()
//    ) {
//        ExtendedFloatingActionButton(
//            onClick = { /*TODO*/ },
//            shape = RoundedCornerShape(10.dp),
//            containerColor = Color.White,
//            contentColor = Color.Black
//        ) {
//            Row(Modifier.fillMaxWidth()) {
//                Text(text = "Скрыть завершенные задачи")
//            }
//        }
//    }
}