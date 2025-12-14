package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.di.SessionManager
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class ReactionApi @Inject constructor(
    private val sessionManager: SessionManager
) {
    private val baseUrl = BuildConfig.BASE_URL
    private fun client() = sessionManager.getClient()

    suspend fun getReactionTypes(): HttpResponse {
        return client().get("$baseUrl/meta/reactions")
    }

    suspend fun reactToPost(request: ReactRequestDto): HttpResponse {
        return client().post("$baseUrl/posts/react") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}

data class ReactRequestDto(
    @com.google.gson.annotations.SerializedName("post_id") val postId: String,
    @com.google.gson.annotations.SerializedName("reaction_type_id") val reactionTypeId: Int
)
