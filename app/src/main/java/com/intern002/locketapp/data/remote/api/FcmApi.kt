package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.remote.model.fcm.RegisterFcmTokenRequest
import com.intern002.locketapp.di.SessionManager
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class FcmApi @Inject constructor(private val sessionManager: SessionManager) {
    private val baseUrl = BuildConfig.BASE_URL

    private fun client() = sessionManager.getClient()

    suspend fun registerToken(request: RegisterFcmTokenRequest) {
        client().post("$baseUrl/fcm/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
