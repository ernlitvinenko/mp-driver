package com.example.mpdriver

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.mpdriver.data.api.RetrofitClient
import com.example.mpdriver.data.models.AppTaskResponse
import com.example.mpdriver.data.models.GetPhoneCodeRequest


import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.screens.PhoneCodeInputScreen
import com.example.mpdriver.screens.PhoneInputScreen
import com.example.mpdriver.viewmodels.AuthViewModel
import com.example.mpdriver.viewmodels.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yandex.mapkit.MapKitFactory


class MainActivity : ComponentActivity() {
    val timeTickReciever = TimeTickReciever()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("f4385b18-0740-454a-a71f-d20da7e8fc3b")
        MapKitFactory.initialize(this)
        registerReceiver(timeTickReciever, IntentFilter(Intent.ACTION_TIME_TICK))
        val viewModel: MainViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
            val notificationPermissionResultLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}
            val notificationPermissionState =
                rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

            if (notificationPermissionState.status.isGranted) {
                Column {
                    Navigator()
                }
            }

            LaunchedEffect(notificationPermissionState) {
                if (!notificationPermissionState.status.isGranted) {
                    notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeTickReciever)
    }

}


sealed class Routes(val route: String) {
    object Auth: Routes("auth") {
        object Code: Routes("auth/code")
    }
    object Home: Routes("home")
}

