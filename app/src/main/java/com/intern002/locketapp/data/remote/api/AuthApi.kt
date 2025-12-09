package com.intern002.locketapp.data.remote.api

import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.remote.model.auth.*
import com.intern002.locketapp.data.remote.response.AuthResponse
import com.intern002.locketapp.data.remote.response.CheckEmailResponse
import com.intern002.locketapp.di.SessionManager
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class AuthApi @Inject constructor(
    private val sessionManager: SessionManager
) {
    private val baseUrl = BuildConfig.BASE_URL

    private fun client() = sessionManager.getClient()

    suspend fun checkEmail(email: String): CheckEmailResponse {
        return client().get("$baseUrl/auth/check-email") {
            url {
                parameters.append("email", email)
            }
        }.body()
    }

    suspend fun register(request: RegisterRequest): AuthResponse {
        return client().post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun login(request: LoginRequest): AuthResponse {
        return client().post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun googleLogin(request: GoogleLoginRequest): HttpResponse {
        return client().post("$baseUrl/auth/google") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun completeGoogleRegistration(request: CompleteGoogleRegistrationRequest): AuthResponse {
        return client().post("$baseUrl/auth/google/complete") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun refreshToken(request: RefreshRequest): AuthResponse {
        return client().post("$baseUrl/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}