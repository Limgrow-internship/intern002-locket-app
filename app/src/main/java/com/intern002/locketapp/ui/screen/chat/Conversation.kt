package com.intern002.locketapp.ui.screen.chat

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val avatarUrl: String,
    val lastMessageFromMe: Boolean, // Added this field
    val isRead: Boolean
)
