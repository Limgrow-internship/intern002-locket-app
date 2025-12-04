package com.intern002.locketapp.data.remote.model.fcm

import kotlinx.serialization.Serializable

@Serializable
data class RegisterFcmTokenRequest(
    val token: String,
    val deviceInfo: String? = null // Optional, you can add device info if needed
)
