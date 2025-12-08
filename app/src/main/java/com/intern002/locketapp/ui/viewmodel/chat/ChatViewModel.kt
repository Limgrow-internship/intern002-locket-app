package com.intern002.locketapp.ui.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.ChatRepository
import com.intern002.locketapp.data.repository.UserRepository
import com.intern002.locketapp.ui.screen.chat.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatListState {
    object Loading : ChatListState()
    data class Success(val conversations: List<Conversation>) : ChatListState()
    data class Error(val message: String) : ChatListState()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _chatListState = MutableStateFlow<ChatListState>(ChatListState.Loading)
    val chatListState: StateFlow<ChatListState> = _chatListState

    init {
        getConversations()
    }

    private fun getConversations() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUserProfile()
            if (user == null) {
                _chatListState.value = ChatListState.Error("User not found")
                return@launch
            }

            chatRepository.getConversations(user.id)
                .onEach { conversations ->
                    _chatListState.value = ChatListState.Success(conversations)
                }
                .catch { e ->
                    _chatListState.value = ChatListState.Error(e.message ?: "An unknown error occurred")
                }
                .launchIn(viewModelScope)

            chatRepository.refreshConversations()
        }
    }
    
    fun onRefresh() {
        viewModelScope.launch {
            chatRepository.refreshConversations()
        }
    }
}
