package com.intern002.locketapp.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.data.remote.api.AuthApi
import com.intern002.locketapp.data.remote.api.UserApi
import com.intern002.locketapp.data.remote.model.auth.CompleteGoogleRegistrationRequest
import com.intern002.locketapp.data.remote.model.auth.GoogleLoginRequest
import com.intern002.locketapp.data.remote.model.auth.GoogleRegistrationInfo
import com.intern002.locketapp.data.remote.model.auth.LoginRequest
import com.intern002.locketapp.data.remote.model.auth.RefreshRequest
import com.intern002.locketapp.data.remote.model.auth.RegisterRequest
import com.intern002.locketapp.data.remote.response.AuthResponse
import com.intern002.locketapp.utils.Result
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
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

    suspend fun register(
        email: String,
        username: String,
        password: String,
        birthday: String
    ): Result<AuthResponse> {
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

    suspend fun completeGoogleRegistration(
        idToken: String,
        username: String,
        birthday: String
    ): Result<AuthResponse> {
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

    suspend fun requestOtp(email: String): Result<Unit> {
        return try {
            val response = authApi.forgotPassword(email)
            if (response.status.isSuccess()) {
                Result.Success(Unit)
            } else {
                Result.Error("Xin OTP thất bại")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")

        }
    }

    // 2. Verify OTP
    suspend fun verifyOtp(email: String, otp: String): Result<Unit> {
        return try {
            val response = authApi.verifyOtp(email, otp)
            if (response.status.isSuccess()) {
                Result.Success(Unit)
            } else {
                Result.Error("Nhập OTP thất bại")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    // 3. Reset Pass
    suspend fun resetPassword(email: String, otp: String, newPass: String): Result<Unit> {
        return try {
            val response = authApi.resetPassword(email, otp, newPass)
            if (response.status.isSuccess()) {
                Result.Success(Unit)
            } else {
                Result.Error("Đặt lại mật khẩu thất bại")
            }
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
