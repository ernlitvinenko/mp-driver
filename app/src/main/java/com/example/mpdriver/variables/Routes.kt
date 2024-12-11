package com.example.mpdriver.variables

sealed class Routes(val route: String) {
    data object Auth: Routes("auth") {
        data object Code: Routes("auth/code")
    }
    data object Home: Routes("home") {
        data object Feed: Routes("home/feed")
        data object Events: Routes("home/events")
        data object Notifications: Routes("home/notifications")
        data object Chat: Routes("home/chat")
        data object Tasks: Routes("home/tasks")
        data object Map: Routes("home/map")
    }

}