package com.intern002.locketapp.ui.screen.onboarding

import android.os.Bundle
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

        binding.ivOnboarding.setImageResource(R.drawable.img_onboarding3)

        showNativeAdIfAvailable()

        AdManager.prefetchNativeAdSmall(requireContext().applicationContext)

        setupGetStartedButton()
        setupPreviousButton()

        binding.indicatorLayout.findViewById<TextView>(R.id.btnNext)?.text = getString(R.string.get_started)
    }

    private fun showNativeAdIfAvailable() {
        val nativeAd = AdManager.nativeAdSmall

        if (nativeAd != null) {
            val adView = layoutInflater.inflate(
                R.layout.layout_native_ad,
                binding.adPlaceholder,
                false
            ) as NativeAdView

            NativeAdBindingHelper.bindNativeAdInline(adView, nativeAd)

            adView.findViewById<android.widget.ImageView>(R.id.ad_close)?.setOnClickListener {
                binding.adPlaceholder.visibility = View.GONE
                AdManager.destroyNativeAdSmall()
            }

            binding.adPlaceholder.removeAllViews()
            binding.adPlaceholder.addView(adView)
            binding.adPlaceholder.visibility = View.VISIBLE
        } else {
            binding.adPlaceholder.visibility = View.GONE
        }
    }

    private fun navigateToHomeScreen() {
        AdManager.destroyNativeAdSmall()
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