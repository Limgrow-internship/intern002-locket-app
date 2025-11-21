package com.intern002.locketapp.ui.screen.auth.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentSignupPasswordBinding

class SignupPasswordFragment : Fragment() {

    private var _binding: FragmentSignupPasswordBinding? = null
    private val binding get() = _binding!!

    private val args: SignupPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
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

                val isValid = passLength in 8..18

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
            val action = SignupPasswordFragmentDirections.actionSignupPasswordFragmentToUsernameFragment(
                email = args.email,
                password = password,
                idToken = null, // Not used in email signup flow
                suggestedUsername = null // Not used in email signup flow
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
