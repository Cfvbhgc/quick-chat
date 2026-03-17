package com.quickchat.app.domain.usecase

import com.quickchat.app.data.repository.ChatRepository
import javax.inject.Inject

class MarkAsReadUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatId: String) {
        chatRepository.markAsRead(chatId)
    }
}
