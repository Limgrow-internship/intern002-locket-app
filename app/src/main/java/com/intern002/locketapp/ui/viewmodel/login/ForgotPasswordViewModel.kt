package com.intern002.locketapp.ui.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.AuthRepository
import com.intern002.locketapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorEvent = _errorChannel.receiveAsFlow()

    // 1. Gọi ở màn hình nhập Email (LoginPasswordFragment khi bấm Forgot)
    fun sendOtp(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.requestOtp(email)
            _isLoading.value = false

            when (result) {
                is Result.Success -> {
                    onSuccess()
                }

                is Result.Error -> {
                    result.message
                    _errorChannel.send(
                        result.message ?: "Lỗi gửi OTP"
                    )
                }

                else -> {
                    _errorChannel.send("Lỗi không xác định")
                }
            }
        }
    }


    fun verifyOtp(email: String, otp: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.verifyOtp(email, otp)
            _isLoading.value = false

            when (result) {
                is Result.Success -> {
                    onSuccess()
                }

                is Result.Error -> {
                    result.message
                    _errorChannel.send(
                        result.message ?: "Lỗi xác thực OTP"
                    )
                }

                else -> {
                    _errorChannel.send("Lỗi không xác định")
                }
            }
        }
    }

    // 3. Gọi ở màn hình nhập Pass mới
    fun resetPassword(email: String, otp: String, newPass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.resetPassword(email, otp, newPass)
            _isLoading.value = false

            when (result) {
                is Result.Success -> {
                    onSuccess()
                }

                is Result.Error -> {
                    result.message
                    _errorChannel.send(
                        result.message ?: "Lỗi đặt lại mật khẩu"
                    )
                }

                else -> {
                    _errorChannel.send("Lỗi không xác định")
                }
            }
        }
    }
}