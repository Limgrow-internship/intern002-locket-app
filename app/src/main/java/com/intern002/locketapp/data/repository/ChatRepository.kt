package com.intern002.locketapp.data.repository

import android.util.Log
import com.intern002.locketapp.data.local.dao.ConversationDao
import com.intern002.locketapp.data.local.dao.MessageDao
import com.intern002.locketapp.data.local.entity.toEntity
import com.intern002.locketapp.data.local.entity.toUiModel
import com.intern002.locketapp.data.model.Message
import com.intern002.locketapp.data.remote.api.ChatApi
import com.intern002.locketapp.data.remote.api.SendMessageRequest
import com.intern002.locketapp.data.remote.dto.ConversationListItemDTO
import com.intern002.locketapp.data.remote.dto.MessageDTO
import com.intern002.locketapp.data.remote.dto.RealtimeMessageDTO
import com.intern002.locketapp.data.remote.dto.toMessageDTO
import com.intern002.locketapp.ui.screen.chat.Conversation
import com.intern002.locketapp.utils.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import javax.inject.Inject

interface ChatRepository {
    fun getConversations(currentUserId: String): Flow<List<Conversation>>
    suspend fun refreshConversations(currentUserId: String): Result<Unit>
    suspend fun clearConversations()
    fun getMessages(conversationId: String): Flow<List<Message>>
    suspend fun refreshMessages(conversationId: String, page: Int, pageSize: Int): Result<Unit>
    suspend fun sendMessage(request: SendMessageRequest): Result<MessageDTO>
    fun subscribeToMessages(conversationId: String): Flow<MessageDTO>
    suspend fun saveNewMessage(message: MessageDTO, conversationId: String)
    suspend fun markConversationAsRead(conversationId: String)
    suspend fun deleteConversationByPartnerId(partnerId: String)
    suspend fun clearAllLocalData()
    fun subscribeToConversationUpdates(): Flow<ConversationListItemDTO>
    suspend fun saveConversation(conversation: ConversationListItemDTO)
    suspend fun getPartnerIdByConversationId(conversationId: String): String?
    suspend fun updateConversationWithNewMessage(conversationId: String, message: MessageDTO)
}

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val supabaseClient: SupabaseClient,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ChatRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getConversations(currentUserId: String): Flow<List<Conversation>> {
        return conversationDao.getConversations().map { entities ->
            entities.map { it.toUiModel(currentUserId) }
        }
    }

    override suspend fun refreshConversations(currentUserId: String): Result<Unit> {
        return try {
            val remoteConversations = chatApi.getConversations()
            conversationDao.insertOrUpdateConversations(remoteConversations.map { it.toEntity() })
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Failed to refresh conversations: ${e.message}")
            Result.Error("Failed to refresh conversations. Please check your network.")
        }
    }

    override suspend fun clearConversations() {
        conversationDao.clearAll()
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessages(conversationId).map { entities ->
            entities.map { it.toUiModel() }
        }
    }

    override suspend fun refreshMessages(conversationId: String, page: Int, pageSize: Int): Result<Unit> {
        return try {
            val messages = chatApi.getMessages(conversationId, page, pageSize)
            messageDao.insertOrUpdateMessages(messages.map { it.toEntity(conversationId) })
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun sendMessage(request: SendMessageRequest): Result<MessageDTO> {
        return try {
            val sentMessage = chatApi.sendMessage(request)
            Result.Success(sentMessage)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    override fun subscribeToMessages(conversationId: String): Flow<MessageDTO> {
        val channel = supabaseClient.channel("messages:${conversationId}")
        return channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "messages"
            filter = "conversation_id=eq.$conversationId"
        }.map { action ->
            Log.d("RealtimeDebug", "[1] Repo received raw payload: ${action.record}")
            val realtimeMessage = json.decodeFromJsonElement<RealtimeMessageDTO>(action.record)
            Log.d("RealtimeDebug", "[2] Repo successfully decoded: $realtimeMessage")
            realtimeMessage.toMessageDTO()
        }.catch { e ->
            Log.e("RealtimeDebug", "[ERROR] Repo decoding/mapping failed: ", e)
        }.onStart {
            Log.d("RealtimeDebug", "Subscribing to channel for conversation $conversationId")
            channel.subscribe()
        }.onCompletion {
            Log.d("RealtimeDebug", "Unsubscribing from channel for conversation $conversationId")
            channel.unsubscribe()
        }
    }

    override suspend fun saveNewMessage(message: MessageDTO, conversationId: String) {
        try {
            messageDao.insertOrUpdateMessages(listOf(message.toEntity(conversationId)))
        } catch (e: Exception) {
            Log.e("ChatRepository", "Failed to save new message: ${e.message}")
        }
    }

    override suspend fun markConversationAsRead(conversationId: String) {
        try {
            chatApi.markConversationAsRead(conversationId)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Failed to mark conversation as read: ${e.message}")
        }
    }

    override suspend fun deleteConversationByPartnerId(partnerId: String) {
        conversationDao.deleteConversationByPartnerId(partnerId)
    }

    override suspend fun clearAllLocalData() {
        conversationDao.clearAll()
        messageDao.clearAll()
    }

    override fun subscribeToConversationUpdates(): Flow<ConversationListItemDTO> {
        val channel = supabaseClient.channel("conversations-updates")
        return channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "conversations"
        }.map {
            Log.d("ChatRepository", "Conversation update received: ${it.record}")
            json.decodeFromJsonElement<ConversationListItemDTO>(it.record)
        }.catch { e ->
            Log.e("ChatRepository", "Error subscribing to conversation updates: ${e.message}")
        }.onStart {
            Log.d("ChatRepository", "Subscribing to conversation updates")
            channel.subscribe()
        }.onCompletion {
            Log.d("ChatRepository", "Unsubscribing from conversation updates")
            channel.unsubscribe()
        }
    }

    override suspend fun saveConversation(conversation: ConversationListItemDTO) {
        try {
            conversationDao.insertOrUpdateConversations(listOf(conversation.toEntity()))
        } catch (e: Exception) {
            Log.e("ChatRepository", "Failed to save conversation: ${e.message}")
        }
    }

    override suspend fun getPartnerIdByConversationId(conversationId: String): String? {
        return conversationDao.getPartnerIdByConversationId(conversationId)
    }

    override suspend fun updateConversationWithNewMessage(conversationId: String, message: MessageDTO) {
        val conversation = conversationDao.getConversationById(conversationId)
        if (conversation != null) {
            val updatedConversation = conversation.copy(
                lastMessageText = message.content,
                lastMessageType = message.messageType,
                lastMessageTimestamp = message.createdAt,
                lastMessageSenderId = message.senderId,
                lastMessageIsRead = message.isRead,
                unreadCount = if (message.senderId == conversation.partnerId) conversation.unreadCount + 1 else 0
            )
            conversationDao.insertOrUpdateConversations(listOf(updatedConversation))
        }
    }
}
