package com.intern002.locketapp.data.remote.model.post

import com.google.gson.annotations.SerializedName

data class CreatePostRequest(
    @SerializedName("media_url")
    val mediaUrl: String,

    @SerializedName("media_type")
    val mediaType: String,

    @SerializedName("caption")
    val caption: String?,

    @SerializedName("recipient_ids")
    val recipientIds: List<String>
)