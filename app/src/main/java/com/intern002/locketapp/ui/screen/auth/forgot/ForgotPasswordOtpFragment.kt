package com.intern002.locketapp.ui.screen.auth.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.intern002.locketapp.databinding.FragmentForgotPasswordOtpBinding
import com.intern002.locketapp.ui.viewmodel.login.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordOtpFragment : Fragment() {
    private var _binding: FragmentForgotPasswordOtpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForgotPasswordViewModel by viewModels()
    private val args: ForgotPasswordOtpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = args.email
        binding.tvSubtitle.text = "We sent a code to $email"

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnVerify.setOnClickListener {
            val otp = binding.edtOtp.text.toString().trim()

            if (otp.length < 6) {
                binding.tvErrorMessage.text = "Mã OTP phải đủ 6 số"
                binding.tvErrorMessage.isVisible = true
                return@setOnClickListener
            }

            viewModel.verifyOtp(email, otp) {
                val action = ForgotPasswordOtpFragmentDirections
                    .actionForgotPasswordOtpFragmentToResetPasswordFragment(email, otp)
                findNavController().navigate(action)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.isVisible = isLoading
                binding.btnVerify.isEnabled = !isLoading
                if (isLoading) binding.tvErrorMessage.isVisible = false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorEvent.collect { msg ->
                binding.tvErrorMessage.text = msg
                binding.tvErrorMessage.isVisible = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}