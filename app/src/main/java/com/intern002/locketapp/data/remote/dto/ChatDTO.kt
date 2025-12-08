package com.intern002.locketapp.data.remote.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class ConversationPartnerDTO(
    val id: String,
    val username: String,
    val avatarUrl: String? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class MessageDTO(
    val conversationId: String? = null,
    val senderId: String,
    val messageType: String,
    val content: String?,
    val imageUrl: String?,
    val createdAt: String,
    val isRead: Boolean = false
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class ConversationListItemDTO(
    val conversationId: String,
    val partner: ConversationPartnerDTO,
    val lastMessage: MessageDTO?,
    val unreadCount: Int = 0,
    val createdAt: String
)
