package com.intern002.locketapp.ui.screen.auth.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentLoginPasswordBinding
import com.intern002.locketapp.ui.viewmodel.login.PasswordLoginState
import com.intern002.locketapp.ui.viewmodel.login.LoginPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

@AndroidEntryPoint
class LoginPasswordFragment : Fragment() {

    private var _binding: FragmentLoginPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginPasswordViewModel by viewModels()
    private val args: LoginPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupKeyboardAdjustment()

        setupViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupKeyboardAdjustment() {
        // This is a simpler and more robust way to handle the keyboard
        // It adjusts the bottom padding of the root view, pushing all content up
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, insets.bottom)
            windowInsets
        }
    }

    private fun setupViews() {
        binding.buttonContinue.isEnabled = false
        binding.tvErrorMessage.isVisible = false
        binding.tvCorrectMessage.isVisible = false

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString().trim()
                val passLength = password.length

                if (password.isEmpty()) {
                    binding.tvErrorMessage.isVisible = false
                    binding.tvCorrectMessage.isVisible = false
                    binding.buttonContinue.isEnabled = false
                    return
                }

                val isValid = passLength >= 8

                binding.buttonContinue.isEnabled = isValid
                binding.tvCorrectMessage.isVisible = isValid
                binding.tvErrorMessage.isVisible = !isValid

                if (!isValid) {
                    binding.tvErrorMessage.text = getString(R.string.password_length_error)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonContinue.setOnClickListener {
            val password = binding.passwordEditText.text.toString().trim()
            viewModel.onContinueClicked(args.email, password)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    handlePasswordLoginState(state)
                }
            }
        }
    }

    private fun handlePasswordLoginState(state: PasswordLoginState) {
        binding.progressBar.isVisible = state is PasswordLoginState.Loading
        binding.buttonContinue.isEnabled = state !is PasswordLoginState.Loading

        when (state) {
            is PasswordLoginState.Success -> {
                val action = LoginPasswordFragmentDirections.actionLoginPasswordFragmentToMainContainerFragment()
                findNavController().navigate(action)
            }
            is PasswordLoginState.Error -> {
                showErrorDialog(state.message)
            }
            else -> { }
        }
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Login Failed")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}