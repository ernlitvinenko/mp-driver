package com.example.mpdriver

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mpdriver.components.ActiveButton
import com.example.mpdriver.components.HomeScreenLayout
import com.example.mpdriver.components.InDevelopmentComponent
import com.example.mpdriver.data.database.MMKVDb
import com.example.mpdriver.helpers.AppUpdateHelper
import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.screens.ActiveTab
import com.example.mpdriver.screens.AddEventScreen
import com.example.mpdriver.screens.Feed
import com.example.mpdriver.screens.ListEventsScreen
import com.example.mpdriver.screens.MapScreen
import com.example.mpdriver.screens.NoteScreen
import com.example.mpdriver.screens.PhoneCodeInputScreen
import com.example.mpdriver.screens.PhoneInputScreen
import com.example.mpdriver.screens.SettingsScreen
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

    lateinit var appUpdater: AppUpdateHelper

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            MapKitFactory.initialize(this)
        }
        catch (e: AssertionError) {
            MapKitFactory.setApiKey("f4385b18-0740-454a-a71f-d20da7e8fc3b")
            MapKitFactory.initialize(this)
        }

        MMKVDb.instance.initializeStorage(this)

        appUpdater = AppUpdateHelper(this)


        registerReceiver(timeTickReciever, IntentFilter(Intent.ACTION_TIME_TICK))

        enableEdgeToEdge()
        setContent {
            val notificationPermissionResultLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}
            val notificationPermissionState =
                rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
            val externalStoragePermission =
                rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (notificationPermissionState.status.isGranted) {
                Column {
                    Navigator()
                }
            }


            LaunchedEffect(notificationPermissionState, externalStoragePermission) {
                if (!notificationPermissionState.status.isGranted) {
                    notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                if (!externalStoragePermission.status.isGranted) {
                    notificationPermissionResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    val activity = LocalContext.current as? Activity

    NavHost(navController = navController, startDestination = startDestination.route) {
        composable(Routes.Settings.route) {
            BackHandler {
                activity?.finish()
            }
            SettingsScreen()

        }
        composable(Routes.Auth.route) {
            BackHandler(enabled = true) {
                activity?.finish()
            }
            Scaffold (
                topBar = {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp), Arrangement.End) {
                        ActiveButton(onClick = { navigateTo(Routes.Settings) }, text = "Настройки")
                    }
                }
            ){
                Box(modifier = Modifier.padding(it)) {
                    PhoneInputScreen(navigateTo = {
                        navigateTo(Routes.Auth.Code)
                    }, viewmodel = authViewModel)
                }
            }
        }
        composable(Routes.Auth.Code.route) {
            BackHandler(enabled = true) {
                mainViewModel.dropAccessToken()
                authViewModel.clearAllData()
                navigateTo(Routes.Auth)
            }
            PhoneCodeInputScreen(
                authViewModel = authViewModel,
                mainViewModel = mainViewModel,
                navigateTo = {
                    navigateTo(Routes.Home.Feed)
                })
        }
        composable(Routes.Home.Feed.route) {
            BackHandler(true) {
                activity?.finish()
            }
            HomeScreenLayout(
                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) },
                exitAccountAction = {
                    mainViewModel.dropAccessToken()
                    authViewModel.clearAllData()
                    navigateTo(Routes.Auth)
                }) {
                Feed(navigateTo = { navigateTo(it) },
                    model = mainViewModel, navigateToTask = {
                        navigateTo(Routes.Home.Tasks.Task.navigateTo(it))
                    })
            }
        }
        composable(Routes.Home.Chat.route) {
            BackHandler(true) {
                navigateTo(Routes.Home.Feed)
            }
            HomeScreenLayout(
                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) },
                title = "Чат"
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(), verticalArrangement = Arrangement.Center
                ) {
                    InDevelopmentComponent {
                        navigateTo(Routes.Home.Feed)
                    }
                }

            }
        }
        composable(Routes.Home.Events.route) {
            BackHandler(true) {
                navigateTo(Routes.Home.Feed)
            }
            HomeScreenLayout(
                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) },
                title = "События"
            ) {
                ListEventsScreen(model = mainViewModel, navigateTo = { navigateTo(it) })
            }
        }

        composable(Routes.Home.Events.Add.route) {
            BackHandler(true) {
                navigateTo(Routes.Home.Events)
            }
            HomeScreenLayout(
                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) },
                title = "Добавить событие", backlink = true
            ) {
                AddEventScreen {
                    navigateTo(it)
                }
            }
        }

        composable(Routes.Home.Notifications.route) {
            BackHandler(true) {
                navigateTo(Routes.Home.Feed)
            }
            HomeScreenLayout(

                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) },
                title = "Уведомления"
            ) {
                NoteScreen()
            }
        }
        composable(Routes.Home.Tasks.route) {
            BackHandler(true) {
                navigateTo(Routes.Home.Feed)
            }
            HomeScreenLayout(
                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) }, title = "Задачи"
            ) {
                TasksList(mainViewModel = mainViewModel, navigateTo = {navigateTo(it)})
            }
        }

        composable(Routes.Home.Tasks.Planned.route) {
            BackHandler(true) {
                navigateTo(Routes.Home.Feed)
            }
            HomeScreenLayout(
                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) }, title = "Задачи"
            ) {
                TasksList(mainViewModel = mainViewModel, navigateTo = {navigateTo(it)})
            }
        }

        composable(Routes.Home.Tasks.Closed.route) {
            BackHandler(true) {
                navigateTo(Routes.Home.Feed)
            }
            HomeScreenLayout(
                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) }, title = "Задачи"
            ) {
                TasksList(mainViewModel = mainViewModel, activeTabDefault = ActiveTab.COMPLETED, navigateTo = {navigateTo(it)})
            }
        }


        composable(
            Routes.Home.Tasks.Task.route,
            arguments = Routes.Home.Tasks.Task.navArguments
        ) { stackEntry ->
            BackHandler(enabled = true) {
                navigateTo(Routes.Home.Feed)
            }
            HomeScreenLayout(
                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) },
                title = "Детали задачи",
                backlink = true
            ) {
                SubtaskScreen(Routes.Home.Tasks.Task.backStack(stackEntry)!!, mainViewModel, navigateTo = {navigateTo(it)})
            }
        }

        composable(Routes.Home.Map.route) {

            BackHandler(enabled = true) {
                navigateTo(Routes.Home.Feed)
            }
            HomeScreenLayout(
                mainViewModel = mainViewModel,
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) },
                title = "Карта"
            ) {
                MapScreen()
            }
        }
    }
}
