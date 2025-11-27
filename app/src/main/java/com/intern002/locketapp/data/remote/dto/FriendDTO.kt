package com.intern002.locketapp.data.remote.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class FriendDTO(
    val id: String,
    val email: String? = null,
    val username: String,
    val discriminator: Int,
    val birthday: String,
    val avatarUrl: String? = null
)
