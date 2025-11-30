package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.remote.dto.ConversationListItemDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class ChatApi @Inject constructor(private val client: HttpClient) {
    private val baseUrl = BuildConfig.BASE_URL

    suspend fun getConversations(): List<ConversationListItemDTO> {
        return client.get("$baseUrl/chat/conversations").body()
    }
}
