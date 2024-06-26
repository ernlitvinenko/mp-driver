package com.example.mpdriver.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mpdriver.NotificationService
import com.example.mpdriver.api.Auth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@Composable
fun PhoneInputScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    var errorText by remember {
        mutableStateOf("")
    }
    var phone by remember {
        mutableStateOf("")
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
            text = "Введите номер телефона",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(15.dp))
        TextField(
            isError = errorText != "",
            modifier = Modifier.fillMaxWidth(),
            value = phone, onValueChange = { phone = it.filter { it.isDigit() } },
            placeholder = { Text(text = "+7 (999) 999-99-99") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF2F2F2),
                focusedContainerColor = Color(0xFFE2E2E2),
                focusedIndicatorColor = Color(0xFFE5332A),
                errorContainerColor = Color(0xFFE2E2E2),
                errorTextColor = Color(0xFFE5332A)
            ),
            visualTransformation = VisualTransformation { text ->
                TransformedText(
                    AnnotatedString(
                        phoneChecking(text.text)
                    ), object : OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int {
                            when (offset) {
                                1 -> return offset + 1
                                in 2..4 -> return offset + 3
                                in 5..7 -> return offset + 5
                                in 8..9 -> return offset + 6
                                in 10..11 -> return offset + 7
                                else -> return offset
                            }
                        }

                        override fun transformedToOriginal(offset: Int): Int {
                            return offset
                        }

                    }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                autoCorrect = false,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = {
                Auth(context).getPhoneCode(phone.filter { it.isDigit() }) {

                    it.code?.let {
                        NotificationService(context).showNotificationAuthCode(it.toString())

                        MainScope().launch {
                            navHostController.navigate("auth/code")
                        }

                    }

                    it.error?.let { error ->
                        errorText = it.langs!!.ru!!
                    }


                }
            })
        )
        Text(text = errorText, color = Color(0xFFE5332A), fontWeight = FontWeight.Bold)
    }
}

fun phoneChecking(text: String): String {
    val digitText = text.filter { char -> char.isDigit() }


    when (digitText.length) {
        1 -> return "+7"
        in 2..4 -> return "+7 (${digitText.slice(1..<digitText.length)}"
        in 5..7 -> return "+7 (${digitText.slice(1..3)}) ${digitText.slice(4..<digitText.length)}"
        in 8..9 -> return "+7 (${digitText.slice(1..3)}) ${digitText.slice(4..6)}-${
            digitText.slice(
                7..<digitText.length
            )
        }"

        in 10..11 -> return "+7 (${digitText.slice(1..3)}) ${digitText.slice(4..6)}-${
            digitText.slice(
                7..8
            )
        }-${digitText.slice(9..<digitText.length)}"
    }


    return ""
}