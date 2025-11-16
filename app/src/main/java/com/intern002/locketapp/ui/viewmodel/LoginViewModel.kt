package com.intern002.locketapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


// Định nghĩa các trạng thái mà màn hình Login có thể có
sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Success(val userName: String) : LoginState
    data class Error(val message: String) : LoginState
}

class LoginViewModel(
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun onGoogleLoginClicked() {
        if(_loginState.value != LoginState.Idle) return

        _loginState.value = LoginState.Loading

        //call coroutine
        viewModelScope.launch {
            try {
                delay(2000) // Giả gọi api, nào có sẽ thay
                _loginState.value = LoginState.Success("Long Đại Ca") // fake
            }catch(e: Exception) {
                _loginState.value = LoginState.Error(e.message.toString())
            }
        }
    }

    fun onButtonLoginClicked() {
        if(_loginState.value != LoginState.Idle) return

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {

            }catch(e: Exception) {

            }
        }
    }
}