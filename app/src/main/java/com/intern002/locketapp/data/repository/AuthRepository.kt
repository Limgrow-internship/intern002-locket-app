package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.api.AuthApi
import com.intern002.locketapp.data.remote.api.UserApi
import com.intern002.locketapp.data.remote.model.auth.*
import com.intern002.locketapp.data.remote.response.AuthResponse
import com.intern002.locketapp.data.remote.response.CheckEmailResponse
import com.intern002.locketapp.utils.Result
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi
) {

    suspend fun checkEmailExists(email: String): Result<Boolean> {
        return try {
            val response = authApi.checkEmail(email)
            Result.Success(response.exists)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun register(email: String, username: String, password: String, birthday: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(email, username, password, birthday)
            val response = authApi.register(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = authApi.login(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun googleLogin(idToken: String): Result<AuthResponse> {
        return try {
            val request = GoogleLoginRequest(idToken)
            val response = authApi.googleLogin(request)

            when (response.status) {
                HttpStatusCode.OK -> {
                    val authResponse = response.body<AuthResponse>()
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
        return try {
            val request = CompleteGoogleRegistrationRequest(idToken, username, birthday)
            val response = authApi.completeGoogleRegistration(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return try {
            val request = RefreshRequest(refreshToken)
            val response = authApi.refreshToken(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun logout() {
        try {
            userApi.logout()
        } catch (e: Exception) {
            // Logout failures can often be ignored on the client-side
            // as the main goal is to clear local tokens.
        }
    }
}