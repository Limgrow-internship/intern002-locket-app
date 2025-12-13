package com.intern002.locketapp.ui.viewmodel.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChangeBirthdayState {
    object Idle : ChangeBirthdayState()
    object Loading : ChangeBirthdayState()
    object Success : ChangeBirthdayState()
    data class Error(val message: String?) : ChangeBirthdayState()
    object SameBirthdayError : ChangeBirthdayState()
}

@HiltViewModel
class ChangeBirthdayViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangeBirthdayState>(ChangeBirthdayState.Idle)
    val uiState: StateFlow<ChangeBirthdayState> = _uiState

    fun changeBirthday(newBirthday: String) {
        viewModelScope.launch {
            _uiState.value = ChangeBirthdayState.Loading
            try {
                userRepository.updateBirthday(newBirthday)
                _uiState.value = ChangeBirthdayState.Success
            } catch (e: Exception) {
                Log.d("ChangeBirthdayVM", "Caught exception: ${e::class.java.simpleName}")

                var isConflict = false
                var cause: Throwable? = e
                while (cause != null) {
                    if (cause is ResponseException && cause.response.status == HttpStatusCode.Conflict) {
                        isConflict = true
                        break
                    }
                    cause = cause.cause
                }

                if (isConflict || e is NoTransformationFoundException) {
                    _uiState.value = ChangeBirthdayState.SameBirthdayError
                } else {
                    _uiState.value = ChangeBirthdayState.Error(e.message ?: "An unknown error occurred")
                }
            }
        }
    }
}