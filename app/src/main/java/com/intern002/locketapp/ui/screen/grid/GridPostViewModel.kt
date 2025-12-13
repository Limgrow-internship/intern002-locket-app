package com.intern002.locketapp.ui.screen.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.model.UserProfile
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.repository.PostRepository
import com.intern002.locketapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GridPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private var currentPage = 1
    private val pageSize = 20
    private var isLastPage = false
    private var isLoading = false

    init {
        loadPosts(isRefresh = true)
        fetchUserProfile()
    }

    fun loadPosts(isRefresh: Boolean = false) {
        if (isLoading) return
        if (isRefresh) {
            currentPage = 1
            isLastPage = false
        } else if (isLastPage) {
            return
        }

        isLoading = true
        viewModelScope.launch {
            val result = postRepository.getPosts(currentPage, pageSize)
            result.onSuccess { newPosts ->
                if (isRefresh) {
                    _posts.value = newPosts
                } else {
                    _posts.value += newPosts
                }

                if (newPosts.size < pageSize) {
                    isLastPage = true
                } else {
                    currentPage++
                }
                isLoading = false
            }

            result.onFailure {
                isLoading = false
            }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                _userProfile.value = userRepository.getCurrentUserProfile()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}