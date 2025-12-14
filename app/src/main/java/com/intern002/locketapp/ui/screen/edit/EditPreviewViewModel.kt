package com.intern002.locketapp.ui.screen.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.model.Friend
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.remote.model.post.CreatePostRequest
import com.intern002.locketapp.data.repository.CloudinaryRepository
import com.intern002.locketapp.data.repository.FriendshipRepository
import com.intern002.locketapp.data.repository.PostRepository
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
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
    private val friendshipRepository: FriendshipRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState
    private val _friendsList = MutableStateFlow<List<Friend>>(emptyList())
    val friendsList: StateFlow<List<Friend>> = _friendsList

    init {
        fetchFriends()
    }

    fun sendPost(uri: Uri, isVideo: Boolean, caption: String, friendIds: List<String>) {
        _sendState.value = SendState.Loading
        viewModelScope.launch {
            try {
                val cloudUrl = cloudinaryRepository.uploadMedia(uri, isVideo)

                val type = if (isVideo) "video" else "photo"
                val me = userRepository.getCurrentUserProfile()

                val myPost = Post(
                    UUID.randomUUID().toString(),
                    me.id,
                    me.username,
                    me.avatarUrl,
                    cloudUrl,
                    type,
                    caption,
                    getCurrentIsoTime(),
                    0,
                    emptyList()
                )
                val savePostResult = postRepository.saveLocalPost(myPost)

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
            try {
                val friends = friendshipRepository.getFriends()

                android.util.Log.d("DEBUG_FRIEND", "API trả về: ${friends.size} bạn")

                friends.forEach { friend ->
                    android.util.Log.d(
                        "DEBUG_FRIEND",
                        "--> Tên: ${friend.username}, ID: ${friend.id}"
                    )
                }

                _friendsList.value = friends

            } catch (e: Exception) {
                android.util.Log.e("DEBUG_FRIEND", "Toang rồi: ${e.message}")
                _friendsList.value = emptyList()
            }
        }
    }

    private fun getCurrentIsoTime(): String {
        val df = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
        df.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return df.format(java.util.Date())
    }
}