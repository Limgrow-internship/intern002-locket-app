package com.intern002.locketapp.ui.screen.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GridPostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private var currentPage = 1
    private val pageSize = 20
    private var isLastPage = false
    private var isLoading = false

    init {
        loadPosts(isRefresh = true)
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
}