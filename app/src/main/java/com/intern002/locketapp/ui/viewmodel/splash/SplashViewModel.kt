package com.intern002.locketapp.ui.viewmodel.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.domain.usecase.auth.RefreshTokenUseCase
import com.intern002.locketapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashState {
    object Loading : SplashState()
    object Authenticated : SplashState()
    object Unauthenticated : SplashState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val authManager: AuthManager
) : ViewModel() {

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState: StateFlow<SplashState> = _splashState

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            val refreshToken = authManager.getRefreshToken().first()
            if (refreshToken == null) {
                _splashState.value = SplashState.Unauthenticated
                return@launch
            }

            when (val result = refreshTokenUseCase(refreshToken)) {
                is Result.Success -> {
                    result.data?.let {
                        authManager.saveTokens(it.accessToken, it.refreshToken)
                        _splashState.value = SplashState.Authenticated
                    } ?: run {
                        _splashState.value = SplashState.Unauthenticated
                    }
                }
                is Result.Error -> {
                    _splashState.value = SplashState.Unauthenticated
                }
                else -> { /* Do nothing for Loading */ }
            }
        }
    }
}
