package com.quickchat.app.ui.screens.chat

import com.quickchat.app.domain.model.Message
import com.quickchat.app.domain.model.User

data class ChatState(
    val messages: List<Message> = emptyList(),
    val otherUser: User? = null,
    val inputText: String = "",
    val isTyping: Boolean = false,
    val isLoading: Boolean = true
)
