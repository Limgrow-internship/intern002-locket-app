package com.intern002.locketapp.ui.screen.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.nativead.NativeAdView
import com.intern002.locketapp.R
import com.intern002.locketapp.ads.AdManager
import com.intern002.locketapp.ads.NativeAdBindingHelper
import com.intern002.locketapp.databinding.FragmentOnboarding1Binding

class OnboardingFragment1 : Fragment() {

    private var _binding: FragmentOnboarding1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBackground.setImageResource(R.drawable.img_onboarding1)

        showNativeAdIfAvailable()

        AdManager.prefetchNativeAdSmall(requireContext().applicationContext)

        setupNextButton()
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

            binding.adPlaceholder.removeAllViews()
            binding.adPlaceholder.addView(adView)
            binding.adPlaceholder.visibility = View.VISIBLE
        } else {
            binding.adPlaceholder.visibility = View.GONE
        }
    }

    private fun setupNextButton() {
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_onboarding1_to_onboarding2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}