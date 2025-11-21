package com.intern002.locketapp.data.remote.model.auth

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class RefreshRequest(
    val refreshToken: String
)
