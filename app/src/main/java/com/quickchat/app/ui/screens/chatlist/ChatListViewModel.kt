package com.quickchat.app.ui.screens.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickchat.app.data.notification.MockFcmService
import com.quickchat.app.data.remote.ChatWebSocket
import com.quickchat.app.domain.usecase.GetChatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getChatsUseCase: GetChatsUseCase,
    private val webSocket: ChatWebSocket,
    private val mockFcmService: MockFcmService
) : ViewModel() {

    private val _state = MutableStateFlow(ChatListState())
    val state: StateFlow<ChatListState> = _state.asStateFlow()

    init {
        // Connect WebSocket and start FCM simulation on app launch
        webSocket.connect()
        mockFcmService.start()
        observeChats()
    }

    private fun observeChats() {
        viewModelScope.launch {
            getChatsUseCase().collect { chats ->
                _state.update { it.copy(chats = chats, isLoading = false) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
}
