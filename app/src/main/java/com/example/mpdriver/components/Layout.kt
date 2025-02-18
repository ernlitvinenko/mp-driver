package com.example.mpdriver.components

import android.util.Log
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpdriver.MainActivity
import com.example.mpdriver.R
import com.example.mpdriver.data.api.RetrofitUpdateApi
import com.example.mpdriver.data.api.UpdateChangeLogResponse
import com.example.mpdriver.helpers.isAppInstalled
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.Route
import com.example.mpdriver.variables.Routes
import com.example.mpdriver.variables.version
import com.example.mpdriver.viewmodels.MainViewModel
import com.example.mpdriver.viewmodels.UpdateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun <T> Layout(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
    dataList: List<T>,
    state: LazyListState = rememberLazyListState(),
    itemComponent: @Composable (T) -> Unit
) {
    LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), state = state
    ) {
        item {
            header()
        }

        if (dataList.isEmpty()) {
            item {
                EmptyList(Modifier.padding(vertical = 60.dp), text = "Здесь пока пусто")
            }
        } else {
            items(items = dataList) {
                itemComponent(it)
            }
        }
        item {
            footer()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenLayout(
    updateViewModel: UpdateViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(),
    navigateUp: () -> Unit,
    navigateTo: (Route) -> Unit,
    title: String = "Лента",
    backlink: Boolean = false,
    exitAccountAction: () -> Unit = {},
    content: @Composable () -> Unit
) {

    val context = LocalContext.current as MainActivity

    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val updateSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var isSheetVisible by remember {
        mutableStateOf(false)
    }

    var isSheetUpdateVisible by remember {
        mutableStateOf(false)
    }

    var sheetUpdateData by remember {
        mutableStateOf<UpdateChangeLogResponse?>(null)
    }

    var isDownloadInProgress by remember {
        mutableStateOf(false)
    }

    var downloadProgress by remember {
        mutableStateOf(0f)
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
            Footer(navigateTo = { navigateTo(it) }, model = mainViewModel)
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
        ModalBottomSheet(
            onDismissRequest = { isSheetVisible = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Версия $version",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val data = updateViewModel.checkForUpdates(context)
                            withContext(Dispatchers.Main) {
                                data.let {
                                    isSheetVisible = false
                                    sheetUpdateData = it
                                    isSheetUpdateVisible = true
                                }
                            }
                        }
                    },
                    Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = JDEColor.BLACK.color,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, JDEColor.BLACK.color),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Проверить наличие обновлений", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val data = updateViewModel.installYanavi()
                            withContext(Dispatchers.Main) {
                                data.let {
                                    isSheetVisible = false
                                    sheetUpdateData = it
                                    isSheetUpdateVisible = true
                                }
                            }
                        }
                    },
                    Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = JDEColor.BLACK.color,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, JDEColor.BLACK.color),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isAppInstalled(context, "ru.yandex.yandexnavi"),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.yanavi),
                            contentDescription = "yanavi"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Установка Яндекс навигатора", fontWeight = FontWeight.Bold)
                    }
                }
                
                IteractionButton(onClick = { navigateTo(Routes.Settings) }) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(text = "Расширенные настройки", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = ">")
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 10.dp))
                OutlinedButton(
                    onClick = { exitAccountAction() },
                    Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = JDEColor.PRIMARY.color,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, JDEColor.PRIMARY.color)
                ) {
                    Text(text = "Выйти из аккаунта", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    if (isSheetUpdateVisible) {
        ModalBottomSheet(
            onDismissRequest = { isSheetUpdateVisible = false },
            sheetState = updateSheetState,
            containerColor = Color.White
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {

                if (isDownloadInProgress) {
                    Text(
                        "Выполняется обновление,\nпожалуйста подождите.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    LinearProgressIndicator(progress = { downloadProgress },
                        Modifier
                            .padding(5.dp)
                            .fillMaxWidth(), color = JDEColor.PRIMARY.color)
                } else {
                    Text(
                        if (sheetUpdateData != null) "Доступно новое обновление" else "Нет новых обновлений",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (sheetUpdateData != null) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                sheetUpdateData?.link?.let {
                                    isDownloadInProgress = true
                                    val downloadId = context.appUpdater.downloadAndInstallApk(
                                        it
                                    )
                                    coroutineScope.launch {
                                        context.appUpdater.getPercentageOfDownloading(downloadId) {
                                            downloadProgress = it
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JDEColor.PRIMARY.color)
                        ) {
                            Text(text = "Загрузить обновление")
                        }
                    }
                }


            }
        }
    }
}
