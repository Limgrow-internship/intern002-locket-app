package com.intern002.locketapp.ui.screen.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.remote.model.friend.FriendUserResponse
import com.intern002.locketapp.data.remote.model.post.CreatePostRequest
import com.intern002.locketapp.data.repository.CloudinaryRepository
import com.intern002.locketapp.data.repository.FriendRepository
import com.intern002.locketapp.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SendState {
    object Idle : SendState
    object Loading : SendState
    object Success : SendState
    data class Error(val message: String) : SendState
}

@HiltViewModel
class EditPreviewViewModel @Inject constructor(
    private val cloudinaryRepository: CloudinaryRepository,
    private val postRepository: PostRepository,
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState
    private val _friendsList = MutableStateFlow<List<FriendUserResponse>>(emptyList())
    val friendsList: StateFlow<List<FriendUserResponse>> = _friendsList

    init {
        // Tự động load bạn bè khi ViewModel khởi tạo
        fetchFriends()
    }

    fun sendPost(uri: Uri, isVideo: Boolean, caption: String, friendIds: List<String>) {
        _sendState.value = SendState.Loading
        viewModelScope.launch {
            try {
                val cloudUrl = cloudinaryRepository.uploadMedia(uri, isVideo)

                val type = if (isVideo) "video" else "photo"
                val request = CreatePostRequest(cloudUrl, type, caption, friendIds)
                val result = postRepository.createPost(request)

                if (result.isSuccess) {
                    _sendState.value = SendState.Success
                } else {
                    _sendState.value = SendState.Error(result.exceptionOrNull()?.message ?: "Lỗi")
                }
            } catch (e: Exception) {
                _sendState.value = SendState.Error(e.message ?: "Lỗi")
            }
        }
    }

    private fun fetchFriends() {
        viewModelScope.launch {
            friendRepository.getFriends()
                .onSuccess { friends ->
                    _friendsList.value = friends
                }
                .onFailure {
                }
        }
    }
}