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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.JDEButton
import com.example.mpdriver.components.Layout
import com.example.mpdriver.variables.JDEColor


private data class Settings (
    val text: String,
    val value: String
)

@Preview(showBackground = true)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    val settings = mutableListOf(
        Settings(text = "IP адрес сервера данных", value = "10.2.100.110:30033"),
        Settings(text = "IP адрес сервера обновлений", value = "10.2.100.110:30033")
    )

    Layout(dataList = settings,
header = {
    Column (
        Modifier
            .border(1.dp, color = JDEColor.PRIMARY.color, shape = RoundedCornerShape(10.dp))
            .padding(10.dp)) {
        Text(text = "Внимание! При изменении расширенных настроек необходимо перезапустить приложение, чтобы настройки применились", color = JDEColor.PRIMARY.color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }

    Spacer(modifier = Modifier.height(20.dp))
    Text(text = "Расширенные настройки", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(20.dp))
},
        footer = {
        ActiveButton(modifier = Modifier.fillMaxWidth(), text = "Сохранить")
    }) {
        Column(Modifier.padding(vertical = 10.dp)) {
            Text(text = it.text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(5.dp))
            TextField(modifier = Modifier.fillMaxWidth(), value = it.value, onValueChange = {}, colors = TextFieldDefaults.colors(
                unfocusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                focusedContainerColor = JDEColor.TEXT_FOCUSED_FIELD_COLOR.color,
                focusedIndicatorColor = JDEColor.PRIMARY.color,
                errorContainerColor = JDEColor.TEXT_FOCUSED_FIELD_COLOR.color,
                errorTextColor = JDEColor.PRIMARY.color))
        }
    }
}
