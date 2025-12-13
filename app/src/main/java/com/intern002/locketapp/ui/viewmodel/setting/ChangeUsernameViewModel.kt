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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChangeUsernameState {
    object Idle : ChangeUsernameState()
    object Loading : ChangeUsernameState()
    object Success : ChangeUsernameState()
    data class Error(val message: String?) : ChangeUsernameState()
    object SameUsernameError : ChangeUsernameState()
}

@HiltViewModel
class ChangeUsernameViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangeUsernameState>(ChangeUsernameState.Idle)
    val uiState: StateFlow<ChangeUsernameState> = _uiState

    fun changeUsername(newUsername: String) {
        viewModelScope.launch {
            _uiState.value = ChangeUsernameState.Loading
            try {
                userRepository.updateUsername(newUsername)
                _uiState.value = ChangeUsernameState.Success
            } catch (e: Exception) {
                Log.d("ChangeUsernameVM", "Caught exception: ${e::class.java.simpleName}")

                if (e is ClientRequestException) {
                    Log.d("ChangeUsernameVM", "Exception is ClientRequestException. Status: ${e.response.status}")
                }

                if ((e is ClientRequestException && e.response.status == HttpStatusCode.Conflict) || e is NoTransformationFoundException) {
                     _uiState.value = ChangeUsernameState.SameUsernameError
                } else {
                    _uiState.value = ChangeUsernameState.Error(e.message ?: "An unknown error occurred")
                }
            }
        }
    }
}