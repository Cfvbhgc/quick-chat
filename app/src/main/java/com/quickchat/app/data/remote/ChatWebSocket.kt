package com.quickchat.app.data.remote

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over the WebSocket connection.
 * Decouples transport from business logic so we can swap mock/real implementations.
 */
interface ChatWebSocket {
    val incomingMessages: Flow<MessageDto>
    val typingEvents: Flow<TypingDto>
    val onlineStatusEvents: Flow<OnlineStatusDto>
    val statusUpdates: Flow<StatusUpdateDto>

    fun connect()
    fun disconnect()
    fun sendMessage(message: MessageDto)
    fun sendTypingEvent(chatId: String, isTyping: Boolean)
}
