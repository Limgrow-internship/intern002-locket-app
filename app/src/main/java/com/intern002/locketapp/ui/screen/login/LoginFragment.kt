package com.intern002.locketapp.ui.screen.login

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentLoginBinding
import com.intern002.locketapp.ui.viewmodel.LoginState
import com.intern002.locketapp.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val  binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupLoginLink()
        observeLoginState()

    }

    private fun setupClickListeners() {
        binding.buttonGoogle.setOnClickListener{
            viewModel.onGoogleLoginClicked()
        }
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest { state ->
                    handleLoginState(state)
                }
            }
        }
    }

    private fun handleLoginState(state: LoginState) {
        binding.progressBar.isVisible = state is LoginState.Loading

        val isEnabled = state !is LoginState.Loading
        binding.buttonGoogle.isEnabled = isEnabled
        binding.buttonFacebook.isEnabled = isEnabled

        when(state) {
            is LoginState.Success -> {
                Toast.makeText(context, "Enter Your Email", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_loginEmailFragment)
            }
            is LoginState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is LoginState.Idle, is LoginState.Loading -> {}
        }
    }

    private fun setupLoginLink() {
        val fullText = getString(R.string.login_prompt_full)
        val loginText = getString(R.string.login_action_text)// SỬA TEXT
        val start = fullText.indexOf(loginText)
        if (start == -1) {
            Log.e("LoginFragment", "LỖI: Không tìm thấy '$loginText' trong '$fullText'")
            return
        }
        val end = start + loginText.length

        val spannable = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_loginEmailFragment)
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val highlightColor = ContextCompat.getColor(requireContext(), R.color.white)
        spannable.setSpan(ForegroundColorSpan(highlightColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textLoginLink.text = spannable
        binding.textLoginLink.movementMethod = LinkMovementMethod.getInstance()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        //Chống memory leak
        _binding = null
    }
}