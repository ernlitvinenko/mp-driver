package com.example.mpdriver.components.subtask.sheet.steps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mpdriver.data.models.AppTask


sealed class ActionRoutes(val route: String) {
    data object Initial: ActionRoutes("initial")
    data object Success: ActionRoutes("success")
    data object Failure: ActionRoutes("failure")
}

@Composable
fun Navigator(subtask: AppTask, onRouteChange: (ActionRoutes) -> Unit) {
    val controller = rememberNavController()

    var currentRoute by remember {
        mutableStateOf<ActionRoutes>(ActionRoutes.Initial)
    }

    NavHost(navController = controller, startDestination = currentRoute.route) {
        composable(ActionRoutes.Initial.route) {
            currentRoute = ActionRoutes.Initial
            InitialStep { rt ->
                controller.navigate(rt.route)
            }
        }
        composable(ActionRoutes.Success.route) {
            currentRoute = ActionRoutes.Success
            SuccessStep(subtask = subtask) {}
        }

        composable(ActionRoutes.Failure.route) {
            currentRoute = ActionRoutes.Failure
            FailureStep(subtask = subtask) {}
        }
    }
}