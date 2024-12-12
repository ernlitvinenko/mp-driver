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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.mpdriver.components.Footer
import com.example.mpdriver.components.Header
import com.example.mpdriver.components.TaskComponent

import com.example.mpdriver.data.api.RetrofitClient
import com.example.mpdriver.data.models.GetPhoneCodeRequest


import com.example.mpdriver.recievers.TimeTickReciever
import com.example.mpdriver.screens.Feed
import com.example.mpdriver.screens.PhoneCodeInputScreen
import com.example.mpdriver.screens.PhoneInputScreen
import com.example.mpdriver.screens.TasksList
import com.example.mpdriver.variables.JDEColor
import com.example.mpdriver.variables.Routes
import com.example.mpdriver.viewmodels.AuthViewModel
import com.example.mpdriver.viewmodels.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.transport.bicycle.Route


class MainActivity : ComponentActivity() {
    val timeTickReciever = TimeTickReciever()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("f4385b18-0740-454a-a71f-d20da7e8fc3b")
        MapKitFactory.initialize(this)
        registerReceiver(timeTickReciever, IntentFilter(Intent.ACTION_TIME_TICK))


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
        mutableStateOf<Routes>(Routes.Auth)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }

    fun navigateTo(route: Routes) {
        navController.navigate(route.route)
    }

    fun navigateUp() {
        navController.navigateUp()
    }

//    TODO write logic for token auth

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
                navController.navigate(Routes.Auth.Code.route)
            }, viewmodel = authViewModel)
        }
        composable(Routes.Auth.Code.route) {
            PhoneCodeInputScreen(
                authViewModel = authViewModel,
                mainViewModel = mainViewModel,
                navigateTo = {
                    navController.navigate(Routes.Home.Feed.route)
                })
        }
        composable(Routes.Home.Feed.route) {
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) }) {
                Feed(navigateToTasks = {
                    navController.navigate(Routes.Home.Tasks.route)
                }, model = mainViewModel)
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
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) }) {
                TasksList(mainViewModel = mainViewModel)
            }
        }
        composable(Routes.Home.Map.route) {
            HomeScreenLayout(navigateUp = { navigateUp() }, navigateTo = { navigateTo(it) }) {
                Text(text = "Карта")
            }
        }
    }
}

@Composable
fun HomeScreenLayout(
    navigateUp: () -> Unit,
    navigateTo: (Routes) -> Unit,
    title: String = "Лента",
    backlink: Boolean = false,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            Header(title = title,
                navigateUp = { navigateUp() },
                backLink = backlink)
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
}
