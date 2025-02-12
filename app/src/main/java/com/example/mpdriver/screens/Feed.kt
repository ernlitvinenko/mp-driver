package com.example.mpdriver.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.feed.ActiveTask
import com.example.mpdriver.components.feed.FeedTaskDataCard
import com.example.mpdriver.components.subtask.sheet.steps.ApiCalls
import com.example.mpdriver.components.subtask.sheet.steps.FailureStepApiCallData
import com.example.mpdriver.components.subtask.sheet.steps.SuccessStepApiCallData
import com.example.mpdriver.data.models.TaskStatus
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.Route
import com.example.mpdriver.variables.Routes
import com.example.mpdriver.variables.datetimeFormatFrom
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.byUnicodePattern

private data class FeedDataListProps(
    val title: String,
    val count: Int,
    val date: String,
    val buttonLabel: String,
    val dateDescription: String,
    val handler: () -> Unit
)


@Composable
fun Feed(
    modifier: Modifier = Modifier,
    model: MainViewModel,
    navigateTo: (Route) -> Unit = {},
    navigateToTask: (Long) -> Unit = {},

) {

    //    Fetch active task
    var isLoading by remember {
        mutableStateOf(true)
    }

    val coroutineScope = rememberCoroutineScope()

    val dateFormat = LocalDateTime.Format {
        byUnicodePattern("dd.MM.yyyy")
    }

    val  plannedTasks = model.plannedTasksLiveData.observeAsState(emptyList())
    val completedTasks = model.completedTaskLiveData.observeAsState(emptyList())
    val activeTask = model.activeTaskLiveData.observeAsState()


    val dataList = listOf(
        FeedDataListProps(
            title = "Запланированные задачи",
            count = plannedTasks.value.count() ?: 0,
            date = when (plannedTasks.value.count()) {
                0 -> "-"
                else -> dateFormat.format(
                    LocalDateTime.parse(
                        plannedTasks.value[0].startPln,
                        datetimeFormatFrom
                    )
                )
            },
            buttonLabel = "Смотреть запланированные задачи",
            dateDescription = "Ближайшая",
            handler = {navigateTo(Routes.Home.Tasks.Planned)}
        ),
        FeedDataListProps(
            title = "Завершенные задачи",
            count = completedTasks.value.count() ?: 0,
            when (completedTasks.value.count()) {
                0 -> "-"
                else -> dateFormat.format(
                    LocalDateTime.parse(
                        completedTasks.value[0].startPln,
                        datetimeFormatFrom
                    )
                )
            },
            buttonLabel = "Смотреть завершенные задачи",
            dateDescription = "Последняя",
            handler = {navigateTo(Routes.Home.Tasks.Closed)}
        )
    )

    LaunchedEffect(Unit) {
        model.fetchTaskData()
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


    Layout(dataList = dataList, header = {
        Column {
            ActiveTask(activeTask = activeTask.value, navigateToTask = {navigateToTask(it)}, model = model, apiCalls = object : ApiCalls {
                override fun success(data: SuccessStepApiCallData) {
                    coroutineScope.launch {
                        model.changeTask(data.subtaskId, TaskStatus.COMPLETED, datetime = data.dateTime)
                        model.fetchTaskData()
                        withContext(Dispatchers.Main) {
                            navigateTo(Routes.Home.Feed)
                        }
                    }
                }

                override fun failure(data: FailureStepApiCallData) {
                    coroutineScope.launch {
                        model.changeTask(data.subtaskId, TaskStatus.CANCELLED, data.datetime, errorText = data.reason)
                        model.fetchTaskData()
                       withContext(Dispatchers.Main) {
                            navigateTo(Routes.Home.Feed)
                       }
                    }
                }

            })
            Spacer(modifier = Modifier.height(20.dp))
        }

    }) {
        FeedTaskDataCard(
            title = it.title,
            count = it.count,
            dateDescription = it.dateDescription,
            buttonLabel = it.buttonLabel,
            date = it.date,
        ) {
            it.handler()
        }
    }
}




