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
    val lastMessageTimestamp: String?,
    val lastMessageIsFromMe: Boolean,
    val lastMessageIsRead: Boolean,
    val unreadCount: Int,
)


fun ConversationListItemDTO.toEntity(): ConversationEntity {
    return ConversationEntity(
        id = this.conversationId,
        partnerId = this.partner.id,
        partnerName = this.partner.username,
        partnerAvatarUrl = this.partner.avatarUrl,
        lastMessageText = this.lastMessage?.content,
        lastMessageTimestamp = this.lastMessage?.createdAt ?: this.createdAt,
        lastMessageIsFromMe = false,
        lastMessageIsRead = this.lastMessage?.isRead ?: false,
        unreadCount = this.unreadCount
    )
}

fun ConversationEntity.toUiModel(currentUserId: String): Conversation {
    val lastMessageFromMe = this.lastMessageIsFromMe

    val status = if (lastMessageFromMe) {
        if (this.lastMessageIsRead) {
            MessageStatus.Read(this.partnerAvatarUrl, this.partnerName)
        } else {
            MessageStatus.Sent
        }
    } else {
        if (this.unreadCount > 0) MessageStatus.Unread else MessageStatus.None
    }

    val displayText = when {
        lastMessageFromMe -> "You: ${this.lastMessageText ?: ""}"
        else -> this.lastMessageText ?: "No messages yet"
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
