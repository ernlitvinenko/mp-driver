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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.apollographql.apollo3.api.ApolloResponse
import com.example.mpdriver.GetActiveTaskIdQuery
import com.example.mpdriver.GetTaskByIdQuery
import com.example.mpdriver.NotificationApplication
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.feed.ActiveTask
import com.example.mpdriver.components.feed.FeedTaskDataCard
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.data.models.TaskStatus
import com.example.mpdriver.type.StatusEnumQl
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.datetimeFormatFrom
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.byUnicodePattern



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Feed(modifier: Modifier = Modifier,
         model: MainViewModel = viewModel(),
         navigateToTasks: () -> Unit = {}) {
//    Fetch active task


    var isLoading by remember {
        mutableStateOf(true)
    }


//    val context = LocalContext.current


    val dateFormat = LocalDateTime.Format {
        byUnicodePattern("dd.MM.yyyy")
    }

    val dataList = listOf(
        mapOf(
            "title" to "Запланированные задачи",
            "count" to model.plannedTasks.count(),
            "date" to when (model.plannedTasks.count()) {
                0 -> "-"
                else -> dateFormat.format(LocalDateTime.parse(model.plannedTasks[0].startPln, datetimeFormatFrom))
            },
            "buttonLabel" to "Смотреть запланированные задачи",
            "dateDescription" to "Ближайшая",
        ),
        mapOf(
            "title" to "Завершенные задачи",
            "count" to model.completedTasks.count(),
            "date" to when (model.completedTasks.count()) {
                0 -> "-"
                else -> dateFormat.format(LocalDateTime.parse(model.completedTasks[0].startPln, datetimeFormatFrom))
            },
            "buttonLabel" to "Смотреть завершенные задачи",
            "dateDescription" to "Последняя",
        ),


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
            ActiveTask(activeTask = model.activeTask)
            Spacer(modifier = Modifier.height(20.dp))
        }

    }) {
        FeedTaskDataCard(
            title = it["title"].toString(),
            count = it["count"].toString().toInt(),
            dateDescription = it["dateDescription"].toString(),
            buttonLabel = it["buttonLabel"].toString(),
            date = it["date"].toString(),
        ) {
            navigateToTasks()
        }
    }
}




