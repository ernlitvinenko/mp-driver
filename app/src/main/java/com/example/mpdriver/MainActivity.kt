package com.example.mpdriver

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mpdriver.api.Api
import com.example.mpdriver.api.TaskApi
import com.example.mpdriver.api.apolloClient
import com.example.mpdriver.api.fetchAppDataToDB


import com.example.mpdriver.api.toJson
import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.screens.Feed
import com.example.mpdriver.screens.MapScreen
import com.example.mpdriver.screens.NoteScreen
import com.example.mpdriver.screens.PhoneCodeInputScreen
import com.example.mpdriver.screens.PhoneInputScreen
import com.example.mpdriver.screens.SubtaskScreen
import com.example.mpdriver.screens.TasksList
import com.example.mpdriver.storage.Database
import com.tencent.mmkv.MMKV
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.launch
import okhttp3.Dispatcher


class MainActivity : ComponentActivity() {
    val timeTickReciever = TimeTickReciever()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("f4385b18-0740-454a-a71f-d20da7e8fc3b")
        MapKitFactory.initialize(this)
        val rootDir = MMKV.initialize(this)
        Log.d("MMKV.KEYS", Database.allKeys!!.joinToString())

        println("mmkv root: $rootDir")
        registerReceiver(timeTickReciever, IntentFilter(Intent.ACTION_TIME_TICK))

        enableEdgeToEdge()
        setContent {
            AuthNavigator()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeTickReciever)
    }
}

@Composable
fun AuthNavigator() {
    val authNavigator = rememberNavController()
    Api.setNavHostController(authNavigator)
    val startDestination = if (Database.access_token == null) "auth" else "home"
    NavHost(navController = authNavigator, startDestination = startDestination) {
        composable("auth") {
            PhoneInputScreen(navHostController = authNavigator)
        }
        composable("auth/code") {
            PhoneCodeInputScreen(navHostController = authNavigator)
        }
        composable("home") {
            MainNavigator()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigator() {
    var headerTitle by remember {
        mutableStateOf("Лента")
    }
    var backLink by remember {
        mutableStateOf(false)
    }

    var loadingData by remember {
        mutableStateOf(true)
    }


    val bottomNavController = rememberNavController()

    val api = TaskApi(LocalContext.current)

    LaunchedEffect(Unit) {
        api.send_task_status_chains(onResponse = {
            Database.dropUpdates()
        })
        apolloClient.fetchAppDataToDB()
        loadingData = false
    }

    if (loadingData)
        return Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFFE5332A))
                Text( text = "Происходит обновление даннных, Пожалуйста, подождите.", fontSize = 16.sp, textAlign = TextAlign.Center)
            }
        }


    Scaffold(
        topBar = {
            Header(
                hostController = bottomNavController,
                title = headerTitle,
                backLink = backLink
            )
        },
        bottomBar = {
            Footer(
                hostController = bottomNavController
            )
        },
    ) {


        Box(modifier = Modifier.padding(it)) {
            NavHost(
                navController = bottomNavController,
                startDestination = "feed",
            ) {
                composable("feed",
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            tween(200)
                        )
                    }
                ) {
                    backLink = false
                    headerTitle = "Лента"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        Feed(hostController = bottomNavController)
                    }
                }
                composable("feed/planned-tasks",

                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left, tween(200)
                        )
                    }

                ) {
                    backLink = true
                    headerTitle = "Задачи"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        TasksList(hostController = bottomNavController)
                    }
                }
                composable("events") {
                    backLink = false
                    headerTitle = "События"

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        Text(text = "События")
                    }

                }
                composable("settings") {
                    backLink = false
                    headerTitle = "Настройки"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        Text(text = "Profile")
                    }
                }
                composable("notifications") {
                    backLink = false
                    headerTitle = "Уведомления"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        NoteScreen()
                    }
                }
                composable("chat") {
                    backLink = false
                    headerTitle = "Чат"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        Text(text = "Chat")
                    }
                }
                composable("map",
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        ExitTransition.None
                    }) {
                    backLink = false
                    headerTitle = "Карта"
                    MapScreen()
                }
                composable("tasks/{taskId}", arguments = listOf(navArgument("taskId") {
                    type = NavType.LongType
                }),
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left, tween(200)
                        )
                    }
                ) { bse ->
                    bse.arguments?.let { args ->
                        headerTitle = "Детали задачи"
                        backLink = true
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {
                            SubtaskScreen(args.getLong("taskId"))
                        }
                        return@composable
                    }


                }
            }
        }
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String = "Лента",
    backLink: Boolean = false,
    hostController: NavHostController
) {
    Row(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .height(100.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (backLink) {
                IconButton(onClick = { hostController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
            Text(
                text = title,
                modifier = Modifier,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (!backLink) {
                TextButton(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black,
                    )
                ) {
                    Icon(
                        Icons.Sharp.Settings,
                        contentDescription = "Settings",
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }


        }
    }
}

data class FooterNavMenuLabel(
    val title: String,
    val defaultImage: Int,
    val imageActive: Int
)

@Composable
fun Footer(
    modifier: Modifier = Modifier,
    hostController: NavHostController
) {

    val menuItems = mapOf<String, FooterNavMenuLabel>(
        "feed" to FooterNavMenuLabel("Лента", R.drawable.home_default, R.drawable.home),
        "events" to FooterNavMenuLabel("События", R.drawable.calendar_default, R.drawable.calendar),
        "notifications" to FooterNavMenuLabel(
            "Уведомления",
            R.drawable.bell_default,
            R.drawable.bell
        ),
        "chat" to FooterNavMenuLabel("Чат", R.drawable.chat_default, R.drawable.chat),
        "map" to FooterNavMenuLabel("Карта", R.drawable.location_default, R.drawable.location)
    )

    var activeRoute by remember {
        mutableStateOf("feed")
    }

    Row(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            menuItems.forEach {
                TextButton(
                    onClick = {
                        activeRoute = it.key
                        hostController.navigate(it.key)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black,
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (activeRoute == it.key) {
                            Image(
                                painter = painterResource(id = it.value.imageActive),
                                contentDescription = ""
                            )
                            Text(text = it.value.title, fontSize = 11.sp, color = Color.Black)
                        } else {
                            Image(
                                painter = painterResource(id = it.value.defaultImage),
                                contentDescription = ""
                            )
                            Text(text = it.value.title, fontSize = 11.sp, color = Color.Gray)
                        }

                    }
                }

            }
        }
    }
}




