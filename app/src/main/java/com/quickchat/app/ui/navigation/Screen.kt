package com.quickchat.app.ui.navigation

sealed class Screen(val route: String) {
    data object ChatList : Screen("chat_list")
    data object Chat : Screen("chat/{chatId}/{chatName}") {
        fun createRoute(chatId: String, chatName: String) = "chat/$chatId/$chatName"
    }
    data object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
    data object Settings : Screen("settings")
}
