package com.intern002.locketapp.data.remote.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class UserDTO(
    val id: String,
    val username: String,
    val discriminator: Int,
    val avatarUrl: String? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class FriendshipDTO(
    val user: UserDTO,
    val status: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class FriendDTO(
    val id: String,
    val username: String,
    val discriminator: Int,
    val avatarUrl: String? = null,
    val status: String? = null
)
