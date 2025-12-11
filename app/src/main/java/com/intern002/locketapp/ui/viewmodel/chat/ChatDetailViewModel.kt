package com.intern002.locketapp.ui.viewmodel.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.model.Message
import com.intern002.locketapp.data.model.SendStatus
import com.intern002.locketapp.data.remote.api.SendMessageRequest
import com.intern002.locketapp.data.repository.ChatRepository
import com.intern002.locketapp.data.repository.FriendshipRepository
import com.intern002.locketapp.data.repository.UserRepository
import com.intern002.locketapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.TimeUnit
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
    private val friendshipRepository: FriendshipRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: String = savedStateHandle.get<String>("conversationId")!!

    private val _messageState = MutableStateFlow<MessageListState>(MessageListState.Loading)
    val messageState: StateFlow<MessageListState> = _messageState.asStateFlow()

    private val _temporaryMessagesFlow = MutableStateFlow<List<Message>>(emptyList())
    private var currentUserId: String? = null

    private val _removeFriendEvent = Channel<Unit>()
    val removeFriendEvent = _removeFriendEvent.receiveAsFlow()

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
            .combine(_temporaryMessagesFlow) { dbMessages, tempMessages ->
                (dbMessages + tempMessages)
                    .distinctBy { it.localId }
                    .sortedBy { it.createdAt }
            }
            .onEach { combinedList ->
                _messageState.value = MessageListState.Success(processMessagesWithTimestamps(combinedList))
            }
            .catch { e ->
                _messageState.value = MessageListState.Error(e.message ?: "Failed to load messages")
            }
            .launchIn(viewModelScope)
    }

    private fun parseIsoString(isoString: String): Date? {
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
        )
        for (pattern in patterns) {
            try {
                val parser = SimpleDateFormat(pattern, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                return parser.parse(isoString)
            } catch (e: ParseException) {
            }
        }
        return null
    }

    private fun processMessagesWithTimestamps(messages: List<Message>): List<Message> {
        if (messages.isEmpty()) return emptyList()

        val displayFormatter = SimpleDateFormat("EEE h:mm a", Locale.US)

        return messages.mapIndexed { index, message ->
            val utcDate = parseIsoString(message.createdAt)

            var displayString = ""
            var shouldShowTimestamp = false

            if (utcDate != null) {
                val localDate = Date(utcDate.time + TimeUnit.HOURS.toMillis(7))

                val prevUtcDate = if (index > 0) parseIsoString(messages[index - 1].createdAt) else null

                if (index == 0) {
                    shouldShowTimestamp = true
                } else if (prevUtcDate != null) {
                    val prevLocalDate = Date(prevUtcDate.time + TimeUnit.HOURS.toMillis(7))

                    val calPrev = Calendar.getInstance().apply { time = prevLocalDate }
                    val calCurrent = Calendar.getInstance().apply { time = localDate }

                    val isDifferentDay = calPrev.get(Calendar.DAY_OF_YEAR) != calCurrent.get(Calendar.DAY_OF_YEAR) ||
                            calPrev.get(Calendar.YEAR) != calCurrent.get(Calendar.YEAR)

                    val diffInMinutes = (utcDate.time - prevUtcDate.time) / 60000
                    val timeDiffExceeded = diffInMinutes > 10

                    shouldShowTimestamp = isDifferentDay || timeDiffExceeded
                }

                if (shouldShowTimestamp) {
                    displayString = displayFormatter.format(localDate)
                }
            }

            message.copy(
                showTimestamp = shouldShowTimestamp,
                displayTimestamp = displayString
            )
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
                if (newMessageDto.senderId != currentUserId) {
                    chatRepository.saveNewMessage(newMessageDto, conversationId)
                    chatRepository.updateConversationWithNewMessage(conversationId, newMessageDto)
                }
            }
            .catch { e ->
                Log.e("RealtimeDebug", "[ERROR] ViewModel subscription failed: ", e)
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val userId = currentUserId ?: return@launch

            val utcTimestampString = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            val tempMessage = Message(
                senderId = userId,
                messageType = "text",
                content = text,
                imageUrl = null,
                createdAt = utcTimestampString,
                localId = UUID.randomUUID().toString(),
                sendStatus = SendStatus.SENDING
            )

            _temporaryMessagesFlow.value = _temporaryMessagesFlow.value + tempMessage

            val request = SendMessageRequest(
                conversationId = conversationId,
                messageType = "text",
                content = text
            )
            val result = chatRepository.sendMessage(request)

            when (result) {
                is Result.Success -> {
                    _temporaryMessagesFlow.value = _temporaryMessagesFlow.value.filterNot { it.localId == tempMessage.localId }
                    result.data?.let { sentMessage ->
                        chatRepository.saveNewMessage(sentMessage, conversationId)
                        chatRepository.updateConversationWithNewMessage(conversationId, sentMessage)
                    }
                }
                is Result.Error -> {
                    val currentTemps = _temporaryMessagesFlow.value
                    val index = currentTemps.indexOfFirst { it.localId == tempMessage.localId }
                    if (index != -1) {
                        val updatedList = currentTemps.toMutableList()
                        updatedList[index] = updatedList[index].copy(sendStatus = SendStatus.FAILED)
                        _temporaryMessagesFlow.value = updatedList
                    }
                }
                else -> {
                    Log.d("SendMessage", "Received unexpected result state: $result")
                }
            }
        }
    }

    private fun markConversationAsRead() {
        viewModelScope.launch {
            chatRepository.markConversationAsRead(conversationId)
        }
    }

    fun removeFriend() {
        viewModelScope.launch {
            val partnerId = chatRepository.getPartnerIdByConversationId(conversationId)
            if (partnerId != null) {
                try {
                    friendshipRepository.deleteFriendship(partnerId)
                    chatRepository.deleteConversationByPartnerId(partnerId)
                    _removeFriendEvent.send(Unit)
                } catch (e: Exception) {
                    // Handle error
                }
            } else {
                // Handle error
            }
        }
    }
}
