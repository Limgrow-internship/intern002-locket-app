package com.intern002.locketapp.data.remote.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This DTO is specifically for handling the snake_case JSON payload 
 * that comes directly from the Supabase Realtime subscription.
 */
@OptIn(InternalSerializationApi::class)
@Serializable
data class RealtimeMessageDTO(
    @SerialName("id")
    val id: String, // FIX: The ID from the database is a UUID String, not a Long
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
    val createdAt: String
)

/**
 * Maps a RealtimeMessageDTO (from Supabase) to the standard MessageDTO 
 * used throughout the Ktor client and the app.
 */
fun RealtimeMessageDTO.toMessageDTO(): MessageDTO {
    return MessageDTO(
        conversationId = this.conversationId,
        senderId = this.senderId,
        messageType = this.messageType,
        content = this.content,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt
    )
}
