package com.intern002.locketapp.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.data.remote.api.AuthApi
import com.intern002.locketapp.data.remote.api.UserApi
import com.intern002.locketapp.data.remote.model.auth.*
import com.intern002.locketapp.data.remote.response.AuthResponse
import com.intern002.locketapp.utils.Result
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val userRepository: UserRepository,
    private val authManager: AuthManager,
    private val fcmRepository: FcmRepository
) {

    private suspend fun registerFcmToken() {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            fcmRepository.registerToken(token)
            Log.d("AuthRepository", "FCM Token registered successfully: $token")
        } catch (e: Exception) {
            Log.e("AuthRepository", "FCM Token registration failed", e)
        }
    }

    suspend fun checkEmailExists(email: String): Result<Boolean> {
        return try {
            val response = authApi.checkEmail(email)
            Result.Success(response.exists)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun register(email: String, username: String, password: String, birthday: String): Result<AuthResponse> {
        userRepository.clearCurrentUserProfile()
        return try {
            val request = RegisterRequest(email, username, password, birthday)
            val response = authApi.register(request)
            authManager.saveTokens(response.accessToken, response.refreshToken)
            registerFcmToken()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        userRepository.clearCurrentUserProfile()
        return try {
            val request = LoginRequest(email, password)
            val response = authApi.login(request)
            authManager.saveTokens(response.accessToken, response.refreshToken)
            registerFcmToken()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun googleLogin(idToken: String): Result<AuthResponse> {
        userRepository.clearCurrentUserProfile()
        return try {
            val request = GoogleLoginRequest(idToken)
            val response = authApi.googleLogin(request)

            when (response.status) {
                HttpStatusCode.OK -> {
                    val authResponse = response.body<AuthResponse>()
                    authManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                    registerFcmToken() // Register FCM token reliably
                    Result.Success(authResponse)
                }
                HttpStatusCode.Accepted -> {
                    val registrationInfo = response.body<GoogleRegistrationInfo>()
                    Result.RegistrationRequired(registrationInfo)
                }
                else -> {
                    Result.Error(response.status.description)
                }
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun completeGoogleRegistration(idToken: String, username: String, birthday: String): Result<AuthResponse> {
        userRepository.clearCurrentUserProfile()
        return try {
            val request = CompleteGoogleRegistrationRequest(idToken, username, birthday)
            val response = authApi.completeGoogleRegistration(request)
            authManager.saveTokens(response.accessToken, response.refreshToken)
            registerFcmToken() // Register FCM token reliably
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return try {
            val request = RefreshRequest(refreshToken)
            val response = authApi.refreshToken(request)
            authManager.saveTokens(response.accessToken, response.refreshToken)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun logout() {
        try {
            userApi.logout()
        } catch (e: Exception) {
        }
        authManager.clearTokens()

        userRepository.clearCurrentUserProfile()
    }

}
