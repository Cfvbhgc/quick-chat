package com.quickchat.app.domain.model

data class Chat(
    val id: String,
    val participants: List<User>,
    val lastMessage: Message?,
    val unreadCount: Int,
    val updatedAt: Long
)
