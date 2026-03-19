package com.quickchat.app.data.remote

/**
 * DTOs for WebSocket message transport.
 * In production these would be serialized to/from JSON over the wire.
 */
data class MessageDto(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val type: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT,
    TYPING,
    STATUS_UPDATE,
    ONLINE_STATUS
}

data class StatusUpdateDto(
    val messageId: String,
    val status: String
)

data class TypingDto(
    val chatId: String,
    val userId: String,
    val isTyping: Boolean
)

data class OnlineStatusDto(
    val userId: String,
    val isOnline: Boolean
)
