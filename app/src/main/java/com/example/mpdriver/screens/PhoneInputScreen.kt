package com.example.mpdriver.screens

import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpdriver.NotificationService
import com.example.mpdriver.errors.AuthErrors
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.viewmodels.AuthViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@Composable
fun PhoneInputScreen(navigateTo: () -> Unit = {}, viewmodel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val service = NotificationService(context)
    val phone = viewmodel.phoneNumber.observeAsState("")

    var errorText by remember {
        mutableStateOf("")
    }

    fun setErrorMessage(error: AuthErrors) {
        errorText = error.displayText
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
        Text(text = phone.value)
        TextField(
            value = phone.value,
            isError = errorText != "",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                viewmodel.onChangePhoneNumber(
                    preformatPhoneNumber(it).filter { char -> char.isDigit() }
                )
            },

            placeholder = { Text(text = "+7 (999) 999-99-99") },

            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = JDEColor.TEXT_FIELD_BG_COLOR.color,
                focusedContainerColor = JDEColor.TEXT_FOCUSED_FIELD_COLOR.color,
                focusedIndicatorColor = JDEColor.PRIMARY.color,
                errorContainerColor = JDEColor.TEXT_FOCUSED_FIELD_COLOR.color,
                errorTextColor = JDEColor.PRIMARY.color
            ),
            visualTransformation = PhoneNumberVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                autoCorrect = false,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = {

                MainScope().launch {
                    proceedCode(
                        handleOk = {
                            navigateTo()
                        },
                        setErrorMessage = { error ->
                            setErrorMessage(error)
                        },
                        viewmodel = viewmodel,
                        notificationService = service
                    )

                }
            })
        )
        Text(text = errorText, color = JDEColor.PRIMARY.color, fontWeight = FontWeight.Bold)
    }
}

fun preformatPhoneNumber(input: String): String {
    val number =  when {
        input.startsWith("+7") || input.startsWith("8") -> input
        input.startsWith("7") -> input
        input.startsWith("9") -> "7$input"
        else -> input
    }

    return when (number.length) {
        in 0..11 -> number
        else -> number.slice(0..11)
    }
}

fun formatPhoneNumber(digits: String): String {
    val sb = StringBuilder()
    for (i in digits.indices) {
        when (i) {
            0 -> sb.append("+")
            1 -> sb.append(" (")
            4 -> sb.append(") ")
            7 -> sb.append("-")
            9 -> sb.append("-")
        }
        sb.append(digits[i])
    }
    return sb.toString()
}


class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedText = formatPhoneNumber(text.text.filter { it.isDigit() })
        Log.d("Phone_visual_transformation", "filter: $formattedText")
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Account for added formatting characters
                return formattedText.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Remove formatting characters to map back
                return text.text.filter { it.isDigit() }.length
            }
        }
        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}


suspend fun proceedCode(handleOk: () -> Unit, setErrorMessage: (error: AuthErrors) -> Unit, viewmodel: AuthViewModel, notificationService: NotificationService) {
    val data = viewmodel.getCode()

    data?.let {
        when (it.status) {
            0 -> {
                Log.d("sms_code", "${it.code}")
                notificationService.showNotificationAuthCode(it.code.toString())
                handleOk()
            }
            2 -> {
                setErrorMessage(AuthErrors.InvalidPhoneNumber)
            }
            5 -> {
                setErrorMessage(AuthErrors.NoProfileFounded)
            }
            else -> {
                setErrorMessage(AuthErrors.ServerError)
            }
        }
    }
}
