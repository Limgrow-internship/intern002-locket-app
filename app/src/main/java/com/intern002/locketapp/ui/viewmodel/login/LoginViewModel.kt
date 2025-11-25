package com.intern002.locketapp.ui.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.prefs.AuthManager
import com.intern002.locketapp.domain.usecase.auth.GoogleLoginUseCase
import com.intern002.locketapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
    data class RegistrationRequired(val idToken: String, val email: String?, val suggestedUsername: String?) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val authManager: AuthManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun onGoogleLoginResult(idToken: String?) {
        if (idToken == null) {
            _loginState.value = LoginState.Error("Google Sign-In failed.")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            when (val result = googleLoginUseCase(idToken)) {
                is Result.Success -> {
                    result.data?.let {
                        authManager.saveTokens(it.accessToken, it.refreshToken)
                        _loginState.value = LoginState.Success
                    } ?: run {
                        _loginState.value = LoginState.Error("Login failed: Response was empty.")
                    }
                }
                is Result.Error -> {
                    _loginState.value = LoginState.Error(result.message ?: "An unknown error occurred")
                }
                is Result.RegistrationRequired -> {
                    val info = result.registrationInfo
                    _loginState.value = LoginState.RegistrationRequired(
                        idToken = idToken,
                        email = info.email,
                        suggestedUsername = info.suggestedUsername
                    )
                }
                else -> {}
            }
        }
    }
}
