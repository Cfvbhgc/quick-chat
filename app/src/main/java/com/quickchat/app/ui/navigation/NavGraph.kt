package com.quickchat.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quickchat.app.ui.screens.chat.ChatScreen
import com.quickchat.app.ui.screens.chatlist.ChatListScreen
import com.quickchat.app.ui.screens.profile.ProfileScreen
import com.quickchat.app.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ChatList.route
    ) {
        composable(Screen.ChatList.route) {
            ChatListScreen(
                onChatClick = { chatId, chatName ->
                    navController.navigate(Screen.Chat.createRoute(chatId, chatName))
                },
                onProfileClick = { userId ->
                    navController.navigate(Screen.Profile.createRoute(userId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("chatName") { type = NavType.StringType }
            )
        ) {
            ChatScreen(
                onBackClick = { navController.popBackStack() },
                onProfileClick = { userId ->
                    navController.navigate(Screen.Profile.createRoute(userId))
                }
            )
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
