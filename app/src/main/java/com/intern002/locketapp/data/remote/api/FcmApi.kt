package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.remote.model.fcm.RegisterFcmTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FcmApi(private val client: HttpClient) {
    private val baseUrl = BuildConfig.BASE_URL

    suspend fun registerToken(request: RegisterFcmTokenRequest) {
        client.post("$baseUrl/fcm/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
