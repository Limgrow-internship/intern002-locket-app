package com.intern002.locketapp.ui.screen.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.intern002.locketapp.databinding.ActivitySplashBinding
import com.intern002.locketapp.ui.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var rootAnimator: AnimatorSet? = null
    private var mInterstitialAd: InterstitialAd? = null
    private val TAG = "SplashActivity"

    private val dots by lazy {
        listOf(
            binding.dot1,
            binding.dot2,
            binding.dot3,
            binding.dot4,
            binding.dot5
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startJumpingAnimation()
        startAdLoading()
    }

    private fun startJumpingAnimation() {
        val jumpHeight = 30f
        val jumpDuration = 400L

        val animators = dots.map { dot ->
            ObjectAnimator.ofFloat(dot, "translationY", 0f, -jumpHeight, 0f).apply {
                duration = jumpDuration
                interpolator = AccelerateDecelerateInterpolator()
            }
        }

        rootAnimator = AnimatorSet().apply {
            playSequentially(animators)

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animation.start()
                }
            })
        }

        rootAnimator?.start()
    }

    private fun startAdLoading() {
        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                mInterstitialAd = null
                navigateToMain()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
                showInterstitialAd()
            }
        })
    }

    private fun showInterstitialAd() {
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
                navigateToMain()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(TAG, "Ad failed to show.")
                navigateToMain()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                mInterstitialAd = null
            }
        }

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        rootAnimator?.cancel()
        super.onDestroy()
    }
}
