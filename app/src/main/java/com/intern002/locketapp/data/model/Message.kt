package com.intern002.locketapp.data.model

import com.intern002.locketapp.data.remote.dto.MessageDTO
import java.util.UUID

enum class SendStatus {
    SENDING,
    SENT,
    FAILED
}

data class Message(
    val senderId: String,
    val messageType: String,
    val content: String?,
    val imageUrl: String?,
    val createdAt: String, // The original ISO string from server/client
    val localId: String = UUID.randomUUID().toString(),
    var sendStatus: SendStatus = SendStatus.SENT,
    var showTimestamp: Boolean = false,
    var displayTimestamp: String = "" // New field for the formatted string
)

fun MessageDTO.toMessage(): Message {
    return Message(
        senderId = this.senderId,
        messageType = this.messageType,
        content = this.content,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        sendStatus = SendStatus.SENT
    )
}
