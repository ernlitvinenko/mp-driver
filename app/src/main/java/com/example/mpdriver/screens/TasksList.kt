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
import com.example.mpdriver.api.TaskApi
import com.example.mpdriver.api.TaskResponse
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.HeaderTabs
import com.example.mpdriver.components.HeaderTabsData
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.StaleButton
import com.example.mpdriver.components.Task
import com.example.mpdriver.storage.Database
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@Composable
fun TasksList(modifier: Modifier = Modifier, hostController: NavHostController) {
    val api = TaskApi(LocalContext.current)

    var isLoading by remember {
        mutableStateOf(true)
    }


    var itemsList by remember {
        mutableStateOf<List<TaskResponse>>(emptyList())
    }
    api.getPlannedTasks {
        itemsList = it
        isLoading = false
    }

    var activeTab by remember {
        mutableIntStateOf(0)
    }


    if(isLoading) {
        Column (Modifier.fillMaxWidth().padding(vertical = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFFE5332A))
        }
        return
    }
    Layout(dataList = itemsList, header = {
        HeaderTabs(
            tabsData = listOf(
                HeaderTabsData(0, "Запланированные"),
                HeaderTabsData(1, "Завершенные")
            ), activeTab = activeTab
        ) {
            activeTab = it
        }
    }) {
        Task(Modifier.padding(bottom = 10.dp), taskResponse = it) {
            when (activeTab) {
                0 -> Button(
                    onClick = {
                            api.setTaskStatusInProgress(it.id!!) {
                                MainScope().launch {
                                    hostController.navigate("feed")
                                }
                            }
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color.Black,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    enabled = if (Database.tasks.filter { it.status == "InProgress" }.count() >= 1) false else true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Приступить к выполнению")
                }
            }
        }
    }

}

