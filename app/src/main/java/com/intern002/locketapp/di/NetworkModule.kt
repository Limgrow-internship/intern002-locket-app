package com.intern002.locketapp.di

import android.util.Log
import com.intern002.locketapp.BuildConfig
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.data.remote.api.AuthApi
import com.intern002.locketapp.data.remote.api.ChatApi
import com.intern002.locketapp.data.remote.api.UserApi
import com.intern002.locketapp.data.remote.model.auth.RefreshRequest
import com.intern002.locketapp.data.remote.response.AuthResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(authManager: AuthManager): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorLogger", message)
                    }
                }
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Auth) {
                bearer {
                    // 1. Tải Token mới nhất từ AuthManager
                    loadTokens {
                        val accessToken = runBlocking { authManager.getAccessToken().first() }
                        val refreshToken = runBlocking { authManager.getRefreshToken().first() }
                        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                            null
                        } else {
                            // Trả về token mới nhất đã được lưu trong DataStore
                            BearerTokens(accessToken, refreshToken)
                        }
                    }

                    // 2. Refresh Token (Logic làm mới token)
                    refreshTokens {
                        val refreshTokenValue = runBlocking { authManager.getRefreshToken().first() }
                        if (refreshTokenValue.isNullOrBlank()) {
                            return@refreshTokens null
                        }

                        try {
                            val response: AuthResponse = client.post("${BuildConfig.BASE_URL}/auth/refresh") {
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

                    // 3. BẮT BUỘC: Đảm bảo Ktor gửi token cho các request cần xác thực
                    sendWithoutRequest { request ->
                        val path = request.url.encodedPath
                        // Không gửi token cho các endpoint xác thực (login, register, google, refresh, check-email)
                        !path.contains("/auth/login") &&
                                !path.contains("/auth/register") &&
                                !path.contains("/auth/google") &&
                                !path.contains("/auth/refresh") &&
                                !path.contains("/auth/check-email")
                    }
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideAuthApi(client: HttpClient): AuthApi {
        return AuthApi(client)
    }

    @Provides
    @Singleton
    fun provideUserApi(client: HttpClient): UserApi {
        return UserApi(client)
    }

    @Provides
    @Singleton
    fun provideChatApi(client: HttpClient): ChatApi {
        return ChatApi(client)
    }
}