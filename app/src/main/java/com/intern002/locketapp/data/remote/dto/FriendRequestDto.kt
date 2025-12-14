package com.intern002.locketapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FriendRequestDto(
    @SerializedName("username")
    val username: String,

    @SerializedName("discriminator")
    val discriminator: Int
)