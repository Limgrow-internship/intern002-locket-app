package com.intern002.locketapp.data.remote.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class RealtimeMessageDTO(
    @SerialName("conversation_id")
    val conversationId: String,
    @SerialName("sender_id")
    val senderId: String,
    @SerialName("message_type")
    val messageType: String,
    @SerialName("content")
    val content: String?,
    @SerialName("image_url")
    val imageUrl: String?,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("is_read")
    val isRead: Boolean
)

fun RealtimeMessageDTO.toMessageDTO(): MessageDTO {
    return MessageDTO(
        conversationId = this.conversationId,
        senderId = this.senderId,
        messageType = this.messageType,
        content = this.content,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        isRead = this.isRead
    )
}
