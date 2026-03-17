package com.quickchat.app.domain.model

data class User(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val isOnline: Boolean,
    val lastSeen: Long
)
