package com.intern002.locketapp.ui.screen.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val refreshTrigger = MutableLiveData<Boolean>(false)
    val scrollRequest = MutableLiveData<Int?>()
}