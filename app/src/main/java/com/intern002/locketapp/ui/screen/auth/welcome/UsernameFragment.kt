package com.intern002.locketapp.ui.screen.auth.welcome

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.intern002.locketapp.databinding.FragmentUsernameBinding

class UsernameFragment : Fragment() {

    private var _binding: FragmentUsernameBinding? = null
    private val binding get() = _binding!!

    private val args: UsernameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        binding.btnContinue.isEnabled = false
        args.suggestedUsername?.let {
            binding.etUsername.setText(it)
        }

        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnContinue.isEnabled = s.toString().trim().isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnContinue.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val action = UsernameFragmentDirections.actionUsernameFragmentToBirthdayFragment(
                email = args.email,
                username = username,
                idToken = args.idToken,
                password = args.password
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
