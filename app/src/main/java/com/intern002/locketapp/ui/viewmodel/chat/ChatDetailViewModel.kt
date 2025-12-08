package com.intern002.locketapp.ui.viewmodel.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.model.Message
import com.intern002.locketapp.data.model.SendStatus
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
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
    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            currentUserId = userRepository.getCurrentUserProfile()?.id
            loadInitialMessages()
            subscribeToNewMessages()
        }
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
                    _messageState.value = MessageListState.Success(emptyList())
                }
                else -> {
                    _messageState.value = MessageListState.Success(emptyList())
                }
            }
        }
    }

    private fun subscribeToNewMessages() {
        chatRepository.subscribeToMessages(conversationId)
            .onEach { newMessageDto ->
                val newMessage = newMessageDto.toMessage()

                if (newMessage.senderId == currentUserId) {
                    val index = _currentMessages.indexOfFirst { it.sendStatus == SendStatus.SENDING }
                    if (index != -1) {
                        _currentMessages[index] = newMessage.copy(sendStatus = SendStatus.SENT)
                    } else {

                        if (_currentMessages.none { it.createdAt == newMessage.createdAt }) {
                            _currentMessages.add(newMessage)
                        }
                    }
                } else {
                    if (_currentMessages.none { it.createdAt == newMessage.createdAt }) {
                        _currentMessages.add(newMessage)
                    }
                }
                _messageState.value = MessageListState.Success(_currentMessages.toList())
            }
            .catch { e ->
                Log.e("RealtimeDebug", "[ERROR] ViewModel subscription failed: ", e)
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            if (currentUserId == null) return@launch

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val timestamp = sdf.format(Date())

            val tempMessage = Message(
                senderId = currentUserId!!,
                messageType = "text",
                content = text,
                imageUrl = null,
                createdAt = timestamp,
                localId = UUID.randomUUID().toString(),
                sendStatus = SendStatus.SENDING
            )

            _currentMessages.add(tempMessage)
            _messageState.value = MessageListState.Success(_currentMessages.toList())

            val request = SendMessageRequest(
                conversationId = conversationId,
                messageType = "text",
                content = text
            )

            val result = chatRepository.sendMessage(request)


            if (result is Result.Error) {
                val index = _currentMessages.indexOfFirst { it.localId == tempMessage.localId }
                if (index != -1) {
                    _currentMessages[index] = _currentMessages[index].copy(sendStatus = SendStatus.FAILED)
                    _messageState.value = MessageListState.Success(_currentMessages.toList())
                }
            }
        }
    }
}
