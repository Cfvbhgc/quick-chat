package com.quickchat.app.data.repository

import com.quickchat.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    fun getUser(userId: String): Flow<User?>
    fun getAllUsers(): Flow<List<User>>
    suspend fun updateOnlineStatus(userId: String, isOnline: Boolean)
}
