package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.data.remote.model.post.CreatePostRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class PostApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun createPost(request: CreatePostRequest): HttpResponse {
        return client.post("posts") { // Endpoint
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}