package com.intern002.locketapp.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentOnboardingContainerBinding

data class OnboardingPage(
    val onboardingLabel: String,
    val imageResId: Int,
    val title: String,
    val subtitle: String,
    val showAd: Boolean
)
class OnboardingContainerFragment: Fragment() {
    private var _binding: FragmentOnboardingContainerBinding? = null
    private val binding get() = _binding!!

    private val onBoardingPages = listOf(
        OnboardingPage(
            onboardingLabel = "Onboarding 1",
            imageResId = R.drawable.img_onboarding1,
            title = "Welcome to HushCam 💞",
            subtitle = "Share real time photos with your favorite people — right on your home screen.",
            showAd = false
        ),
        OnboardingPage(
            onboardingLabel = "On board 2",
            imageResId = R.drawable.img_onboarding2,
            title = "Closer Than You Think 💫",
            subtitle = "Even miles apart, a single moment can make hearts feel near.",
            showAd = false
        ),
        OnboardingPage(
            onboardingLabel = "On board 3",
            imageResId = R.drawable.img_onboarding3,
            title = "Moments That Speak",
            subtitle = "Send a quick message or emoji to keep the moment alive and fun.",
            showAd = false
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPagerOnboarding.adapter = OnboardingPagerAdapter(this, onBoardingPages)

        setupIndicators()

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
                updateNextButtonText(position)
            }
        })

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPagerOnboarding.currentItem
            if (currentItem < onBoardingPages.size - 1) {
                binding.viewPagerOnboarding.currentItem = currentItem + 1
            } else {
               // Navigate to orther fragment
            }
        }
        binding.btnSkip.setOnClickListener {
            // Navigate to orther fragment

        }

        updateNextButtonText(0)
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onBoardingPages.size)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in onBoardingPages.indices) {
            indicators[i] = ImageView(requireContext()).apply {
                this.layoutParams = layoutParams
                this.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive))
            }
            binding.layoutIndicators.addView(indicators[i])
        }
    }

    private fun updateIndicators(position: Int) {
        val childCount = binding.layoutIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive))
            }
        }
    }

    private fun updateNextButtonText(position: Int) {
        if (position == onBoardingPages.size - 1) {
            binding.btnNext.text = "Get Started"
        } else {
            binding.btnNext.text = "Next"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class OnboardingPagerAdapter(
    fragment: Fragment,
    private val pages: List<OnboardingPage>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        val page = pages[position]
        return OnboardingStepFragment.newInstance(
            page.imageResId,
            page.title,
            page.subtitle,
            page.showAd
        )
    }
}