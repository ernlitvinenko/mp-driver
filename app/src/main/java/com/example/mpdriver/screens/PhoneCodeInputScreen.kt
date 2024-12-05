package com.example.mpdriver.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.viewmodels.AuthViewModel
//import com.example.mpdriver.storage.api.Auth
//import com.example.mpdriver.storage.Database
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@Composable
fun PhoneCodeInputScreen(model: AuthViewModel = viewModel()) {
    var errorText by remember {
        mutableStateOf("")
    }
    var code = model.phoneCode.observeAsState("")

    var isSuccess by remember {
        mutableStateOf(false)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), verticalArrangement = Arrangement.Center
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "JDE Перевозчик",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Введите код из СМС",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(15.dp))

        BasicTextField(
            value = code.value,
            onValueChange = {
                model.onChangePhoneCode(it)
                if (code.value.length == 4) {
//                    TODO Call API Method for auth
//                    TODO Set access_token in MainViewModel
//                    TODO set error_text on API Error
                }
            },

            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            decorationBox = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    repeat(4) { index ->
                        val isFocused = code.value.length == index
                        val char = when {
                            index >= code.value.length -> ""
                            else -> code.value[index].toString()
                        }
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .width(40.dp)
                                .border(
                                    if (isFocused) 3.dp else 1.dp,
                                    color = when {
                                        isSuccess -> JDEColor.SUCCESS.color
                                        errorText != "" -> JDEColor.PRIMARY.color
                                        else -> Color.Gray
                                    },
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp),
                            text = char,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            })
        Spacer(modifier = Modifier.padding(bottom = 10.dp))
        Text(text = errorText, color = JDEColor.PRIMARY.color, fontWeight = FontWeight.Bold)
    }
}
