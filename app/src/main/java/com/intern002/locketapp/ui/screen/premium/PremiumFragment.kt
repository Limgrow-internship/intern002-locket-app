package com.intern002.locketapp.ui.screen.premium

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.databinding.FragmentPremiumBinding

class PremiumFragment : Fragment() {

    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val strokeWidthInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            resources.displayMetrics
        ).toInt()

        binding.cardMonthly.setOnClickListener {
            binding.cardMonthly.strokeWidth = strokeWidthInPixels
            binding.cardYearly.strokeWidth = 0
        }

        binding.cardYearly.setOnClickListener {
            binding.cardYearly.strokeWidth = strokeWidthInPixels
            binding.cardMonthly.strokeWidth = 0
        }

        binding.unlockButton.setOnClickListener {
            val welcomeDialog = WelcomePremiumDialog()
            welcomeDialog.show(childFragmentManager, "WelcomePremiumDialog")
        }

        binding.noThanksText.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
