// LanguageRepository.kt
package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.datasource.LanguageDataSource
import com.intern002.locketapp.data.remote.model.Language
import kotlinx.coroutines.delay

class LanguageRepository {

    suspend fun fetchLanguages(): List<Language> {
        delay(500)

        return LanguageDataSource.getLanguages()
    }
}