@Composable
fun Navigator(model: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val startDestination = "auth"
    NavHost(navController = navController , startDestination = startDestination) {
        composable(Routes.Auth.route) {
            PhoneInputScreen(navigateTo = {
                navController.navigate(Routes.Auth.Code.route)
            })
        }
        composable(Routes.Auth.Code.route) {
            PhoneCodeInputScreen()
        }
        composable(Routes.Home.route) {
//            MainNavigator()
        }
    }
}
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainNavigator() {
//    var headerTitle by remember {
//        mutableStateOf("Лента")
//    }
//    var backLink by remember {
//        mutableStateOf(false)
//    }
//
//    var loadingData by remember {
//        mutableStateOf(true)
//    }
//
//    val bottomNavController = rememberNavController()
//    val context = LocalContext.current
//
//    val api = TaskApi(context)
//
//    LaunchedEffect(Unit) {
//
//        apolloClient.fetchAppDataToDB(context.applicationContext as NotificationApplication)
//        loadingData = false
//    }
//
//    if (loadingData)
//        return Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
//            Column(
//                Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                CircularProgressIndicator(color = Color(0xFFE5332A))
//                Text(
//                    text = "Выполняется обновление даннных, Пожалуйста, подождите.",
//                    fontSize = 16.sp,
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//
//
//    Scaffold(
//        topBar = {
//            Header(
//                hostController = bottomNavController,
//                title = headerTitle,
//                backLink = backLink
//            )
//        },
//        bottomBar = {
//            Footer(
//                hostController = bottomNavController
//            )
//        },
//    ) {
//
//
//        Box(modifier = Modifier.padding(it)) {
//            NavHost(
//                navController = bottomNavController,
//                startDestination = "feed",
//            ) {
//                composable("feed",
//                    exitTransition = {
//                        slideOutOfContainer(
//                            AnimatedContentTransitionScope.SlideDirection.Left,
//                            tween(200)
//                        )
//                    }
//                ) {
//                    backLink = false
//                    headerTitle = "Лента"
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                    ) {
//                        Feed(hostController = bottomNavController)
//                    }
//                }
//                composable("feed/planned-tasks",
//
//                    enterTransition = {
//                        slideIntoContainer(
//                            AnimatedContentTransitionScope.SlideDirection.Left, tween(200)
//                        )
//                    }
//
//                ) {
//                    backLink = true
//                    headerTitle = "Задачи"
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                    ) {
//                        TasksList(hostController = bottomNavController)
//                    }
//                }
//                composable("events") {
//                    backLink = false
//                    headerTitle = "События"
//
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                    ) {
////                        CreateEventScreen()
//                    }
//
//                }
//                composable("settings") {
//                    backLink = false
//                    headerTitle = "Настройки"
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                    ) {
//                        Text(text = "Profile")
//                    }
//                }
//                composable("notifications") {
//                    backLink = false
//                    headerTitle = "Уведомления"
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                    ) {
//                        NoteScreen()
//                    }
//                }
//                composable("chat") {
//                    backLink = false
//                    headerTitle = "Чат"
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                    ) {
//                        Text(text = "Chat")
//                    }
//                }
//                composable("map",
//                    enterTransition = {
//                        fadeIn()
//                    },
//                    exitTransition = {
//                        ExitTransition.None
//                    }) {
//                    backLink = false
//                    headerTitle = "Карта"
//                    MapScreen()
//                }
//                composable("tasks/{taskId}", arguments = listOf(navArgument("taskId") {
//                    type = NavType.LongType
//                }),
//                    enterTransition = {
//                        slideIntoContainer(
//                            AnimatedContentTransitionScope.SlideDirection.Left, tween(200)
//                        )
//                    }
//                ) { bse ->
//                    bse.arguments?.let { args ->
//                        headerTitle = "Детали задачи"
//                        backLink = true
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .fillMaxHeight()
//                        ) {
//                            SubtaskScreen(args.getLong("taskId"))
//                        }
//                        return@composable
//                    }
//
//
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Header(
//    modifier: Modifier = Modifier,
//    title: String = "Лента",
//    backLink: Boolean = false,
//    hostController: NavHostController
//) {
//    Row(
//        modifier = modifier
//            .shadow(10.dp, RoundedCornerShape(10.dp))
//            .fillMaxWidth()
//            .background(Color.White)
//            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
//            .height(100.dp)
//            .padding(horizontal = 16.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if (backLink) {
//                IconButton(onClick = { hostController.navigateUp() }) {
//                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
//                }
//            }
//            Text(
//                text = title,
//                modifier = Modifier,
//                fontSize = 30.sp,
//                fontWeight = FontWeight.SemiBold
//            )
//            if (!backLink) {
//                TextButton(
//                    onClick = { /*TODO*/ },
//                    colors = ButtonDefaults.textButtonColors(
//                        contentColor = Color.Black,
//                    )
//                ) {
//                    Icon(
//                        Icons.Sharp.Settings,
//                        contentDescription = "Settings",
//                    )
//                }
//            } else {
//                Spacer(modifier = Modifier.weight(1f))
//            }
//
//
//        }
//    }
//}
//
//data class FooterNavMenuLabel(
//    val title: String,
//    val defaultImage: Int,
//    val imageActive: Int
//)
//
//@Composable
//fun Footer(
//    modifier: Modifier = Modifier,
//    hostController: NavHostController
//) {
//
//    val menuItems = mapOf<String, FooterNavMenuLabel>(
//        "feed" to FooterNavMenuLabel("Лента", R.drawable.home_default, R.drawable.home),
//        "events" to FooterNavMenuLabel("События", R.drawable.calendar_default, R.drawable.calendar),
//        "notifications" to FooterNavMenuLabel(
//            "Уведомления",
//            R.drawable.bell_default,
//            R.drawable.bell
//        ),
//        "chat" to FooterNavMenuLabel("Чат", R.drawable.chat_default, R.drawable.chat),
//        "map" to FooterNavMenuLabel("Карта", R.drawable.location_default, R.drawable.location)
//    )
//
//    var activeRoute by remember {
//        mutableStateOf("feed")
//    }
//
//    Row(
//        modifier = modifier
//            .shadow(10.dp, RoundedCornerShape(10.dp))
//            .fillMaxWidth()
//            .background(Color.White)
//            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
//            .padding(horizontal = 5.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 40.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            menuItems.forEach {
//                TextButton(
//                    onClick = {
//                        activeRoute = it.key
//                        hostController.navigate(it.key)
//                    },
//                    colors = ButtonDefaults.textButtonColors(
//                        contentColor = Color.Black,
//                    )
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        if (activeRoute == it.key) {
//                            Image(
//                                painter = painterResource(id = it.value.imageActive),
//                                contentDescription = ""
//                            )
//                            Text(text = it.value.title, fontSize = 11.sp, color = Color.Black)
//                        } else {
//                            Image(
//                                painter = painterResource(id = it.value.defaultImage),
//                                contentDescription = ""
//                            )
//                            Text(text = it.value.title, fontSize = 11.sp, color = Color.Gray)
//                        }
//
//                    }
//                }
//
//            }
//        }
//    }
//}




