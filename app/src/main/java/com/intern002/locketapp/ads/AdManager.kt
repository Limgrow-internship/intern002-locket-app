package com.intern002.locketapp.ads

import android.app.Activity
import android.content.Context
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

    private const val NATIVE_SMALL_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    private const val FULL_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    fun prefetchNativeAd(context: Context) {
        if (nativeAdFull != null) {
            return
        }
        val adLoader = AdLoader.Builder(context, FULL_AD_UNIT_ID)
            .forNativeAd { ad: NativeAd ->
                nativeAdFull?.destroy()
                nativeAdFull = ad
            }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    nativeAdFull = null
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun prefetchInterstitialAd(context: Context) {
        if (interstitialAd != null) {
            return
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
                override fun onAdLoaded(ad: InterstitialAd) {
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
                nativeAdSmall?.destroy()
                nativeAdSmall = ad
            }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
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
            onAdClosed.invoke()
            prefetchInterstitialAd(activity.applicationContext)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onAdClosed.invoke()
                prefetchInterstitialAd(activity.applicationContext)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                onAdClosed.invoke()
                prefetchInterstitialAd(activity.applicationContext)
            }
        }

        ad.show(activity)
    }

    fun destroyNativeAd() {
        nativeAdFull?.destroy()
        nativeAdFull = null
    }

    fun destroyAllAds() {
        nativeAdFull?.destroy()
        nativeAdFull = null
        nativeAdSmall?.destroy()
        nativeAdSmall = null
        interstitialAd = null
    }
}