package com.quickchat.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String?,
    val isOnline: Boolean,
    val lastSeen: Long
)
