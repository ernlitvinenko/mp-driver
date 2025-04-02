package com.example.mpdriver.components.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.NotificationData
import com.example.mpdriver.NotificationService
//import com.example.mpdriver.storage.api.TaskResponse
//import com.example.mpdriver.storage.api.apolloClient
import com.example.mpdriver.components.EmptyList
import com.example.mpdriver.components.IteractionButton
import com.example.mpdriver.components.subtask.Subtask
import com.example.mpdriver.components.TaskComponent
import com.example.mpdriver.components.subtask.sheet.steps.ApiCalls
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.data.models.TaskStatus
import com.example.mpdriver.variables.Routes
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun ActiveTask(activeTask: AppTask? = null,
               apiCalls: ApiCalls,
               model: MainViewModel,
               navigateToTask: (Long) -> Unit = {}

) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val notificationService = NotificationService(context)

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
    ) {

        if (activeTask == null) {
            EmptyList(Modifier.padding(vertical = 20.dp), text = "У вас нет активных задач")
            return
        }
        Text(
            text = "Активная задача",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        TaskComponent(taskData = activeTask, onClick = {
            navigateToTask(activeTask.id)
        }, onClickCloseTask = {
            scope.launch {
                model.changeTask(activeTask, status = TaskStatus.COMPLETED, datetime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
                model.fetchTaskData()
                withContext(Dispatchers.Main) {
                    notificationService.showNotification(
                        NotificationData(
                            title = "MP Водитель - Изменился статус Задачи",
                            text = "Текущий статус подзадачи - Выполнено"
                        )
                    )
                }
            }
        })
    }

    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "Активная подзадача",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.height(10.dp))
    activeTask?.subtasks?.find { subtaskData ->
        subtaskData.status == TaskStatus.IN_PROGRESS
    }?.let {
        Subtask(subtaskData = it, apiCalls = apiCalls, model = model)
    }


}


