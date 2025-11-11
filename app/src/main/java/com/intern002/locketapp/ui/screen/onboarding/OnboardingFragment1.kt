package com.intern002.locketapp.ui.screen.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.R
import com.intern002.locketapp.ads.AdManager
import com.intern002.locketapp.ads.NativeAdBindingHelper
import com.intern002.locketapp.databinding.FragmentOnboarding1Binding
import com.google.android.gms.ads.nativead.NativeAdView
import android.widget.FrameLayout

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

        loadAndDisplayNativeAd()

        setupNextButton()
    }

    private fun loadAndDisplayNativeAd() {
        val adContainer = binding.adPlaceholder
        val nativeAd = AdManager.nativeAdFull

        if (nativeAd != null) {
            adContainer.visibility = View.VISIBLE

            NativeAdBindingHelper.bindNativeAdFull(adContainer, nativeAd) { adView: NativeAdView ->
                // Tùy chọn: thiết lập các sự kiện trên AdView nếu cần
            }

            AdManager.prefetchNativeAd(requireContext().applicationContext)
        } else {
            // Ẩn FrameLayout nếu không có quảng cáo (hoặc thử tải lại)
            adContainer.visibility = View.GONE
            AdManager.prefetchNativeAd(requireContext().applicationContext)
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