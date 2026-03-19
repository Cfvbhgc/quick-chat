package com.quickchat.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val participantIds: String, // Comma-separated IDs; simple approach for mock data
    val unreadCount: Int,
    val updatedAt: Long
)
