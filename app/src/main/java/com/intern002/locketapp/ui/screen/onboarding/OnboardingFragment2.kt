package com.intern002.locketapp.ui.screen.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentOnboarding2Binding

class OnboardingFragment2 : Fragment() {

    // Khai báo View Binding
    private var _binding: FragmentOnboarding2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNextButton()

//        setupSkipButton()
    }

    private fun setupNextButton() {
        //  nằm trong indicatorLayout của fragment_onboarding_2.xml
        binding.indicatorLayout.findViewById<View>(R.id.btnNext).setOnClickListener {
            findNavController().navigate(R.id.action_onboarding2_to_onboarding3)
        }
    }

//    private fun setupSkipButton() {
//        binding.indicatorLayout.findViewById<View>(R.id.btnSkip).setOnClickListener {
//
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}