package com.intern002.locketapp.ui.screen.chat

import com.intern002.locketapp.data.remote.dto.ConversationListItemDTO

sealed class MessageStatus {
    object Unread : MessageStatus()
    object Sending : MessageStatus()
    object Sent : MessageStatus()
    data class Read(val readerAvatarUrl: String?, val readerName: String) : MessageStatus()
    object None : MessageStatus()
    object Failed : MessageStatus()
}

data class Conversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val avatarUrl: String,
    val lastMessageFromMe: Boolean,
    var status: MessageStatus
)

fun ConversationListItemDTO.toConversation(currentUserId: String): Conversation {
    val lastMsg = this.lastMessage
    val lastMessageFromMe = lastMsg?.senderId == currentUserId

    val status = if (lastMessageFromMe) {
        if (lastMsg?.isRead == true) {
            MessageStatus.Read(this.partner.avatarUrl, this.partner.username)
        } else {
            MessageStatus.Sent
        }
    } else {
        // The partner sent the last message
        if (this.unreadCount > 0) {
            MessageStatus.Unread
        } else {
            MessageStatus.None
        }
    }

    val lastMessageText = when (lastMsg?.messageType) {
        "text" -> {
            if (lastMessageFromMe) {
                "You: ${lastMsg.content}"
            } else {
                lastMsg.content
            }
        }
        "image" -> if (lastMessageFromMe) "You sent an image" else "Sent you an image"
        "sticker" -> if (lastMessageFromMe) "You sent a sticker" else "Sent you a sticker"
        else -> "No messages yet"
    }

    return Conversation(
        id = this.conversationId,
        name = this.partner.username,
        lastMessage = lastMessageText ?: "No messages yet",
        timestamp = lastMsg?.createdAt ?: this.createdAt,
        avatarUrl = this.partner.avatarUrl ?: "",
        lastMessageFromMe = lastMessageFromMe,
        status = status
    )
}
