package com.example.mpdriver.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.Route


@Composable
fun <T> Layout(modifier: Modifier = Modifier, header: @Composable () -> Unit = {}, dataList: List<T>, state : LazyListState = rememberLazyListState(), itemComponent: @Composable (T) -> Unit) {
    LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), state = state) {
        item {
            header()
        }
        items(items = dataList) {
            itemComponent(it)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenLayout(
    navigateUp: () -> Unit,
    navigateTo: (Route) -> Unit,
    title: String = "Лента",
    backlink: Boolean = false,
    exitAccountAction: () -> Unit = {},
    content: @Composable () -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var isSheetVisible by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            Header(
                title = title,
                navigateUp = { navigateUp() },
                backLink = backlink,
                openSettingsAction = { isSheetVisible = true }
            )
        },
        bottomBar = {
            Footer(navigateTo = { navigateTo(it) })
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                content()
            }
        }
    }
    if (isSheetVisible) {
        ModalBottomSheet(onDismissRequest = { isSheetVisible = false }, sheetState = sheetState, containerColor = Color.White) {
            Column (
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)) {
                OutlinedButton(onClick = { exitAccountAction() }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(contentColor = JDEColor.PRIMARY.color, containerColor = Color.Transparent), border = BorderStroke(1.dp, JDEColor.PRIMARY.color)) {
                    Text(text = "Выйти из аккаунта", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
