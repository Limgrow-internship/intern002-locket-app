package com.intern002.locketapp.ui.viewmodel.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.model.Message
import com.intern002.locketapp.data.model.toMessage
import com.intern002.locketapp.data.remote.api.SendMessageRequest
import com.intern002.locketapp.data.repository.ChatRepository
import com.intern002.locketapp.data.repository.UserRepository
import com.intern002.locketapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MessageListState {
    object Loading : MessageListState()
    data class Success(val messages: List<Message>) : MessageListState()
    data class Error(val message: String) : MessageListState()
}

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: String = savedStateHandle.get<String>("conversationId")!!

    private val _messageState = MutableStateFlow<MessageListState>(MessageListState.Loading)
    val messageState: StateFlow<MessageListState> = _messageState.asStateFlow()

    private val _currentMessages = mutableListOf<Message>()

    init {
        loadInitialMessages()
        subscribeToNewMessages()
    }

    private fun loadInitialMessages() {
        viewModelScope.launch {
            _messageState.value = MessageListState.Loading
            when (val result = chatRepository.getMessages(conversationId, 1, 50)) {
                is Result.Success -> {
                    val initialMessages = result.data?.map { it.toMessage() }?.reversed() ?: emptyList()
                    _currentMessages.addAll(initialMessages)
                    _messageState.value = MessageListState.Success(_currentMessages.toList())
                }
                is Result.Error -> {
                    _messageState.value = MessageListState.Error(result.message ?: "Failed to load messages")
                }
                else -> {}
            }
        }
    }

    private fun subscribeToNewMessages() {
        chatRepository.subscribeToMessages(conversationId)
            .onEach { newMessageDto ->
                Log.d("RealtimeDebug", "[3] ViewModel received DTO: $newMessageDto")
                val newMessage = newMessageDto.toMessage()
                _currentMessages.add(newMessage)
                _messageState.value = MessageListState.Success(_currentMessages.toList())
                Log.d("RealtimeDebug", "[4] ViewModel updated UI with new message list")
            }
            .catch { e ->
                Log.e("RealtimeDebug", "[ERROR] ViewModel subscription failed: ", e)
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val request = SendMessageRequest(
                conversationId = conversationId,
                messageType = "text",
                content = text
            )
            chatRepository.sendMessage(request)
            // No need to handle the result here, the realtime subscription will update the UI.
        }
    }
}
