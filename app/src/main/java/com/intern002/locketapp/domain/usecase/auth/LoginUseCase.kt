package com.intern002.locketapp.domain.usecase.auth

import com.intern002.locketapp.data.remote.response.AuthResponse
import com.intern002.locketapp.data.repository.AuthRepository
import com.intern002.locketapp.utils.Result // Correct import
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        return authRepository.login(email, password)
    }
}
