package com.intern002.locketapp.data.model

import com.intern002.locketapp.data.remote.dto.MessageDTO
import java.util.UUID

enum class SendStatus {
    SENDING,
    SENT,
    FAILED
}

data class Message(
    val id: String,
    val senderId: String,
    val messageType: String,
    val content: String?,
    val imageUrl: String?,
    val createdAt: String,
    val localId: String = UUID.randomUUID().toString(),
    var sendStatus: SendStatus = SendStatus.SENT,
    var showTimestamp: Boolean = false,
    var displayTimestamp: String = ""
)

fun MessageDTO.toMessage(): Message {
    return Message(
        id = this.id,
        senderId = this.senderId,
        messageType = this.messageType,
        content = this.content,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        sendStatus = SendStatus.SENT
    )
}
