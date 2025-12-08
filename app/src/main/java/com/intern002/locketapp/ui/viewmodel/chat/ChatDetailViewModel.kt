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
import java.util.Calendar
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
            observeMessages()
            refreshMessages()
            subscribeToNewMessages()
            markConversationAsRead()
        }
    }

    private fun observeMessages() {
        chatRepository.getMessages(conversationId)
            .onEach { messagesFromDb ->
                val combinedList = (messagesFromDb + _currentMessages.filter { it.sendStatus == SendStatus.SENDING || it.sendStatus == SendStatus.FAILED })
                    .distinctBy { it.localId }
                    .sortedBy { it.createdAt }

                _messageState.value = MessageListState.Success(processMessagesWithTimestamps(combinedList))
            }
            .catch { e ->
                _messageState.value = MessageListState.Error(e.message ?: "Failed to load messages")
            }
            .launchIn(viewModelScope)
    }

    private fun processMessagesWithTimestamps(messages: List<Message>): List<Message> {
        if (messages.isEmpty()) return emptyList()

        fun parseDate(dateString: String): Date? {
            return try {
                val pattern = if (dateString.contains(".")) "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" else "yyyy-MM-dd'T'HH:mm:ss'Z'"
                val localParser = SimpleDateFormat(pattern, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                localParser.parse(dateString)
            } catch (e: Exception) {
                null
            }
        }

        return messages.mapIndexed { index, message ->
            if (index == 0) {
                message.copy(showTimestamp = true)
            } else {
                val prevMessage = messages[index - 1]
                val prevDate = parseDate(prevMessage.createdAt)
                val currentDate = parseDate(message.createdAt)

                if (prevDate != null && currentDate != null) {
                    val calPrev = Calendar.getInstance().apply { time = prevDate }
                    val calCurrent = Calendar.getInstance().apply { time = currentDate }

                    val isDifferentDay = calPrev.get(Calendar.DAY_OF_YEAR) != calCurrent.get(Calendar.DAY_OF_YEAR) ||
                            calPrev.get(Calendar.YEAR) != calCurrent.get(Calendar.YEAR)

                    val diffInMinutes = (currentDate.time - prevDate.time) / 60000
                    val timeDiffExceeded = diffInMinutes > 10

                    message.copy(showTimestamp = isDifferentDay || timeDiffExceeded)
                } else {
                    message.copy(showTimestamp = false)
                }
            }
        }
    }


    private fun refreshMessages() {
        viewModelScope.launch {
            chatRepository.refreshMessages(conversationId, 1, 50)
        }
    }

    private fun subscribeToNewMessages() {
        chatRepository.subscribeToMessages(conversationId)
            .onEach { newMessageDto ->
                val newMessage = newMessageDto.toMessage()
                if (newMessage.senderId == currentUserId) {
                    val index = _currentMessages.indexOfFirst { it.sendStatus == SendStatus.SENDING && it.content == newMessage.content }
                    if (index != -1) {
                        _currentMessages.removeAt(index)
                    }
                }
                chatRepository.saveNewMessage(newMessageDto, conversationId)

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
            val currentSuccessState = (_messageState.value as? MessageListState.Success)
            val updatedList = ((currentSuccessState?.messages ?: emptyList()) + tempMessage)
                .sortedBy { it.createdAt }
            _messageState.value = MessageListState.Success(processMessagesWithTimestamps(updatedList))


            val request = SendMessageRequest(
                conversationId = conversationId,
                messageType = "text",
                content = text
            )

            val result = chatRepository.sendMessage(request)

            if (result is Result.Error) {
                val index = _currentMessages.indexOfFirst { it.localId == tempMessage.localId }
                if (index != -1) {
                    val failedMessage = _currentMessages[index].copy(sendStatus = SendStatus.FAILED)
                    _currentMessages[index] = failedMessage

                    val currentMessagesList = (_messageState.value as? MessageListState.Success)?.messages ?: emptyList()
                    val finalList = currentMessagesList.map { if (it.localId == failedMessage.localId) failedMessage else it }
                    _messageState.value = MessageListState.Success(processMessagesWithTimestamps(finalList.sortedBy { it.createdAt }))
                }
            }
        }
    }

    private fun markConversationAsRead() {
        viewModelScope.launch {
            chatRepository.markConversationAsRead(conversationId)
        }
    }
}
