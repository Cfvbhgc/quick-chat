package com.quickchat.app.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickchat.app.data.notification.MockFcmService
import com.quickchat.app.data.repository.ChatRepository
import com.quickchat.app.data.repository.ChatRepositoryImpl
import com.quickchat.app.data.repository.UserRepository
import com.quickchat.app.domain.usecase.MarkAsReadUseCase
import com.quickchat.app.domain.usecase.ObserveMessagesUseCase
import com.quickchat.app.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markAsReadUseCase: MarkAsReadUseCase,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val mockFcmService: MockFcmService
) : ViewModel() {

    private val chatId: String = savedStateHandle["chatId"] ?: ""

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        // Tell the FCM mock we're viewing this chat so it won't spam notifications
        mockFcmService.activeChatId = chatId
        observeMessages()
        observeTyping()
        loadOtherUser()
        markAsRead()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            observeMessagesUseCase(chatId).collect { messages ->
                _state.update { it.copy(messages = messages, isLoading = false) }
            }
        }
    }

    private fun observeTyping() {
        viewModelScope.launch {
            chatRepository.getTypingEvents().collect { (eventChatId, isTyping) ->
                if (eventChatId == chatId) {
                    _state.update { it.copy(isTyping = isTyping) }
                }
            }
        }
    }

    private fun loadOtherUser() {
        viewModelScope.launch {
            // Determine the other participant from the first message or chat metadata
            val participantMap = mapOf(
                "chat_1" to "user_2",
                "chat_2" to "user_3",
                "chat_3" to "user_4",
                "chat_4" to "user_5",
                "chat_5" to "user_6"
            )
            val otherUserId = participantMap[chatId] ?: return@launch
            userRepository.getUser(otherUserId).collect { user ->
                _state.update { it.copy(otherUser = user) }
            }
        }
    }

    private fun markAsRead() {
        viewModelScope.launch {
            markAsReadUseCase(chatId)
        }
    }

    fun onInputChange(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isBlank()) return

        _state.update { it.copy(inputText = "") }
        viewModelScope.launch {
            sendMessageUseCase(chatId, text)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mockFcmService.activeChatId = null
    }
}
