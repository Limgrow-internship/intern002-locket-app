package com.intern002.locketapp.ui.screen.login

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.R
import androidx.navigation.fragment.findNavController
import com.intern002.locketapp.databinding.FragmentLoginEmailBinding
import com.intern002.locketapp.ui.viewmodel.EmailLoginState
import com.intern002.locketapp.ui.viewmodel.LoginEmailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginEmailFragment: Fragment() {
    private var _binding: FragmentLoginEmailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginEmailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupTermsLink()
        observeViewModelState()
    }

    private fun setupClickListeners() {
        //Quay về màn hình trước
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonContinue.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            viewModel.onContinueClicked(email)
        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loginState.collectLatest { state ->
                    handleLoginState(state)
                }
            }
        }
    }

    private fun handleLoginState(state: EmailLoginState) {
        binding.buttonContinue.isEnabled = state !is EmailLoginState.Loading

        when(state) {
             is EmailLoginState.Loading -> {
                 binding.emailLayout.error = null
                 binding.emailLayout.isErrorEnabled = false
             }
            is EmailLoginState.Success -> {
                //findNavController().navigate(R.id.action_loginEmailFragment_to_passwordFragment)
            }
            is EmailLoginState.Error -> {
                binding.emailLayout.error = state.message
            }
            is EmailLoginState.Idle -> {}
        }
    }

    private fun setupTermsLink() {

        val fullText = "By tapping Continue, you agree to our Terms of Service and Privacy Policy."
        val termText = "Terms of Service"
        val policyText = "Privacy Policy"

        val spannable = SpannableString(fullText)

        val termStart = fullText.indexOf(termText)
        val termEnd = termStart + termText.length
        val termClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(context, "Mở Terms", Toast.LENGTH_SHORT).show()
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
            }
        }
        spannable.setSpan(termClick, termStart, termEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.BOLD), termStart, termEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val policyStart = fullText.indexOf(policyText)
        val policyEnd = policyStart + policyText.length
        val policyClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // (Mở link webview/browser)
                Toast.makeText(context, "Mở Policy", Toast.LENGTH_SHORT).show()
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
            }
        }
        spannable.setSpan(policyClick, policyStart, policyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.BOLD), policyStart, policyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textTerms.text = spannable
        binding.textTerms.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Chống leak
    }
}