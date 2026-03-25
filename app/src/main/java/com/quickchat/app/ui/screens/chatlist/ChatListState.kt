package com.quickchat.app.ui.screens.chatlist

import com.quickchat.app.domain.model.Chat

data class ChatListState(
    val chats: List<Chat> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
) {
    val filteredChats: List<Chat>
        get() = if (searchQuery.isBlank()) {
            chats
        } else {
            chats.filter { chat ->
                chat.participants.any { user ->
                    user.name.contains(searchQuery, ignoreCase = true)
                } || chat.lastMessage?.text?.contains(searchQuery, ignoreCase = true) == true
            }
        }
}
