package com.intern002.locketapp.data.remote.model.reaction

import com.google.gson.annotations.SerializedName

data class ReactionTypeResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("emoji") val emoji: String,
    @SerializedName("image_url") val imageUrl: String?,
)