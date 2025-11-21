package com.intern002.locketapp.data.remote.response

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)