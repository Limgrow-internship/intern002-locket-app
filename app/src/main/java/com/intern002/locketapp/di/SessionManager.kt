package com.intern002.locketapp.di

import android.util.Log
import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.data.remote.model.auth.RefreshRequest
import com.intern002.locketapp.data.remote.response.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val authManager: AuthManager,
    private val httpClientFactory: HttpClientFactory
) {
    private var client: HttpClient? = null

    fun getClient(): HttpClient {
        if (client == null) {
            client = httpClientFactory.create(authManager)
        }
        return client!!
    }

    fun clearSession() {
        client?.close()
        client = null
    }
}

@Singleton
class HttpClientFactory @Inject constructor() {
    fun create(authManager: AuthManager): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true

            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                    serializeNulls()
                }
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorLogger", message)
                    }
                }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = runBlocking { authManager.getAccessToken().first() }
                        val refreshToken = runBlocking { authManager.getRefreshToken().first() }
                        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                            null
                        } else {
                            BearerTokens(accessToken, refreshToken)
                        }
                    }

                    refreshTokens {
                        val refreshTokenValue =
                            runBlocking { authManager.getRefreshToken().first() }
                        if (refreshTokenValue.isNullOrBlank()) {
                            return@refreshTokens null
                        }

                        try {
                            val response: AuthResponse =
                                client.post("${BuildConfig.BASE_URL}/auth/refresh") {
                                    markAsRefreshTokenRequest()
                                    contentType(ContentType.Application.Json)
                                    setBody(RefreshRequest(refreshTokenValue))
                                }.body()

                            runBlocking {
                                authManager.saveTokens(response.accessToken, response.refreshToken)
                            }

                            BearerTokens(response.accessToken, response.refreshToken)
                        } catch (e: Exception) {
                            runBlocking { authManager.clearTokens() }
                            null
                        }
                    }

                    sendWithoutRequest { request ->
                        val path = request.url.encodedPath
                        !path.contains("/auth/login") &&
                                !path.contains("/auth/register") &&
                                !path.contains("/auth/google") &&
                                !path.contains("/auth/refresh") &&
                                !path.contains("/auth/check-email")
                    }
                }
            }

            defaultRequest {
                url(BuildConfig.BASE_URL)
            }
        }
    }
}