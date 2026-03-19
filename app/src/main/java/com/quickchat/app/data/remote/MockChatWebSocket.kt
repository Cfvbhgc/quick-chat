package com.quickchat.app.data.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simulates a WebSocket server for demo purposes.
 *
 * When the user sends a message, this mock:
 * 1. Emits a DELIVERED status after a short delay (server acknowledged)
 * 2. Shows a typing indicator from the "other" user
 * 3. Sends back an auto-reply message
 * 4. Emits a READ status for the original message after the reply
 */
@Singleton
class MockChatWebSocket @Inject constructor() : ChatWebSocket {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _incomingMessages = MutableSharedFlow<MessageDto>(extraBufferCapacity = 64)
    override val incomingMessages: Flow<MessageDto> = _incomingMessages.asSharedFlow()

    private val _typingEvents = MutableSharedFlow<TypingDto>(extraBufferCapacity = 64)
    override val typingEvents: Flow<TypingDto> = _typingEvents.asSharedFlow()

    private val _onlineStatusEvents = MutableSharedFlow<OnlineStatusDto>(extraBufferCapacity = 64)
    override val onlineStatusEvents: Flow<OnlineStatusDto> = _onlineStatusEvents.asSharedFlow()

    private val _statusUpdates = MutableSharedFlow<StatusUpdateDto>(extraBufferCapacity = 64)
    override val statusUpdates: Flow<StatusUpdateDto> = _statusUpdates.asSharedFlow()

    // Pre-built reply pool so the mock feels somewhat natural
    private val autoReplies = listOf(
        "Got it, thanks!",
        "Sounds good to me",
        "Let me think about that...",
        "Sure, I'll get back to you soon",
        "That's a great idea!",
        "Can we discuss this later?",
        "I'm on it!",
        "Thanks for letting me know",
        "Absolutely!",
        "No worries at all"
    )

    // Maps chatId -> participant userId for generating replies
    private val chatParticipants = mapOf(
        "chat_1" to "user_2",
        "chat_2" to "user_3",
        "chat_3" to "user_4",
        "chat_4" to "user_5",
        "chat_5" to "user_6"
    )

    override fun connect() {
        // Simulate contacts coming online after connection
        scope.launch {
            delay(500)
            listOf("user_2", "user_3", "user_5").forEach { userId ->
                _onlineStatusEvents.emit(OnlineStatusDto(userId, isOnline = true))
                delay(200)
            }
        }
    }

    override fun disconnect() {
        // No-op for mock
    }

    override fun sendMessage(message: MessageDto) {
        scope.launch {
            val responderId = chatParticipants[message.chatId] ?: return@launch

            // Step 1: Message delivered to server
            delay(300)
            _statusUpdates.emit(StatusUpdateDto(message.id, "DELIVERED"))

            // Step 2: Responder starts typing
            delay(800)
            _typingEvents.emit(TypingDto(message.chatId, responderId, isTyping = true))

            // Step 3: Responder sends reply
            delay(1500)
            _typingEvents.emit(TypingDto(message.chatId, responderId, isTyping = false))

            val reply = MessageDto(
                id = UUID.randomUUID().toString(),
                chatId = message.chatId,
                senderId = responderId,
                text = autoReplies.random(),
                timestamp = System.currentTimeMillis()
            )
            _incomingMessages.emit(reply)

            // Step 4: Original message marked as read (they read it before replying)
            delay(200)
            _statusUpdates.emit(StatusUpdateDto(message.id, "READ"))
        }
    }

    override fun sendTypingEvent(chatId: String, isTyping: Boolean) {
        // In a real app this would go over the wire; mock ignores it
    }
}
