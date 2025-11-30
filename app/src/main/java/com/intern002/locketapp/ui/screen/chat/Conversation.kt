package com.intern002.locketapp.ui.screen.chat

import com.intern002.locketapp.data.remote.dto.ConversationListItemDTO
import com.intern002.locketapp.data.remote.dto.MessageDTO


data class Conversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val avatarUrl: String,
    val lastMessageFromMe: Boolean, 
    val isRead: Boolean
)

fun ConversationListItemDTO.toConversation(currentUserId: String): Conversation {
    val lastMsg = this.lastMessage
    val lastMessageFromMe = lastMsg?.senderId.toString() == currentUserId

    val lastMessageText = when (lastMsg?.messageType) {
        "text" -> lastMsg.content
        "image" -> if (lastMessageFromMe) "You sent an image" else "Sent you an image"
        "sticker" -> if (lastMessageFromMe) "You sent a sticker" else "Sent you a sticker"
        else -> "No messages yet"
    }

    val timestampText = lastMsg?.createdAt ?: this.createdAt

    return Conversation(
        id = this.conversationId.toString(),
        name = this.partner.username,
        lastMessage = lastMessageText ?: "No messages yet",
        timestamp = timestampText,
        avatarUrl = this.partner.avatarUrl ?: "",
        lastMessageFromMe = lastMessageFromMe,
        isRead = lastMessageFromMe
    )
}
