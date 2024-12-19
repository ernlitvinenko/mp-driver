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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mpdriver.components.Footer
import com.example.mpdriver.components.Header
import com.example.mpdriver.components.HomeScreenLayout
import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.screens.Feed
import com.example.mpdriver.screens.MapScreen
import com.example.mpdriver.screens.PhoneCodeInputScreen
import com.example.mpdriver.screens.PhoneInputScreen
import com.example.mpdriver.screens.SubtaskAndEventsTab
import com.example.mpdriver.screens.SubtaskScreen
import com.example.mpdriver.screens.TasksList
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.Route
import com.example.mpdriver.variables.Routes
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
//        val pingWorkManager = PeriodicWorkRequestBuilder<PingServiceWorker>(15, TimeUnit.SECONDS).build()

//        WorkManager.getInstance(this).enqueueUniquePeriodicWork("PingServerWork", ExistingPeriodicWorkPolicy.KEEP, pingWorkManager)


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


@Composable
fun Navigator(
    model: AuthViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    var startDestination by remember {
        mutableStateOf<Route>(Routes.Auth)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }

    fun navigateTo(route: Route) {
        navController.navigate(route.route)
    }

    fun navigateUp() {
        navController.navigateUp()
    }


    LaunchedEffect(Unit) {
        mainViewModel.initializeDatabase(context)
        mainViewModel.initAccessToken()
        if (mainViewModel.isAuthorized()) {
            startDestination = Routes.Home.Feed
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = false
        startDestination = Routes.Auth
    }

    if (isLoading) {
        CircularProgressIndicator(color = JDEColor.PRIMARY.color)
        return
    }

    NavHost(navController = navController, startDestination = startDestination.route) {
        composable(Routes.Auth.route) {
            PhoneInputScreen(navigateTo = {
                navigateTo(Routes.Auth.Code)
            }, viewmodel = authViewModel)
        }
        composable(Routes.Auth.Code.route) {
            PhoneCodeInputScreen(
                authViewModel = authViewModel,
                mainViewModel = mainViewModel,
                navigateTo = {
                    navigateTo(Routes.Home.Feed)
                })
        }
        composable(Routes.Home.Feed.route) {
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) }) {
                Feed(navigateToTasks = {
                    navController.navigate(Routes.Home.Tasks.route)
                }, model = mainViewModel, navigateToTask = {
                    navigateTo(Routes.Home.Tasks.Task.navigateTo(it))
                },
                    navigateToHome = { navigateTo(Routes.Home.Feed) })
            }
        }
        composable(Routes.Home.Chat.route) {
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) }) {
                Text(text = "Чат")
            }
        }
        composable(Routes.Home.Events.route) {
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) }) {
                Text(text = "События")
            }
        }
        composable(Routes.Home.Notifications.route) {
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) }) {
                Text(text = "Уведомления")
            }
        }
        composable(Routes.Home.Tasks.route) {
            HomeScreenLayout(
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) }, title = "Задачи"
            ) {
                TasksList(mainViewModel = mainViewModel)
            }
        }
        composable(
            Routes.Home.Tasks.Task.route,
            arguments = Routes.Home.Tasks.Task.navArguments
        ) { stackEntry ->
            HomeScreenLayout(
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) },
                title = "Детали задачи",
                backlink = true
            ) {
                SubtaskScreen(Routes.Home.Tasks.Task.backStack(stackEntry)!!, mainViewModel)
            }
        }

        composable(Routes.Home.Map.route) {
            HomeScreenLayout(navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) },
                title = "Карта") {
                MapScreen()
            }
        }
    }
}
