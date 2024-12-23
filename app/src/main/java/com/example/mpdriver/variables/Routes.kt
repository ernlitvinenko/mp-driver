package com.example.mpdriver.variables

import androidx.navigation.NavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument


open class Route(private vararg val routes: String) {
    constructor(parentRoute: Route, route: String) : this(parentRoute.route, route)

    val route
        get() = routes.joinToString("/")
}


data class NavArgumentType(
    val name: String,
    val type: NavType<*>
)


sealed class Routes(val route: String) {
    data object Auth : Route("auth") {
        data object Code : Route(Auth, "code")
    }

    data object Home : Route("home") {
        data object Feed : Route(Home, "feed")
        data object Events : Route(Home, "events") {
            data object Add: Route (Events, "add")
        }
        data object Notifications : Route(Home, "notifications")
        data object Chat : Route(Home, "chat")
        data object Tasks : Route(Home, "tasks") {
            data object Task : Route(Tasks, "{taskId}") {
                val navArguments = listOf(navArgument("taskId") {
                    type = NavType.LongType
                })

                fun backStack(navBackStackEntry: NavBackStackEntry): Long? {
                    return navBackStackEntry.arguments?.getLong("taskId")
                }

                fun navigateTo(taskId: Long): Route {
                    return Route(Tasks, "$taskId")
                }

            }
        }

        data object Map : Route(Home, "map")
    }

}