package com.intern002.locketapp.domain.usecase.auth

import com.intern002.locketapp.data.remote.response.AuthResponse
import com.intern002.locketapp.data.repository.AuthRepository
import com.intern002.locketapp.utils.Result
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, username: String, password: String, birthday: String): Result<AuthResponse> {
        return authRepository.register(email, username, password, birthday)
    }
}
