package com.intern002.locketapp.data.repository

import android.util.Log
import com.intern002.locketapp.data.remote.api.ChatApi
import com.intern002.locketapp.data.remote.api.SendMessageRequest
import com.intern002.locketapp.data.remote.dto.* // Import all DTOs including the new one
import com.intern002.locketapp.utils.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import javax.inject.Inject

interface ChatRepository {
    suspend fun getConversations(): Result<List<ConversationListItemDTO>>
    suspend fun getMessages(conversationId: String, page: Int, pageSize: Int): Result<List<MessageDTO>>
    suspend fun sendMessage(request: SendMessageRequest): Result<MessageDTO>
    fun subscribeToMessages(conversationId: String): Flow<MessageDTO>
}

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val supabaseClient: SupabaseClient
) : ChatRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getConversations(): Result<List<ConversationListItemDTO>> {
        return try {
            val conversations = chatApi.getConversations()
            Result.Success(conversations)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getMessages(conversationId: String, page: Int, pageSize: Int): Result<List<MessageDTO>> {
        return try {
            val messages = chatApi.getMessages(conversationId, page, pageSize)
            Result.Success(messages)
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
}
