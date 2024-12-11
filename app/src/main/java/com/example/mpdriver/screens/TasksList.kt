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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.mpdriver.components.TaskComponent
//import com.example.mpdriver.database.models.APP_EVENT
//import com.example.mpdriver.storage.CreateUpdateTaskData
//import com.example.mpdriver.storage.Database
import com.example.mpdriver.type.StatusEnumQl
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.viewmodels.MainViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.time.LocalDateTime


private enum class ActiveTab {
    PLANNED,
    COMPLETED
}

@Composable
fun TasksList(modifier: Modifier = Modifier, mainViewModel: MainViewModel = viewModel()) {
    var isLoading by remember {
        mutableStateOf(true)
    }

    var activeTab by remember {
        mutableStateOf(ActiveTab.PLANNED)
    }

    val tasks = mainViewModel.tasks.observeAsState(emptyList())


    LaunchedEffect(activeTab) {
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
    Layout(dataList = tasks.value, header = {
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
                    onClick = { /* TODO  Complete it */ }
                ) {
                    Text(text = "Приступить к выполнению")
                }
                else -> {}
            }
        }
    }
}


