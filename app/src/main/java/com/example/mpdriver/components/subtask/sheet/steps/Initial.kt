package com.example.mpdriver.components.subtask.sheet.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.components.ButtonType
import com.example.mpdriver.components.JDEButton

@Composable
fun InitialStep(navigateTo: (ActionRoutes) -> Unit) {
    Column {
        JDEButton(type = ButtonType.SUCCESS, onClick = { navigateTo(ActionRoutes.Success) }) {
            Text(text = "Подзадача выполнена", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        JDEButton(type = ButtonType.WARNING, onClick = { navigateTo(ActionRoutes.Failure) }) {
            Text(text = "У меня возникла проблема", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }

}