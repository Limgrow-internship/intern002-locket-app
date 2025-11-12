package com.intern002.locketapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.intern002.locketapp.ads.AdManager
import com.intern002.locketapp.databinding.ActivityMainBinding
import com.intern002.locketapp.ui.screen.language.LanguageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        AdManager.prefetchNativeAd(applicationContext)
        AdManager.prefetchInterstitialAd(applicationContext)

        AdManager.prefetchNativeAdSmall(applicationContext)
    }
}
