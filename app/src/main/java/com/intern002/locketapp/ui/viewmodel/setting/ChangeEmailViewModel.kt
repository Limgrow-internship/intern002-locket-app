package com.intern002.locketapp.ui.viewmodel.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UpdateEmailState {
    object Idle : UpdateEmailState()
    object Loading : UpdateEmailState()
    object Success : UpdateEmailState()
    data class Error(val message: String) : UpdateEmailState()
}

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _updateState = MutableStateFlow<UpdateEmailState>(UpdateEmailState.Idle)
    val updateState = _updateState.asStateFlow()

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            _updateState.value = UpdateEmailState.Loading
            try {
                userRepository.updateEmail(newEmail)
                _updateState.value = UpdateEmailState.Success
            } catch (e: Exception) {
                Log.d("DEBUG_EMAIL_ERROR", "Caught exception: ${e::class.java.simpleName}")

                if (e is ClientRequestException) {
                    Log.d("DEBUG_EMAIL_ERROR", "Exception is ClientRequestException. Status: ${e.response.status}")
                }

                if ((e is ClientRequestException && e.response.status == HttpStatusCode.Conflict) || e is NoTransformationFoundException) {
                     _updateState.value = UpdateEmailState.Error("EMAIL_EXISTS")
                } else {
                    _updateState.value = UpdateEmailState.Error(e.message ?: "An unknown error occurred")
                }
            }
        }
    }
    fun resetState() {
        _updateState.value = UpdateEmailState.Idle
    }
}