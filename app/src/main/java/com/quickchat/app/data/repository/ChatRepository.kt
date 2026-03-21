package com.quickchat.app.data.repository

import com.quickchat.app.domain.model.Chat
import com.quickchat.app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun sendMessage(chatId: String, text: String)
    suspend fun markAsRead(chatId: String)
    fun getTypingEvents(): Flow<Pair<String, Boolean>> // chatId to isTyping
}
