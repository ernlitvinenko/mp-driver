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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mpdriver.api.TaskApi
import com.example.mpdriver.api.TaskResponse
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.feed.ActiveTask
import com.example.mpdriver.components.feed.FeedTaskDataCard
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.byUnicodePattern


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Feed(modifier: Modifier = Modifier, hostController: NavHostController) {
//    Fetch active task

    var isLoading by remember {
        mutableStateOf(true)
    }

    val context = LocalContext.current
    var activeTask by remember {
        mutableStateOf<TaskResponse?>(null)
    }
    var plannedTasks by remember {
        mutableStateOf<List<TaskResponse>>(emptyList())
    }
    var completedTasks by remember {
        mutableStateOf<List<TaskResponse>>(emptyList())
    }

    val taskApi = TaskApi(context)

    taskApi.getActiveTask { task ->
        isLoading = false
        task.id?.let {
            activeTask = task
        }
    }

    taskApi.getPlannedTasks { tasks ->
        plannedTasks = tasks
    }
    val dateFormat = LocalDateTime.Format {
        byUnicodePattern("dd.MM.yyyy")
    }

    val dataList = listOf(
        mapOf(
            "title" to "Запланированные задачи",
            "count" to plannedTasks.count(),
            "date" to when (plannedTasks.count()) {
                0 -> "-"
                else -> dateFormat.format(LocalDateTime.parse(plannedTasks[0].startPln!!))
            },
            "buttonLabel" to "Смотреть запланированные задачи",
            "dateDescription" to "Ближайшая",
        ),
        mapOf(
            "title" to "Завершенные задачи",
            "count" to completedTasks.count(),
            "date" to when (completedTasks.count()) {
                0 -> "-"
                else -> dateFormat.format(LocalDateTime.parse(completedTasks[0].startPln!!))
            },
            "buttonLabel" to "Смотреть завершенные задачи",
            "dateDescription" to "Последняя",
        ),
    )

    if(isLoading) {
        Column (Modifier.fillMaxWidth().padding(vertical = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFFE5332A))
        }
        return
    }

    Layout(dataList = dataList, header = {
        Column {
            ActiveTask(activeTask = activeTask, hostController = hostController)
            Spacer(modifier = Modifier.height(20.dp))
        }

    }) {
        FeedTaskDataCard(
            title = it["title"].toString(),
            count = it["count"].toString().toInt(),
            dateDescription = it["dateDescription"].toString(),
            buttonLabel = it["buttonLabel"].toString(),
            hostController = hostController, link = "feed/planned-tasks",
            date = it["date"].toString()
        )
    }
}




