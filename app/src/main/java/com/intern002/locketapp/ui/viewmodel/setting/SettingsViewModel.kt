package com.intern002.locketapp.ui.viewmodel.setting

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.CloudinaryRepository
import com.intern002.locketapp.data.repository.UserRepository
import com.intern002.locketapp.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class AvatarUpdateState {
    object Idle : AvatarUpdateState()
    object Loading : AvatarUpdateState()
    object Success : AvatarUpdateState()
    data class Error(val message: String) : AvatarUpdateState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val userRepository: UserRepository,
    private val cloudinaryRepository: CloudinaryRepository
) : ViewModel() {

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    private val _avatarUpdateState = MutableStateFlow<AvatarUpdateState>(AvatarUpdateState.Idle)
    val avatarUpdateState = _avatarUpdateState.asStateFlow()

    fun onLogoutClicked() {
        viewModelScope.launch {
            logoutUseCase()
            _logoutEvent.emit(Unit)
        }
    }

    fun onAvatarSelected(imageUri: Uri) {
        viewModelScope.launch {
            _avatarUpdateState.value = AvatarUpdateState.Loading
            try {
                val oldAvatarUrl = userRepository.getCurrentUserProfile().avatarUrl

                val uploadedImageUrl = cloudinaryRepository.uploadMedia(imageUri, isVideo = false)

                if (oldAvatarUrl != null) {
                    try {
                        cloudinaryRepository.deleteImage(oldAvatarUrl)
                    } catch (e: Exception) {
                        // Log error but continue updating DB
                    }
                }

                userRepository.updateAvatar(uploadedImageUrl)
                _avatarUpdateState.value = AvatarUpdateState.Success
            } catch (e: Exception) {
                _avatarUpdateState.value = AvatarUpdateState.Error(e.message ?: "Avatar update failed")
            }
        }
    }

    fun onDeleteAvatar() {
        viewModelScope.launch {
            _avatarUpdateState.value = AvatarUpdateState.Loading
            try {
                val currentAvatarUrl = userRepository.getCurrentUserProfile().avatarUrl
                if (currentAvatarUrl != null) {
                    cloudinaryRepository.deleteImage(currentAvatarUrl)
                }
                userRepository.updateAvatar(null)
                _avatarUpdateState.value = AvatarUpdateState.Success
            } catch (e: Exception) {
                _avatarUpdateState.value = AvatarUpdateState.Error(e.message ?: "Failed to delete avatar")
            }
        }
    }
}