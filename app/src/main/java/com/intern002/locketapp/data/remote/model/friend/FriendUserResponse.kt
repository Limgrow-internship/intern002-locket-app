package com.intern002.locketapp.data.remote.model.friend

import com.google.gson.annotations.SerializedName

data class FriendUserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("discriminator") val discriminator: Int,
    @SerializedName("avatarUrl") val avatarUrl: String?
) {
    fun getFullName(): String = username
}