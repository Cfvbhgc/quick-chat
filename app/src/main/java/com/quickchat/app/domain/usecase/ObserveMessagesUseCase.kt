package com.quickchat.app.domain.usecase

import com.quickchat.app.data.repository.ChatRepository
import com.quickchat.app.domain.model.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatId: String): Flow<List<Message>> =
        chatRepository.getMessages(chatId)
}
