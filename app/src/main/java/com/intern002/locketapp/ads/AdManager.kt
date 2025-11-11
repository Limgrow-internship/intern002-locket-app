package com.intern002.locketapp.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    var nativeAdFull: NativeAd? = null
        private set
    var interstitialAd: InterstitialAd? = null
        private set

    private const val NATIVE_SMALL_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110" // ID Native nhỏ/Banner (Có thể dùng lại ID test của bạn, nhưng nên dùng ID thật khác)
    private const val FULL_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110" // Native Test ID
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // Interstitial Test ID

    fun prefetchNativeAd(context: Context) {
        if (nativeAdFull != null) {
            Log.d("AdManager", "Native Ad Full already preloaded.")
            return
        }
        val adLoader = AdLoader.Builder(context, FULL_AD_UNIT_ID)
            .forNativeAd { ad: NativeAd ->
                Log.d("AdManager", "Native Ad Full preloaded successfully.")
                nativeAdFull?.destroy()
                nativeAdFull = ad
            }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdManager", "Native Ad Full failed to load: ${adError.message}")
                    nativeAdFull = null
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun prefetchInterstitialAd(context: Context) {
        if (interstitialAd != null) {
            Log.d("AdManager", "Interstitial Ad already preloaded.")
            return
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdManager", "Interstitial ad failed to load: ${adError.message}")
                    interstitialAd = null
                }
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("AdManager", "Interstitial Ad preloaded successfully.")
                    interstitialAd = ad
                }
            }
        )
    }

    var nativeAdSmall: NativeAd? = null
        private set

    fun prefetchNativeAdSmall(context: Context) {
        if (nativeAdSmall != null) {
            return
        }
        val adLoader = AdLoader.Builder(context, NATIVE_SMALL_AD_UNIT_ID)
            .forNativeAd { ad: NativeAd ->
                Log.d("AdManager", "Native Ad Small preloaded successfully.")
                nativeAdSmall?.destroy()
                nativeAdSmall = ad
            }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdManager", "Native Ad Small failed to load: ${adError.message}")
                    nativeAdSmall = null
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun destroyNativeAdSmall() {
        nativeAdSmall?.destroy()
        nativeAdSmall = null
    }

    fun showInterstitialAd(
        activity: Activity,
        onAdClosed: () -> Unit
    ) {
        val ad = interstitialAd
        if (ad == null) {
            Log.d("AdManager", "Interstitial Ad not ready. Skipping ad.")
            onAdClosed.invoke()
            prefetchInterstitialAd(activity.applicationContext)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdManager", "Ad was dismissed. Calling navigation.")
                interstitialAd = null
                onAdClosed.invoke()
                prefetchInterstitialAd(activity.applicationContext)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("AdManager", "Ad failed to show.")
                interstitialAd = null
                onAdClosed.invoke()
                prefetchInterstitialAd(activity.applicationContext)
            }
        }

        ad.show(activity)
    }

    fun destroyNativeAd() {
        Log.d("AdManager", "Destroying current Native Ad only.")
        nativeAdFull?.destroy()
        nativeAdFull = null
    }

    fun destroyAllAds() {
        Log.d("AdManager", "Destroying all current ads.")
        nativeAdFull?.destroy()
        nativeAdFull = null
        nativeAdSmall?.destroy()
        nativeAdSmall = null
        interstitialAd = null
    }
}