package com.intern002.locketapp.ui.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.ChatRepository
import com.intern002.locketapp.data.repository.UserRepository
import com.intern002.locketapp.ui.screen.chat.Conversation
import com.intern002.locketapp.utils.Result
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
    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            currentUserId = userRepository.getCurrentUserProfile()?.id
            loadConversations()
            subscribeToConversationUpdates()
        }
    }

    private fun loadConversations() {
        val userId = currentUserId
        if (userId == null) {
            _chatListState.value = ChatListState.Error("User not logged in")
            return
        }

        chatRepository.getConversations(userId)
            .onEach { conversations ->
                _chatListState.value = ChatListState.Success(conversations)
            }
            .catch { e ->
                _chatListState.value = ChatListState.Error(e.message ?: "Failed to load from local cache.")
            }
            .launchIn(viewModelScope)

        onRefresh()
    }

    fun onRefresh() {
        val userId = currentUserId
        if (userId == null) {
            return
        }

        viewModelScope.launch {
            val result = chatRepository.refreshConversations(userId)
            if (result is Result.Error) {
                if (_chatListState.value !is ChatListState.Success) {
                    _chatListState.value = ChatListState.Error(result.message ?: "An unknown error occurred")
                }
            }
        }
    }

    private fun subscribeToConversationUpdates() {
        chatRepository.subscribeToConversationUpdates()
            .onEach { updatedConversation ->
                chatRepository.saveConversation(updatedConversation)
            }
            .catch { e ->
            }
            .launchIn(viewModelScope)
    }
}
