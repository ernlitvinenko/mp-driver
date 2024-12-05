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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.apollographql.apollo3.api.ApolloResponse
import com.example.mpdriver.GetPlannedTasksIDsQuery
import com.example.mpdriver.GetTaskByIdQuery
import com.example.mpdriver.NotificationApplication
//import com.example.mpdriver.storage.api.TaskApi
//import com.example.mpdriver.storage.api.TaskResponse
//import com.example.mpdriver.storage.api.apolloClient
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.HeaderTabs
import com.example.mpdriver.components.HeaderTabsData
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.StaleButton
import com.example.mpdriver.components.Task
//import com.example.mpdriver.database.models.APP_EVENT
//import com.example.mpdriver.storage.CreateUpdateTaskData
//import com.example.mpdriver.storage.Database
import com.example.mpdriver.type.StatusEnumQl
import com.example.mpdriver.variables.JDEColor
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.time.LocalDateTime


@Composable
fun TasksList(modifier: Modifier = Modifier, hostController: NavHostController) {
    val context = LocalContext.current
//    val db = (context.applicationContext as NotificationApplication).db


    var isLoading by remember {
        mutableStateOf(true)
    }


    var itemsListResponse by remember {
        mutableStateOf(emptyList<GetPlannedTasksIDsQuery.Task>())
    }


    var activeTab by remember {
        mutableIntStateOf(0)
    }


    LaunchedEffect(activeTab) {


//        if (activeTab == 0) {
//            itemsListResponse = db.taskDao().getNotDefinedTasks().map {
//                GetPlannedTasksIDsQuery.Task(
//                    id = it.id.toString(),
//                    subtasks = db.taskDao().getSubtasksForTask(it.id).map { sbt ->
//                        GetPlannedTasksIDsQuery.Subtask(id = sbt.id.toString())
//                    })
//            }
//        } else {
//            itemsListResponse = db.taskDao().getCompletedTasks().map {
//                GetPlannedTasksIDsQuery.Task(
//                    id = it.id.toString(),
//                    subtasks = db.taskDao().getSubtasksForTask(it.id).map { sbt ->
//                        GetPlannedTasksIDsQuery.Subtask(id = sbt.id.toString())
//                    })
//            }
//        }

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
    Layout(dataList = itemsListResponse, header = {
        HeaderTabs(
            tabsData = listOf(
                HeaderTabsData(0, "Запланированные"),
                HeaderTabsData(1, "Завершенные")
            ), activeTab = activeTab
        ) {
            activeTab = it
        }
    }) {
        Task(Modifier.padding(bottom = 10.dp), task_id = it.id.toLong()) {
            when (activeTab) {
                0 -> Button(
                    onClick = {
                        val nowTime = Clock.System.now().toEpochMilliseconds()
//                        val sbt = db.taskDao().getSubtasksForTask(id = it.id.toLong())[0]
//                        db.eventDao().insert(
//                            mutableListOf(
//                                APP_EVENT(
//                                    recId = it.id.toLong(),
//                                    vidId = 8678,
//                                    typeId = 0,
//                                    dateTime = nowTime.toString(),
//                                    data = """[{"8794":"8681"}]""",
//                                    params = "",
//                                    isOnServer = false,
//                                    text = "Set status to IN_PROGRESS",
//                                    id = 0
//                                ),
//
//                                APP_EVENT(
//                                    recId = sbt.id,
//                                    vidId = 8678,
//                                    typeId = 0,
//                                    dateTime = nowTime.toString(),
//                                    data = """[{"8794":"8681"}]""",
//                                    params = "",
//                                    isOnServer = false,
//                                    text = "Set status to IN_PROGRESS",
//                                    id = 0
//                                )
//                            ))
//                                    hostController . navigate ("feed")
//
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color.Black,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(10.dp),
//                    enabled = db.taskDao().getActiveTask() == null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Приступить к выполнению")
                }
            }
        }
    }

}

