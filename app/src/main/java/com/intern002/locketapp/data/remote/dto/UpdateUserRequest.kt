package com.intern002.locketapp.data.remote.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class UpdateUserRequest(
    val email: String? = null,
    val username: String? = null
)