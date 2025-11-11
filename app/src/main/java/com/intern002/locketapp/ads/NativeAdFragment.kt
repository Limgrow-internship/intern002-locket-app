package com.intern002.locketapp.ads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.nativead.NativeAdView
import com.intern002.locketapp.R

class NativeAdFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_native_ad_full, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adContainer = view.findViewById<ViewGroup>(R.id.nativeAdContainer)
        val nativeAd = AdManager.nativeAdFull

        val navigateToOnboarding = {
            if (isAdded) {
                findNavController().navigate(R.id.action_nativeAdFragment_to_onboardingFlow)
            }
        }

        if (nativeAd != null && adContainer != null) {
            NativeAdBindingHelper.bindNativeAdFull(adContainer, nativeAd) { adView: NativeAdView ->
                val closeAction = {
                    AdManager.destroyNativeAd()
                    navigateToOnboarding()
                    AdManager.prefetchNativeAd(requireContext().applicationContext)
                }

                adView.findViewById<ImageView>(R.id.ad_close)?.setOnClickListener { closeAction() }
                adView.findViewById<ImageView>(R.id.ad_close_1)?.setOnClickListener { closeAction() }
            }
        } else {
            navigateToOnboarding()
        }
    }
}
