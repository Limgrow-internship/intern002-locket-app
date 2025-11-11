package com.intern002.locketapp.ui.screen.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.nativead.NativeAdView
import com.intern002.locketapp.R
import com.intern002.locketapp.ads.AdManager
import com.intern002.locketapp.ads.NativeAdBindingHelper
import com.intern002.locketapp.databinding.FragmentOnboarding3Binding

class OnboardingFragment3 : Fragment() {

    private var _binding: FragmentOnboarding3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayNativeAd()
        setupGetStartedButton()
        setupPreviousButton()

        binding.indicatorLayout.findViewById<TextView>(R.id.btnNext)?.text = "GET STARTED"
    }

    private fun displayNativeAd() {
        val nativeAd = AdManager.nativeAdFull
        val adPlaceholder = binding.adPlaceholder

        if (nativeAd != null) {
            Log.d("OnboardingFragment3", "Displaying Native Ad Full...")
            adPlaceholder.visibility = View.VISIBLE
            NativeAdBindingHelper.bindNativeAdFull(adPlaceholder, nativeAd) { adView: NativeAdView ->
                // Optional: set up events on the AdView if needed
            }

            AdManager.prefetchNativeAd(requireContext().applicationContext)
        } else {
            Log.d("OnboardingFragment3", "Native Ad Full not ready. Hiding placeholder.")
            adPlaceholder.visibility = View.GONE
            AdManager.prefetchNativeAd(requireContext().applicationContext)
        }
    }

    private fun navigateToHomeScreen() {
        AdManager.destroyNativeAd()
        if (isAdded) {
            // findNavController().navigate(R.id.action_onboarding3_to_homeScreen)
        }
    }

    private fun setupGetStartedButton() {
        binding.indicatorLayout.findViewById<View>(R.id.btnNext).setOnClickListener {
            navigateToHomeScreen()
        }
    }

    private fun setupPreviousButton() {
        binding.indicatorLayout.findViewById<View>(R.id.btnPre)?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
