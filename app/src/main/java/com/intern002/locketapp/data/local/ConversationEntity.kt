package com.intern002.locketapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.intern002.locketapp.data.remote.dto.ConversationListItemDTO
import com.intern002.locketapp.ui.screen.chat.Conversation
import com.intern002.locketapp.ui.screen.chat.MessageStatus

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    val partnerId: String,
    val partnerName: String,
    val partnerAvatarUrl: String?,
    val lastMessageText: String?,
    val lastMessageType: String?,
    val lastMessageTimestamp: String?,
    val lastMessageSenderId: String?,
    val lastMessageIsRead: Boolean,
    val unreadCount: Int,
)


fun ConversationListItemDTO.toEntity(): ConversationEntity {
    val lastMsg = this.lastMessage
    return ConversationEntity(
        id = this.conversationId,
        partnerId = this.partner.id,
        partnerName = this.partner.username,
        partnerAvatarUrl = this.partner.avatarUrl,
        lastMessageText = lastMsg?.content,
        lastMessageType = lastMsg?.messageType,
        lastMessageTimestamp = lastMsg?.createdAt ?: this.createdAt,
        lastMessageSenderId = lastMsg?.senderId,
        lastMessageIsRead = lastMsg?.isRead ?: false,
        unreadCount = this.unreadCount
    )
}

fun ConversationEntity.toUiModel(currentUserId: String): Conversation {
    val lastMessageFromMe = this.lastMessageSenderId == currentUserId

    val status = if (lastMessageFromMe) {
        if (this.lastMessageIsRead) {
            MessageStatus.Read(this.partnerAvatarUrl, this.partnerName)
        } else {
            MessageStatus.Sent
        }
    } else {
        if (this.unreadCount > 0) MessageStatus.Unread else MessageStatus.None
    }

    val displayText = when (this.lastMessageType) {
        "text" -> if (lastMessageFromMe) "You: ${this.lastMessageText ?: ""}" else this.lastMessageText ?: ""
        "image" -> if (lastMessageFromMe) "You sent an image" else "Sent you an image"
        "sticker" -> if (lastMessageFromMe) "You sent a sticker" else "Sent you a sticker"
        else -> if(this.lastMessageTimestamp != null) "No messages yet" else ""
    }

    return Conversation(
        id = this.id,
        name = this.partnerName,
        lastMessage = displayText,
        timestamp = this.lastMessageTimestamp ?: "",
        avatarUrl = this.partnerAvatarUrl ?: "",
        lastMessageFromMe = lastMessageFromMe,
        status = status
    )
}
