package com.example.mpdriver

import android.Manifest
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.mpdriver.components.HomeScreenLayout
import com.example.mpdriver.components.InDevelopmentComponent
import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.screens.AddEventScreen
import com.example.mpdriver.screens.Feed
import com.example.mpdriver.screens.ListEventsScreen
import com.example.mpdriver.screens.MapScreen
import com.example.mpdriver.screens.NoteScreen
import com.example.mpdriver.screens.PhoneCodeInputScreen
import com.example.mpdriver.screens.PhoneInputScreen
import com.example.mpdriver.screens.SubtaskScreen
import com.example.mpdriver.screens.TasksList
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.Route
import com.example.mpdriver.variables.Routes
import com.example.mpdriver.viewmodels.AuthViewModel
import com.example.mpdriver.viewmodels.MainViewModel
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yandex.mapkit.MapKitFactory


class MainActivity : ComponentActivity() {
    val timeTickReciever = TimeTickReciever()

    lateinit var  appUpdater: AppUpdaterUtils

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("f4385b18-0740-454a-a71f-d20da7e8fc3b")
        MapKitFactory.initialize(this)

        appUpdater = AppUpdaterUtils(this)
            .setUpdateFrom(UpdateFrom.JSON)
            .setUpdateJSON("https://raw.githubusercontent.com/ernlitvinenko/mp-driver-update/refs/heads/main/update-changelog.json")
            .withListener(object: AppUpdaterUtils.UpdateListener {
                override fun onSuccess(update: Update?, isUpdateAvailable: Boolean?) {
                    Log.d("Latest Version", update!!.getLatestVersion());
                    Log.d("Latest Version Code", update.getLatestVersionCode().toString());
                    Log.d("Release notes", update.getReleaseNotes());
                    Log.d("URL", update.getUrlToDownload().toString());
                    Log.d("Is update available?", isUpdateAvailable.toString());
                }

                override fun onFailed(error: AppUpdaterError?) {
                    Log.d("AppUpdater Error", "Something went wrong");
                }

            })

//        App updater


        appUpdater.start()

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
        appUpdater.stop()
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
            BackHandler(enabled = true) {

            }
            PhoneInputScreen(navigateTo = {
                navigateTo(Routes.Auth.Code)
            }, viewmodel = authViewModel)
        }
        composable(Routes.Auth.Code.route) {
            BackHandler(enabled = true) {
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
            }
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) }, exitAccountAction = {
                mainViewModel.dropAccessToken()
                authViewModel.clearAllData()
                navigateTo(Routes.Auth)
            }) {
                Feed(navigateToTasks = {
                    navController.navigate(Routes.Home.Tasks.route)
                }, model = mainViewModel, navigateToTask = {
                    navigateTo(Routes.Home.Tasks.Task.navigateTo(it))
                },
                    navigateToHome = { navigateTo(Routes.Home.Feed) })
            }
        }
        composable(Routes.Home.Chat.route) {
            BackHandler(true) {
            }
            HomeScreenLayout(navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) },
                title = "Чат") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    InDevelopmentComponent {
                        navigateTo(Routes.Home.Feed)
                    }
                }

            }
        }
        composable(Routes.Home.Events.route) {
            BackHandler(true) {
            }
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) },
                title = "События"
                ) {
               ListEventsScreen(model = mainViewModel, navigateTo = {navigateTo(it)})
            }
        }

        composable(Routes.Home.Events.Add.route) {
            BackHandler(true) {
                navigateTo(Routes.Home.Events)
            }
            HomeScreenLayout(navigateUp = { navigateUp()}, navigateTo = {navigateTo(it)},
                title = "Добавить событие", backlink = true) {
                AddEventScreen {
                    navigateTo(it)
                }
            }
        }

        composable(Routes.Home.Notifications.route) {
            BackHandler(true) {

            }
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) }, title = "Уведомления") {
                NoteScreen()
            }
        }
        composable(Routes.Home.Tasks.route) {
            BackHandler(true) {
                navigateTo(Routes.Home.Feed)
            }
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
            BackHandler(enabled = true) {

            }
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

            BackHandler(enabled = true) {

            }
            HomeScreenLayout(
                navigateUp = { navigateUp() },
                navigateTo = { navigateTo(it) },
                title = "Карта"
            ) {
                MapScreen()
            }
        }
    }
}
