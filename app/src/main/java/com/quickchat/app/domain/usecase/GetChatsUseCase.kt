package com.quickchat.app.domain.usecase

import com.quickchat.app.data.repository.ChatRepository
import com.quickchat.app.domain.model.Chat
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<List<Chat>> = chatRepository.getChats()
}
