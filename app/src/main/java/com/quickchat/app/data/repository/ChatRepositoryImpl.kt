package com.quickchat.app.data.repository

import com.quickchat.app.data.local.ChatDao
import com.quickchat.app.data.local.ChatEntity
import com.quickchat.app.data.local.MessageDao
import com.quickchat.app.data.local.MessageEntity
import com.quickchat.app.data.local.UserEntity
import com.quickchat.app.data.remote.ChatWebSocket
import com.quickchat.app.data.remote.MessageDto
import com.quickchat.app.domain.model.Chat
import com.quickchat.app.domain.model.Message
import com.quickchat.app.domain.model.MessageStatus
import com.quickchat.app.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central repository that bridges the WebSocket (real-time) layer with local Room persistence.
 * All incoming WS events are persisted to Room, and the UI observes Room flows.
 * This ensures a single source of truth and offline-first behavior.
 */
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val webSocket: ChatWebSocket
) : ChatRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _typingEvents = MutableSharedFlow<Pair<String, Boolean>>(extraBufferCapacity = 64)

    companion object {
        const val CURRENT_USER_ID = "user_1"
    }

    init {
        seedDataIfNeeded()
        observeWebSocketEvents()
    }

    override fun getChats(): Flow<List<Chat>> {
        return chatDao.getAllChats().map { chatEntities ->
            chatEntities.map { entity -> mapChatEntityToDomain(entity) }
        }
    }

    override fun getMessages(chatId: String): Flow<List<Message>> {
        return messageDao.getMessagesForChat(chatId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun sendMessage(chatId: String, text: String) {
        val messageId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val entity = MessageEntity(
            id = messageId,
            chatId = chatId,
            senderId = CURRENT_USER_ID,
            text = text,
            timestamp = timestamp,
            status = MessageStatus.SENT.name
        )
        messageDao.insertMessage(entity)
        chatDao.updateTimestamp(chatId, timestamp)

        // Send over the mock WebSocket to trigger the reply simulation
        webSocket.sendMessage(
            MessageDto(
                id = messageId,
                chatId = chatId,
                senderId = CURRENT_USER_ID,
                text = text,
                timestamp = timestamp
            )
        )
    }

    override suspend fun markAsRead(chatId: String) {
        messageDao.markAllAsRead(chatId, CURRENT_USER_ID)
        chatDao.updateUnreadCount(chatId, 0)
    }

    override fun getTypingEvents(): Flow<Pair<String, Boolean>> = _typingEvents.asSharedFlow()

    private fun observeWebSocketEvents() {
        // Incoming messages from other users
        webSocket.incomingMessages.onEach { dto ->
            val entity = MessageEntity(
                id = dto.id,
                chatId = dto.chatId,
                senderId = dto.senderId,
                text = dto.text,
                timestamp = dto.timestamp,
                status = MessageStatus.DELIVERED.name
            )
            messageDao.insertMessage(entity)
            chatDao.updateTimestamp(dto.chatId, dto.timestamp)

            // Increment unread count
            val chat = chatDao.getChatById(dto.chatId)
            chat?.let {
                chatDao.updateUnreadCount(dto.chatId, it.unreadCount + 1)
            }
        }.launchIn(scope)

        // Message status updates (SENT -> DELIVERED -> READ)
        webSocket.statusUpdates.onEach { update ->
            messageDao.updateMessageStatus(update.messageId, update.status)
        }.launchIn(scope)

        // Typing indicators
        webSocket.typingEvents.onEach { event ->
            _typingEvents.emit(event.chatId to event.isTyping)
        }.launchIn(scope)

        // Online status changes are handled by UserRepository
    }

    /**
     * Seeds the Room database with mock chats and messages on first launch.
     * In production this data would come from a REST API sync endpoint.
     */
    private fun seedDataIfNeeded() {
        scope.launch {
            if (chatDao.getChatById("chat_1") != null) return@launch

            val users = listOf(
                UserEntity("user_1", "You", null, true, System.currentTimeMillis()),
                UserEntity("user_2", "Alice Johnson", null, true, System.currentTimeMillis()),
                UserEntity("user_3", "Bob Smith", null, true, System.currentTimeMillis()),
                UserEntity("user_4", "Carol White", null, false, System.currentTimeMillis() - 3600_000),
                UserEntity("user_5", "Dave Brown", null, true, System.currentTimeMillis()),
                UserEntity("user_6", "Eve Davis", null, false, System.currentTimeMillis() - 7200_000)
            )
            users.forEach { chatDao.insertUser(it) }

            val now = System.currentTimeMillis()
            val chats = listOf(
                ChatEntity("chat_1", "user_1,user_2", 2, now - 60_000),
                ChatEntity("chat_2", "user_1,user_3", 0, now - 300_000),
                ChatEntity("chat_3", "user_1,user_4", 1, now - 600_000),
                ChatEntity("chat_4", "user_1,user_5", 0, now - 3600_000),
                ChatEntity("chat_5", "user_1,user_6", 3, now - 7200_000)
            )
            chats.forEach { chatDao.insertChat(it) }

            val messages = listOf(
                MessageEntity("msg_1", "chat_1", "user_2", "Hey, how's the project going?", now - 120_000, "READ"),
                MessageEntity("msg_2", "chat_1", "user_1", "Almost done with the UI!", now - 90_000, "READ"),
                MessageEntity("msg_3", "chat_1", "user_2", "That's awesome! Can't wait to see it", now - 60_000, "DELIVERED"),
                MessageEntity("msg_4", "chat_1", "user_2", "Send me screenshots when ready", now - 30_000, "DELIVERED"),
                MessageEntity("msg_5", "chat_2", "user_1", "Meeting at 3pm tomorrow?", now - 600_000, "READ"),
                MessageEntity("msg_6", "chat_2", "user_3", "Works for me!", now - 300_000, "READ"),
                MessageEntity("msg_7", "chat_3", "user_4", "Check out this new library", now - 900_000, "DELIVERED"),
                MessageEntity("msg_8", "chat_3", "user_1", "Looks interesting, I'll take a look", now - 700_000, "SENT"),
                MessageEntity("msg_9", "chat_3", "user_4", "Let me know what you think", now - 600_000, "DELIVERED"),
                MessageEntity("msg_10", "chat_4", "user_5", "Happy birthday! 🎉", now - 7200_000, "READ"),
                MessageEntity("msg_11", "chat_4", "user_1", "Thank you so much!", now - 3600_000, "READ"),
                MessageEntity("msg_12", "chat_5", "user_6", "Are we still on for Friday?", now - 10800_000, "DELIVERED"),
                MessageEntity("msg_13", "chat_5", "user_6", "I booked the restaurant", now - 9000_000, "DELIVERED"),
                MessageEntity("msg_14", "chat_5", "user_6", "Table for 6 at 7pm", now - 7200_000, "DELIVERED")
            )
            messages.forEach { messageDao.insertMessage(it) }
        }
    }

    private suspend fun mapChatEntityToDomain(entity: ChatEntity): Chat {
        val participantIds = entity.participantIds.split(",")
        val users = chatDao.getUsersByIds(participantIds).map { it.toDomain() }
        val lastMessage = messageDao.getLastMessage(entity.id)?.toDomain()

        return Chat(
            id = entity.id,
            participants = users,
            lastMessage = lastMessage,
            unreadCount = entity.unreadCount,
            updatedAt = entity.updatedAt
        )
    }

    private fun MessageEntity.toDomain() = Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        text = text,
        timestamp = timestamp,
        status = MessageStatus.valueOf(status)
    )

    private fun UserEntity.toDomain() = User(
        id = id,
        name = name,
        avatarUrl = avatarUrl,
        isOnline = isOnline,
        lastSeen = lastSeen
    )
}
