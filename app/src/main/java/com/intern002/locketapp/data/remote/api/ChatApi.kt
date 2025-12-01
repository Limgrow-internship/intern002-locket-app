package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.remote.dto.ConversationListItemDTO
import com.intern002.locketapp.data.remote.dto.MessageDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import javax.inject.Inject

@OptIn(InternalSerializationApi::class)
@Serializable
data class SendMessageRequest(
    val conversationId: String,
    val messageType: String,
    val content: String? = null,
    val imageUrl: String? = null
)

@OptIn(InternalSerializationApi::class)
class ChatApi @Inject constructor(private val client: HttpClient) {
    private val baseUrl = BuildConfig.BASE_URL

    suspend fun getConversations(): List<ConversationListItemDTO> {
        return client.get("$baseUrl/chat/conversations").body()
    }

    suspend fun getMessages(conversationId: String, page: Int, pageSize: Int): List<MessageDTO> {
        return client.get("$baseUrl/chat/messages/$conversationId") {
            url {
                parameters.append("page", page.toString())
                parameters.append("pageSize", pageSize.toString())
            }
        }.body()
    }

    suspend fun sendMessage(request: SendMessageRequest): MessageDTO {
        return client.post("$baseUrl/chat/messages") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
