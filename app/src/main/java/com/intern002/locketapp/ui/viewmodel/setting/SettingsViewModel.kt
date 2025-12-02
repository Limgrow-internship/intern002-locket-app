package com.intern002.locketapp.ui.viewmodel.setting

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
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
    private val mediaManager: MediaManager
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
        _avatarUpdateState.value = AvatarUpdateState.Loading
        mediaManager.upload(imageUri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val uploadedImageUrl = resultData?.get("secure_url") as? String
                    if (uploadedImageUrl != null) {
                        viewModelScope.launch {
                            try {
                                userRepository.updateAvatar(uploadedImageUrl)
                                _avatarUpdateState.value = AvatarUpdateState.Success
                            } catch (e: Exception) {
                                _avatarUpdateState.value = AvatarUpdateState.Error(e.message ?: "Failed to save URL")
                            }
                        }
                    } else {
                        _avatarUpdateState.value = AvatarUpdateState.Error("Could not get URL from Cloudinary")
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    _avatarUpdateState.value = AvatarUpdateState.Error(error?.description ?: "Upload to Cloudinary failed")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    fun onDeleteAvatar() {
        viewModelScope.launch {
            _avatarUpdateState.value = AvatarUpdateState.Loading
            try {
                userRepository.updateAvatar(null) // Pass null to delete
                _avatarUpdateState.value = AvatarUpdateState.Success
            } catch (e: Exception) {
                _avatarUpdateState.value = AvatarUpdateState.Error(e.message ?: "Failed to delete avatar")
            }
        }
    }
}