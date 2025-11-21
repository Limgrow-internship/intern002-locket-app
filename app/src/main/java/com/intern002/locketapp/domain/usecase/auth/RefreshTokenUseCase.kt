package com.intern002.locketapp.domain.usecase.auth

import com.intern002.locketapp.data.remote.response.AuthResponse
import com.intern002.locketapp.data.repository.AuthRepository
import com.intern002.locketapp.utils.Result
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(refreshToken: String): Result<AuthResponse> {
        return repository.refreshToken(refreshToken)
    }
}
