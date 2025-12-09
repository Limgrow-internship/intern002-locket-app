package com.intern002.locketapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id") val id: String,
    @SerializedName("authorId") val authorId: String,
    @SerializedName("mediaUrl") val mediaUrl: String,
    @SerializedName("mediaType") val mediaType: String,
    @SerializedName("caption") val caption: String?,
    @SerializedName("createdAt") val createdAt: String,

    @SerializedName("reaction_count")
    val reactionCount: Int = 0,

    @SerializedName("latest_reactions")
    val latestReactions: List<Reactor> = emptyList(),

    var userName: String = "",
    var userAvatarUrl: String? = null
)