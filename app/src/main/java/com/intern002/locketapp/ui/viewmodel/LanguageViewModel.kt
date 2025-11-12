package com.intern002.locketapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern002.locketapp.data.remote.model.Language
import com.intern002.locketapp.data.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(private val repo: LanguageRepository) : ViewModel() {

    private val _selectedLanguageCode = MutableStateFlow<String?>(null)
    val selectedLanguageCode: StateFlow<String?> = _selectedLanguageCode.asStateFlow()

    private val _allLanguages = MutableStateFlow<List<Language>>(emptyList())
    val searchQuery = MutableStateFlow("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val languages: StateFlow<List<Language>> = searchQuery
        .combine(_allLanguages) { query, languages ->
            if (query.isBlank()) {
                languages
            } else {
                languages.filter { it.name.contains(query, ignoreCase = true) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _allLanguages.value
        )

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val data = repo.fetchLanguages()
                _allLanguages.value = data

                if (data.isEmpty()) {
                    _error.value = "No languages available"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load languages: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedLanguageCode(code: String) {
        _selectedLanguageCode.value = code.lowercase()
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}