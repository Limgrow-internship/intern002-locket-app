package com.intern002.locketapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.intern002.locketapp.data.model.Message
import com.intern002.locketapp.data.remote.dto.MessageDTO

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val senderId: String,
    val messageType: String,
    val content: String?,
    val imageUrl: String?,
    val createdAt: String,
    val isRead: Boolean
)

fun MessageDTO.toEntity(conversationIdForDb: String): MessageEntity {
    return MessageEntity(
        id = this.id,
        conversationId = conversationIdForDb,
        senderId = this.senderId,
        messageType = this.messageType,
        content = this.content,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        isRead = this.isRead
    )
}

fun MessageEntity.toUiModel(): Message {
    return Message(
        id = this.id,
        senderId = this.senderId,
        messageType = this.messageType,
        content = this.content,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt
    )
}
