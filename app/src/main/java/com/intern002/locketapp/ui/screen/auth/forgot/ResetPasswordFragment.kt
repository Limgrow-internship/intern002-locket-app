package com.intern002.locketapp.ui.screen.auth.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentResetPasswordBinding
import com.intern002.locketapp.ui.viewmodel.login.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForgotPasswordViewModel by viewModels()
    private val args: ResetPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnConfirm.setOnClickListener {
            val newPass = binding.edtNewPass.text.toString().trim()
            val confirmPass = binding.edtConfirmPass.text.toString().trim()

            if (newPass.length < 8) {
                showError("Mật khẩu phải từ 8 ký tự trở lên")
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                showError("Mật khẩu nhập lại không khớp!")
                return@setOnClickListener
            }

            viewModel.resetPassword(args.email, args.otp, newPass) {
                Toast.makeText(
                    context,
                    "Đổi mật khẩu thành công! Hãy đăng nhập lại.",
                    Toast.LENGTH_LONG
                ).show()

                findNavController().navigate(R.id.action_ResetPasswordFragment_to_LoginEmailFragment)
            }
        }

        observeViewModel()
    }

    private fun showError(msg: String) {
        binding.tvErrorMessage.text = msg
        binding.tvErrorMessage.isVisible = true
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.isVisible = isLoading
                binding.btnConfirm.isEnabled = !isLoading
                if (isLoading) binding.tvErrorMessage.isVisible = false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorEvent.collect { msg ->
                showError(msg)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}