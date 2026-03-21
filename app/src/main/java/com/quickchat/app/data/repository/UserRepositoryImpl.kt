package com.quickchat.app.data.repository

import com.quickchat.app.data.local.ChatDao
import com.quickchat.app.data.remote.ChatWebSocket
import com.quickchat.app.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val webSocket: ChatWebSocket
) : UserRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Persist online status changes from WebSocket into Room
        webSocket.onlineStatusEvents.onEach { event ->
            val lastSeen = if (event.isOnline) System.currentTimeMillis() else System.currentTimeMillis()
            chatDao.updateUserOnlineStatus(event.userId, event.isOnline, lastSeen)
        }.launchIn(scope)
    }

    override fun getCurrentUser(): Flow<User?> = flow {
        val entity = chatDao.getUserById(ChatRepositoryImpl.CURRENT_USER_ID)
        emit(entity?.let {
            User(it.id, it.name, it.avatarUrl, it.isOnline, it.lastSeen)
        })
    }

    override fun getUser(userId: String): Flow<User?> = flow {
        val entity = chatDao.getUserById(userId)
        emit(entity?.let {
            User(it.id, it.name, it.avatarUrl, it.isOnline, it.lastSeen)
        })
    }

    override fun getAllUsers(): Flow<List<User>> {
        return chatDao.getAllUsers().map { entities ->
            entities.map { User(it.id, it.name, it.avatarUrl, it.isOnline, it.lastSeen) }
        }
    }

    override suspend fun updateOnlineStatus(userId: String, isOnline: Boolean) {
        chatDao.updateUserOnlineStatus(userId, isOnline, System.currentTimeMillis())
    }
}
