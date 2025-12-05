package com.intern002.locketapp.data.remote.model.fcm

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class RegisterFcmTokenRequest(
    val token: String,
    val deviceInfo: String? = null
)
