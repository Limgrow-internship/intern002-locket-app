package com.intern002.locketapp.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.intern002.locketapp.databinding.FragmentOnboardingStepBinding
class OnboardingStepFragment: Fragment() {
    private var _binding: FragmentOnboardingStepBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_IMAGE_RES_ID = "image_res_id"
        private const val ARG_TITLE = "title"
        private const val ARG_SUBTITLE = "subtitle"
        private const val ARG_SHOW_AD = "show_ad"

        fun newInstance(
            @DrawableRes imageResId: Int,
            title: String,
            subtitle: String,
            showAd: Boolean
        ): OnboardingStepFragment {
            return OnboardingStepFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE_RES_ID, imageResId)
                    putString(ARG_TITLE, title)
                    putString(ARG_SUBTITLE, subtitle)
                    putBoolean(ARG_SHOW_AD, showAd)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imagesRedId = arguments?.getInt(ARG_IMAGE_RES_ID) ?: 0
        val title = arguments?.getString(ARG_TITLE) ?: ""
        val subtitle = arguments?.getString(ARG_SUBTITLE) ?: ""
        val showAd = arguments?.getBoolean(ARG_SHOW_AD) ?: false

        if(imagesRedId != 0) {
            binding.imgBackground.setImageResource(imagesRedId)
        }
        binding.tvTitle.text = title
        binding.tvSubtitle.text = subtitle

        if(showAd) {
            binding.adPlaceholder.visibility = View.VISIBLE
        }else {
            binding.adPlaceholder.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}