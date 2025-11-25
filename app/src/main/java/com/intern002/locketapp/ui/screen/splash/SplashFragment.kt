package com.intern002.locketapp.ui.screen.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.R
import com.intern002.locketapp.ads.AdManager
import com.intern002.locketapp.databinding.FragmentSplashBinding
import com.intern002.locketapp.ui.viewmodel.splash.SplashState
import com.intern002.locketapp.ui.viewmodel.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SplashViewModel by viewModels()

    private var rootAnimator: AnimatorSet? = null
    private val dots by lazy {
        listOf(
            binding.dot1,
            binding.dot2,
            binding.dot3,
            binding.dot4,
            binding.dot5
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startJumpingAnimation()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.splashState.collect { state ->
                    if (view?.isAttachedToWindow == true) {
                        when (state) {
                            is SplashState.Authenticated -> {
                                findNavController().navigate(R.id.action_splashFragment_to_mainContainerFragment)
                            }
                            is SplashState.Unauthenticated -> {
                                showInterstitialAd()
                            }
                            is SplashState.Loading -> {
                                // Animation is running while in this state
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showInterstitialAd() {
        val navigateNext: () -> Unit = {
            activity?.runOnUiThread {
                navigateToLanguageScreen()
            }
        }

        binding.root.visibility = View.INVISIBLE

        AdManager.showInterstitialAd(requireActivity(), navigateNext)
    }

    private fun navigateToLanguageScreen() {
        if (isAdded) {
            findNavController().navigate(R.id.action_splashFragment_to_languageFragment)
        }
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

    override fun onDestroyView() {
        rootAnimator?.cancel()
        _binding = null
        super.onDestroyView()
    }
}
