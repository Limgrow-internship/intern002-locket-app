package com.intern002.locketapp.ui.screen.auth.welcome

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentUsernameBinding
import java.util.regex.Pattern

class UsernameFragment : Fragment() {

    private var _binding: FragmentUsernameBinding? = null
    private val binding get() = _binding!!

    private val USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinue.isEnabled = false
        binding.btnContinue.alpha = 0.5f

        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateUsername(s.toString().trim())
            }
        })

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_usernameFragment_to_birthdayFragment)
        }
    }

    private fun validateUsername(username: String) {
        binding.tvHelperText.visibility = View.GONE
        binding.tvErrorMessage.visibility = View.GONE
        binding.tvCorrectMessage.visibility = View.GONE

        if (username.isEmpty()) {
            binding.etUsername.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_selected)
            binding.tvHelperText.visibility = View.VISIBLE
            binding.btnContinue.isEnabled = false
            binding.btnContinue.alpha = 0.5f
            return
        }

        val isLengthValid = username.length in 7..15
        val hasSpecialChars = !USERNAME_PATTERN.matcher(username).matches()

        if (isLengthValid && !hasSpecialChars) {
            binding.etUsername.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_correct)
            binding.tvCorrectMessage.visibility = View.VISIBLE
            binding.tvErrorMessage.visibility = View.GONE
            binding.btnContinue.isEnabled = true
            binding.btnContinue.alpha = 1f
        } else {
            binding.etUsername.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_selected_error)
            binding.tvErrorMessage.visibility = View.VISIBLE
            binding.tvCorrectMessage.visibility = View.GONE
            binding.btnContinue.isEnabled = false
            binding.btnContinue.alpha = 0.5f

            when {
                hasSpecialChars -> binding.tvErrorMessage.text = "Sorry, no special characters allowed."
                username.length < 7 -> binding.tvErrorMessage.text = "Username must be at least 7 characters."
                username.length > 15 -> binding.tvErrorMessage.text = "Username cannot exceed 15 characters."
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}