package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.api.ChatApi
import com.intern002.locketapp.data.remote.dto.ConversationListItemDTO
import com.intern002.locketapp.utils.Result
import javax.inject.Inject

interface ChatRepository {
    suspend fun getConversations(): Result<List<ConversationListItemDTO>>
}

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : ChatRepository {
    override suspend fun getConversations(): Result<List<ConversationListItemDTO>> {
        return try {
            val conversations = chatApi.getConversations()
            Result.Success(conversations)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }
}
