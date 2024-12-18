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

interface ApiCalls {
    fun success(data: SuccessStepApiCallData)
    fun failure(data: FailureStepApiCallData)
}

@Composable
fun Navigator(subtask: AppTask, apiCalls: ApiCalls, onRouteChange: (ActionRoutes) -> Unit) {
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
            onRouteChange(currentRoute)
            SuccessStep(subtask = subtask) {
                apiCalls.success(it)
            }
        }

        composable(ActionRoutes.Failure.route) {
            currentRoute = ActionRoutes.Failure
            onRouteChange(currentRoute)
            FailureStep(subtask = subtask) {
                apiCalls.failure(it)
            }
        }
    }
}