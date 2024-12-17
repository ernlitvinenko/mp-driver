package com.example.mpdriver.components.subtask.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpdriver.components.subtask.sheet.steps.ActionRoutes
import com.example.mpdriver.components.subtask.sheet.steps.Navigator
import com.example.mpdriver.data.models.AppTask


private enum class SheetState {
    HIDE,
    OPEN,
    FULL_SCREEN
}


@Composable
private fun rememberSheetState(): MutableState<SheetState> {
    return remember {
        mutableStateOf<SheetState>(SheetState.OPEN)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtaskSheet(
    setStateAction: () -> Unit = { },
    subtask: AppTask
) {
    var title by remember {
        mutableStateOf("Вам удалось выполнить подзадачу?")
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var sheetStateCurrent by rememberSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            setStateAction()
        },
        containerColor = Color.White,
        sheetState = sheetState
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .imePadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp), horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
            Column {
                Navigator(subtask = subtask) { route ->
                    when (route) {
                        ActionRoutes.Initial -> {
                            title ="Вам удалось выполнить подзадачу?"
                        }
                        ActionRoutes.Success -> title = "Когда вы выполнили подзадачу?"
                        ActionRoutes.Failure -> title = "В чем ваша проблема?"
                    }
                }

            }


        }

    }

    LaunchedEffect(sheetStateCurrent) {
        when (sheetStateCurrent) {
            SheetState.OPEN -> sheetState.show()
            SheetState.HIDE -> {
                setStateAction()
                sheetState.hide()
            }

            SheetState.FULL_SCREEN -> sheetState.expand()
        }
    }
}