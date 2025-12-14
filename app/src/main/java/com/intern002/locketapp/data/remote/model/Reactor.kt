package com.intern002.locketapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class Reactor(
    @SerializedName("user_id") val userId: String,
    @SerializedName("username") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("emoji") val emoji: String
)