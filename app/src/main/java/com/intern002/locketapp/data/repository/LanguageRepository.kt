package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.api.LanguageApiService
import com.intern002.locketapp.data.remote.model.Language
import com.intern002.locketapp.data.remote.model.CountryResponse

class LanguageRepository(private val api: LanguageApiService) {

    private val languageToCountryMap = mapOf(
        "en" to "gb",
        "es" to "es",
        "fr" to "fr",
        "de" to "de",
        "pt" to "pt",
        "ar" to "sa",
        "zh" to "cn",
        "ja" to "jp",
        "ko" to "kr",
        "vi" to "vn",
        "ru" to "ru",
        "it" to "it",
        "th" to "th",
        "id" to "id",
        "hi" to "in",
        "tr" to "tr",
        "pl" to "pl",
        "nl" to "nl",
        "sv" to "se",
        "da" to "dk",
        "fi" to "fi",
        "no" to "no",
        "cs" to "cz",
    )

    suspend fun fetchLanguages(): List<Language> {
        val countries = api.getCountries()

        val languageMap = mutableMapOf<String, Language>()

        countries.forEach { country ->
            country.languages?.forEach { (langCode, langName) ->
                if (!languageMap.containsKey(langCode)) {
                    val countryCode = languageToCountryMap[langCode]
                        ?: country.cca2.lowercase()

                    val flagUrl = "https://flagcdn.com/w320/${countryCode}.png"

                    languageMap[langCode] = Language(
                        code = langCode,
                        name = langName,
                        flagUrl = flagUrl
                    )
                }
            }
        }

        return languageMap.values.sortedBy { it.name }
    }
}