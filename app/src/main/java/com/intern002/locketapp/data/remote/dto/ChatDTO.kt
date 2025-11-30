package com.intern002.locketapp.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ConversationPartnerDTO(
    val id: String,
    val username: String,
    val avatarUrl: String?
)

@Serializable
data class MessageDTO(
    val senderId: String,
    val messageType: String,
    val content: String?,
    val imageUrl: String?,
    val createdAt: Instant
)

@Serializable
data class ConversationListItemDTO(
    val conversationId: String,
    val partner: ConversationPartnerDTO,
    val lastMessage: MessageDTO?,
    val createdAt: Instant
)
