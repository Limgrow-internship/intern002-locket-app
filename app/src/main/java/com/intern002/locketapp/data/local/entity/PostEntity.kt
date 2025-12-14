package com.intern002.locketapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.remote.model.Reactor

@Entity(tableName = "posts")
@TypeConverters(Converters::class)
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val userName: String,
    val userAvatarUrl: String?,
    val mediaUrl: String,
    val mediaType: String,
    val caption: String?,
    val createdAt: String,
    val reactionCount: Int,
    val latestReactions: List<Reactor>
) {
    fun toPost() = Post(
        id,
        authorId,
        userName,
        userAvatarUrl,
        mediaUrl,
        mediaType,
        caption,
        createdAt,
        reactionCount,
        latestReactions
    )
}

fun Post.toEntity() = PostEntity(
    id,
    authorId,
    userName,
    userAvatarUrl,
    mediaUrl,
    mediaType,
    caption,
    createdAt,
    reactionCount,
    latestReactions
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromReactors(list: List<Reactor>?): String = gson.toJson(list ?: emptyList<Reactor>())

    @TypeConverter
    fun toReactors(data: String?): List<Reactor> {
        if (data == null) return emptyList()
        val type = object : TypeToken<List<Reactor>>() {}.type
        return gson.fromJson(data, type)
    }
}