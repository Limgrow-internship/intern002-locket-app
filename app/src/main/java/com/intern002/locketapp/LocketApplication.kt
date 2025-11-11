package com.intern002.locketapp

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.intern002.locketapp.ui.screen.language.LanguageFragment
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocketApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        applySavedLanguage()
    }

    private fun applySavedLanguage() {
        val prefs = getSharedPreferences(LanguageFragment.LANGUAGE_PREFS, Context.MODE_PRIVATE)
        val languageCode = prefs.getString(LanguageFragment.SELECTED_LANGUAGE, null)

        val localeToUse = if (languageCode.isNullOrBlank()) "en" else languageCode

        val localeList = LocaleListCompat.forLanguageTags(localeToUse)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
