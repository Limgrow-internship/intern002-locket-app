package com.intern002.locketapp.ui.screen.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            postRepository.fetchPosts(1, 20)
        }
    }

    val refreshTrigger = MutableLiveData<Boolean>(false)
    val scrollRequest = MutableLiveData<Int?>()
}