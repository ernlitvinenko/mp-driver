package com.example.mpdriver.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.MainActivity
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.JDEButton
import com.example.mpdriver.components.Layout
import com.example.mpdriver.data.database.Tables
import com.example.mpdriver.variables.JDEColor


private data class Settings(
    val text: String,
    val value: MutableState<String>,
    val onValueChange: (String) -> Unit
)

@Preview(showBackground = true)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    var serverIP = remember {
        mutableStateOf(Tables.ServerAPIBaseURL.getValue() ?: "")
    }
    var updateIP = remember {
        mutableStateOf(Tables.UpdatesAPIBaseUrl.getValue() ?: "")
    }

    val context =  LocalContext.current as MainActivity

    val settings = mutableListOf(
        Settings(text = "IP адрес сервера данных",
            value = serverIP,
            onValueChange = {
                serverIP.value = it
            }),
        Settings(
            text = "IP адрес сервера обновлений",
            value = updateIP,
            onValueChange = {
                updateIP.value = it
            })
    )

    Layout(dataList = settings,
        header = {
            Column(
                Modifier
                    .border(1.dp, color = JDEColor.PRIMARY.color, shape = RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Внимание! При изменении расширенных настроек необходимо перезапустить приложение, чтобы настройки применились",
                    color = JDEColor.PRIMARY.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Расширенные настройки", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
        },
        footer = {
            ActiveButton(modifier = Modifier.fillMaxWidth(), text = "Сохранить", onClick = {
                Tables.ServerAPIBaseURL.setValue(serverIP.value)
                Tables.UpdatesAPIBaseUrl.setValue(updateIP.value)
                context.finish()
            })
        }) {
        Column(Modifier.padding(vertical = 10.dp)) {
            Text(text = it.text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(5.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = it.value.value,
                onValueChange = { value ->
                    it.onValueChange(value)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                    focusedContainerColor = JDEColor.TEXT_FOCUSED_FIELD_COLOR.color,
                    focusedIndicatorColor = JDEColor.PRIMARY.color,
                    errorContainerColor = JDEColor.TEXT_FOCUSED_FIELD_COLOR.color,
                    errorTextColor = JDEColor.PRIMARY.color
                )
            )
        }
    }
}
