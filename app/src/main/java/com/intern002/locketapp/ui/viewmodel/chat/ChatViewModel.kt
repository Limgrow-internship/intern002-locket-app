package com.intern002.locketapp.ui.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.ChatRepository
import com.intern002.locketapp.data.repository.UserRepository
import com.intern002.locketapp.ui.screen.chat.Conversation
import com.intern002.locketapp.ui.screen.chat.toConversation
import com.intern002.locketapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun getConversations() {
        viewModelScope.launch {
            _chatListState.value = ChatListState.Loading
            val userResult = userRepository.getCurrentUserProfile()
            if (userResult == null) {
                _chatListState.value = ChatListState.Error("User not logged in")
                return@launch
            }
            when (val result = chatRepository.getConversations()) {
                is Result.Success -> {
                    val conversations = result.data?.map { it.toConversation(userResult.id) } ?: emptyList()
                    _chatListState.value = ChatListState.Success(conversations)
                }
                is Result.Error -> {
                    _chatListState.value = ChatListState.Error(result.message ?: "An unknown error occurred")
                }
                else -> {}
            }
        }
    }
}
