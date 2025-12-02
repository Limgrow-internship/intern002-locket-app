package com.intern002.locketapp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import com.cloudinary.android.MediaManager
import com.intern002.locketapp.ui.screen.language.LanguageFragment
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocketApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        applySavedLanguage()

        try {
            val cloudinaryUrl = BuildConfig.CLOUDINARY_URL
            val uri = cloudinaryUrl.toUri()
            val config = HashMap<String, String>()
            config["cloud_name"] = uri.host ?: ""
            val userInfo = uri.userInfo?.split(":")
            if (userInfo != null && userInfo.size >= 2) {
                config["api_key"] = userInfo[0]
                config["api_secret"] = userInfo[1]
            }
            MediaManager.init(this, config)

            Log.d("LocketApp", "Cloudinary Init Success: ${uri.host}")

        } catch (e: Exception) {
            Log.e("LocketApp", "Cloudinary Init Failed: ${e.message}")
        }
    }

    private fun applySavedLanguage() {
        val prefs = getSharedPreferences(LanguageFragment.LANGUAGE_PREFS, Context.MODE_PRIVATE)
        val languageCode = prefs.getString(LanguageFragment.SELECTED_LANGUAGE, null)

        val localeToUse = if (languageCode.isNullOrBlank()) "en" else languageCode

        val localeList = LocaleListCompat.forLanguageTags(localeToUse)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
