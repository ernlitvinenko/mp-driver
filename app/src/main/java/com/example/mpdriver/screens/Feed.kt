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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.feed.ActiveTask
import com.example.mpdriver.components.feed.FeedTaskDataCard
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.Routes
import com.example.mpdriver.variables.datetimeFormatFrom
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.byUnicodePattern

private data class FeedDataListProps(
    val title: String,
    val count: Int,
    val date: String,
    val buttonLabel: String,
    val dateDescription: String
)


@Composable
fun Feed(
    modifier: Modifier = Modifier,
    model: MainViewModel = viewModel(),
    navigateToTask: (Long) -> Unit = {},
    navigateToTasks: () -> Unit = {},
) {

    //    Fetch active task
    var isLoading by remember {
        mutableStateOf(true)
    }


    val dateFormat = LocalDateTime.Format {
        byUnicodePattern("dd.MM.yyyy")
    }


    val dataList = listOf(
        FeedDataListProps(
            title = "Запланированные задачи",
            count = model.plannedTasks.count(),
            date = when (model.plannedTasks.count()) {
                0 -> "-"
                else -> dateFormat.format(
                    LocalDateTime.parse(
                        model.plannedTasks[0].startPln,
                        datetimeFormatFrom
                    )
                )
            },
            buttonLabel = "Смотреть запланированные задачи",
            dateDescription = "Ближайшая"
        ),
        FeedDataListProps(
            title = "Завершенные задачи",
            count = model.completedTasks.count(),
            when (model.completedTasks.count()) {
                0 -> "-"
                else -> dateFormat.format(
                    LocalDateTime.parse(
                        model.completedTasks[0].startPln,
                        datetimeFormatFrom
                    )
                )
            },
            buttonLabel = "Смотреть завершенные задачи",
            dateDescription = "Последняя"
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
            ActiveTask(activeTask = model.activeTask, navigateToTask = {navigateToTask(it)})
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
            navigateToTasks()
        }
    }
}




