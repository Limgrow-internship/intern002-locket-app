package com.intern002.locketapp.data.model

import com.intern002.locketapp.data.remote.dto.MessageDTO

data class Message(
    val senderId: String,
    val messageType: String,
    val content: String?,
    val imageUrl: String?,
    val createdAt: String
)

fun MessageDTO.toMessage(): Message {
    return Message(
        senderId = this.senderId,
        messageType = this.messageType,
        content = this.content,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt
    )
}
