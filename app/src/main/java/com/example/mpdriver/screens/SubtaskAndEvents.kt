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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mpdriver.components.HeaderTabs
import com.example.mpdriver.components.HeaderTabsData
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.Subtask
import com.example.mpdriver.data.models.AppTask
import com.example.mpdriver.variables.JDEColor


@Preview(showBackground = true)
@Composable
fun SubtaskScreen(taskId: Long = 0) {

//    var api = TaskApi()

    var isLoading by remember {
        mutableStateOf(true)
    }

    var floatingActionShowOffset by remember {
        mutableStateOf(0f)
    }


    var datalist by remember {
        mutableStateOf(emptyList<AppTask>())
    }

    var activeTab by remember {
        mutableStateOf(0)
    }

    val listState = rememberLazyListState()
    var offset = listState.firstVisibleItemScrollOffset + listState.firstVisibleItemIndex

    val tabsData =
        listOf<HeaderTabsData>(HeaderTabsData(0, "Подзадачи"), HeaderTabsData(1, "События"))

    LaunchedEffect(Unit) {
//        val task = Database.tasks.find { it.id == taskId.toString() }
//        datalist = Database.subtasks.filter { sbt ->
//            task!!.subtasks.find { it.id == sbt.id } != null
//        }.sortedBy { LocalDateTime.parse(it.startPln.toString()) }
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

    Layout(state = listState, dataList = datalist, header = {
        HeaderTabs(modifier = Modifier.onGloballyPositioned { lc ->
            val cords = lc.boundsInParent()
            floatingActionShowOffset = cords.bottom
        }, tabsData = tabsData, activeTab = activeTab) {
            activeTab = it
        }
    }) {
        Subtask(subtaskData = it)
    }

    AnimatedVisibility(
        visible = offset + 200 > floatingActionShowOffset + 100,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        ExtendedFloatingActionButton(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(10.dp),
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(text = "Скрыть завершенные задачи")
            }
        }
    }
}