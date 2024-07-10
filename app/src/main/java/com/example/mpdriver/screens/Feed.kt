package com.example.mpdriver.screens

import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.apollographql.apollo3.api.ApolloResponse
import com.example.mpdriver.GetActiveTaskIdQuery
import com.example.mpdriver.GetTaskByIdQuery
import com.example.mpdriver.api.TaskApi
import com.example.mpdriver.api.TaskResponse
import com.example.mpdriver.api.apolloClient
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.feed.ActiveTask
import com.example.mpdriver.components.feed.FeedTaskDataCard
import com.example.mpdriver.storage.Database
import com.example.mpdriver.type.StatusEnumQl
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.byUnicodePattern


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Feed(modifier: Modifier = Modifier, hostController: NavHostController) {
//    Fetch active task

    var isLoading by remember {
        mutableStateOf(true)
    }

    var activeTask by remember {
        mutableStateOf<GetActiveTaskIdQuery.Task?>(null)
    }
    var plannedTasks by remember {
        mutableStateOf(emptyList<GetActiveTaskIdQuery.PlannedTask>())
    }
    var completedTasks by remember {
        mutableStateOf(emptyList<GetActiveTaskIdQuery.CompletedTask>())
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
                else -> dateFormat.format(LocalDateTime.parse(plannedTasks[0].startPln.toString()))
            },
            "buttonLabel" to "Смотреть запланированные задачи",
            "dateDescription" to "Ближайшая",
        ),
        mapOf(
            "title" to "Завершенные задачи",
            "count" to completedTasks.count(),
            "date" to when (completedTasks.count()) {
                0 -> "-"
                else -> dateFormat.format(LocalDateTime.parse(completedTasks[0].startPln.toString()))
            },
            "buttonLabel" to "Смотреть завершенные задачи",
            "dateDescription" to "Последняя",
        ),
    )

    LaunchedEffect(Unit) {

        val tasks = Database.tasks

        val activeTaskDB = tasks.find { it.status == StatusEnumQl.IN_PROGRESS }
        val completedTasksDB = tasks.filter { it.status == StatusEnumQl.COMPLETED }
        val plannedTasksDB = tasks.filter { it.status == StatusEnumQl.NOT_DEFINED }

        activeTask = when (activeTaskDB) {
            null -> null
            else -> GetActiveTaskIdQuery.Task(id = activeTaskDB.id)
        }
        completedTasks = when (completedTasksDB.count()) {
            0 -> emptyList()
            else -> completedTasksDB.map {t ->
               GetActiveTaskIdQuery.CompletedTask(t.id, t.startPln)
            }
        }

        plannedTasks = when (plannedTasksDB.count()) {
            0 -> emptyList()
            else -> plannedTasksDB.map { t ->
                GetActiveTaskIdQuery.PlannedTask(t.id, t.startPln)
            }
        }
        Log.d("FEED:PlannedTasks", "${plannedTasks}")
        Log.d("FEED:ActiveTask", "${activeTask}")
        Log.d("FEED:CompletedTask", "${completedTasks}")

        isLoading = false
    }

    if(isLoading) {
        Column (
            Modifier
                .fillMaxWidth()
                .padding(vertical = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFFE5332A))
        }
        return
    }


    Layout(dataList = dataList, header = {
        Column {
            ActiveTask(activeTaskID = activeTask?.id?.toLong(), hostController = hostController)
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




