package com.example.mpdriver.components.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.apollographql.apollo3.api.ApolloResponse
import com.example.mpdriver.GetActiveSubtaskIDQuery
import com.example.mpdriver.GetActiveTaskIdQuery
import com.example.mpdriver.GetTaskByIdQuery
import com.example.mpdriver.NotificationApplication
//import com.example.mpdriver.storage.api.TaskResponse
//import com.example.mpdriver.storage.api.apolloClient
import com.example.mpdriver.components.ButtonType
import com.example.mpdriver.components.EmptyList
import com.example.mpdriver.components.IteractionButton
import com.example.mpdriver.components.JDEButton
import com.example.mpdriver.components.Subtask
import com.example.mpdriver.components.Task
//import com.example.mpdriver.storage.Database
import com.example.mpdriver.type.StatusEnumQl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTask(activeTaskID: Long? = null, hostController: NavHostController) {
    val context = LocalContext.current
//    val db = (context.applicationContext as NotificationApplication).db
    var responseGetActiveSubtaskID by remember {
        mutableStateOf<GetActiveSubtaskIDQuery.ActiveSubtask?>(null)
    }

//    activeTaskID?.let {
//        LaunchedEffect(Unit) {
//            activeTaskID.let {
//                responseGetActiveSubtaskID =
//                    db.taskDao().getActiveSubtaskForTask(activeTaskID)?.let { asbt ->
//                        GetActiveSubtaskIDQuery.ActiveSubtask(asbt.id.toString())
//                    }
//            }
//        }
//    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
    ) {
        if (activeTaskID == null) {
            EmptyList(Modifier.padding(vertical = 60.dp), text = "У вас нет активных задач")
            return
        }
        Text(
            text = "Активная задача",
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 18.sp
        )

        activeTaskID.let {
            IteractionButton(onClick = { hostController.navigate("tasks/${activeTaskID}") }) {
                Task(task_id = activeTaskID)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Активная подзадача",
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(10.dp))


        responseGetActiveSubtaskID.let { task ->
            IteractionButton(onClick = { /*TODO*/ }) {
                responseGetActiveSubtaskID?.let {
                    Subtask(subtaskID = it.id.toLong())
                }
            }
        }
    }
}


