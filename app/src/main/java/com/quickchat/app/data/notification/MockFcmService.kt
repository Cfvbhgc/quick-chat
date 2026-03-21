package com.quickchat.app.data.notification

import com.quickchat.app.data.remote.ChatWebSocket
import com.quickchat.app.data.remote.MessageDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simulates Firebase Cloud Messaging by observing incoming WebSocket messages
 * and posting local notifications. In production this would be a FirebaseMessagingService.
 */
@Singleton
class MockFcmService @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val webSocket: ChatWebSocket
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Track which chat the user currently has open so we skip notifications for it
    @Volatile
    var activeChatId: String? = null

    private val senderNames = mapOf(
        "user_2" to "Alice Johnson",
        "user_3" to "Bob Smith",
        "user_4" to "Carol White",
        "user_5" to "Dave Brown",
        "user_6" to "Eve Davis"
    )

    fun start() {
        webSocket.incomingMessages
            .onEach { message -> handleIncomingMessage(message) }
            .launchIn(scope)
    }

    private fun handleIncomingMessage(message: MessageDto) {
        // Don't notify for the chat the user is currently viewing
        if (message.chatId == activeChatId) return

        val senderName = senderNames[message.senderId] ?: "Unknown"
        notificationHelper.showMessageNotification(senderName, message.text, message.chatId)
    }
}
