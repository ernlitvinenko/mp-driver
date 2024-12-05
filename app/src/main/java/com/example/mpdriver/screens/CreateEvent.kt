//package com.example.mpdriver.screens
//
//import android.widget.Space
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ExposedDropdownMenuBox
//import androidx.compose.material3.ExposedDropdownMenuDefaults
//import androidx.compose.material3.MenuDefaults
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextFieldColors
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.mpdriver.components.InformationPlaceholderSmall
//import com.example.mpdriver.variables.JDEColor
//import com.example.mpdriver.variables.Typography
//import okhttp3.internal.immutableListOf
//
//
//enum class EventOptions:  {
//    SLEEP,
//    FUEL,
//    ACCIDENT,
//    DINNER,
//    REPAIRING
//}
////
//
////
////object EventOptions {
////
////    val SLEEP = "Сон"
////    val FUEL = "Заправка"
////    val ACCIDENT = "ДТП"
////    val DINNER = "Обед"
////    val REPAIRING = "Ремонт ТС"
////}
//
//val optionsList: List<String> = immutableListOf(
//    EventOptions.FUEL,
//    EventOptions.SLEEP,
//    EventOptions.DINNER,
//    EventOptions.ACCIDENT,
//    EventOptions.REPAIRING
//).toString()
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
//@Composable
//fun CreateEventScreen(modifier: Modifier = Modifier) {
//
//    var expanded by remember {
//        mutableStateOf(false)
//    }
//    var eventValue by remember {
//        mutableStateOf(EventOptions.SLEEP)
//    }
//
//    Column(
//        modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        LazyColumn {
//            item {
//                Typography.TITLE.H1(text = "Добавить событие")
//                Spacer(modifier = Modifier.height(15.dp))
//            }
//            item {
//                Typography.PARAGRAPH.BASE.P5(text = "Выбирете событие из списка:")
//
//            }
//
//            item {
//                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
//                    OutlinedTextField(
//                        readOnly = true,
//                        value = eventValue,
//                        onValueChange = {},
//                        label = { Text(text = "Событие") },
//                        trailingIcon = {
//                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//                        },
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedBorderColor = JDEColor.PRIMARY,
//                            focusedPlaceholderColor = JDEColor.PRIMARY,
//                            focusedLabelColor = JDEColor.PRIMARY
//                        ),
//                        modifier = Modifier
//                            .menuAnchor()
//                            .fillMaxWidth()
//                    )
//                    ExposedDropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false },
//                        Modifier.background(JDEColor.BG_GRAY)
//                    ) {
//                        optionsList.forEach { option ->
//                            DropdownMenuItem(text = {
//                                Typography.PARAGRAPH.BASE.P5(
//                                    text = option,
//                                )
//                            }, onClick = {
//                                eventValue = option
//                                expanded = false
//                            })
//                        }
//                    }
//
//                }
//                Spacer(modifier = Modifier.height(17.dp))
//            }
//
//            item {
//                Card()
//            }
//
//
//        }
//
//    }
//}
//
//
////class EventCardFields {
////   fun getFields(option: ) {
////       when
////   }
////}
//
//@Composable
//@Preview(showBackground = true)
//fun Card() {
//
//    Column(
//        Modifier
//            .fillMaxWidth()
//            .border(BorderStroke(2.dp, JDEColor.SECONDARY), shape = RoundedCornerShape(10.dp))
//            .padding(16.dp)
//    )
//    {
//
////        TITLE
//        Typography.TITLE.H3(text = "Ремонт ТС", align = TextAlign.Left)
//        Spacer(modifier = Modifier.height(17.dp))
//
//        // Fields
//        OutlinedTextField(
//            modifier = Modifier.fillMaxWidth(),
//            value = "",
//            label = {Typography.PARAGRAPH.BASE.P3(text = "Дата", align = TextAlign.Left)},
//            onValueChange = {}, colors = OutlinedTextFieldDefaults.colors(
//                focusedContainerColor = JDEColor.BG_GRAY,
//                unfocusedContainerColor = JDEColor.BG_GRAY,
//                unfocusedBorderColor = JDEColor.BG_GRAY,
//                focusedBorderColor = JDEColor.BLACK, focusedLabelColor = JDEColor.BLACK)
//        )
//        Spacer(modifier = Modifier.height(10.dp))
//    }
//}