package com.intern002.locketapp.ui.screen.auth.login

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
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

        setupViews()
        setupClickListeners()
        setupTermsLink()
        observeViewModelState()
    }

    private fun setupViews() {
        binding.buttonContinue.isEnabled = false
        binding.buttonContinue.alpha = 0.5f
        binding.tvErrorMessage.isVisible = false

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                val isValid = email.isNotBlank() && email.endsWith("@gmail.com", ignoreCase = true)

                binding.buttonContinue.isEnabled = isValid
                binding.buttonContinue.alpha = if (isValid) 1.0f else 0.5f
                binding.tvErrorMessage.isVisible = !isValid && email.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
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
        val isLoading = state is EmailLoginState.Loading
        if (isLoading) {
            binding.buttonContinue.isEnabled = false
            binding.buttonContinue.alpha = 0.5f
            binding.tvErrorMessage.isVisible = false
        } else {
            val currentEmail = binding.emailEditText.text.toString().trim()
            val isValid = currentEmail.isNotBlank() && currentEmail.endsWith("@gmail.com", ignoreCase = true)
            binding.buttonContinue.isEnabled = isValid
            binding.buttonContinue.alpha = if (isValid) 1.0f else 0.5f
        }


        when(state) {
            is EmailLoginState.Loading -> {  }
            is EmailLoginState.Success -> {
                val email = binding.emailEditText.text.toString().trim()
                val action = LoginEmailFragmentDirections.actionLoginEmailFragmentToLoginPasswordFragment(email)
                findNavController().navigate(action)

                viewModel.resetState()
            }
            is EmailLoginState.Error -> {
                binding.tvErrorMessage.text = state.message
                binding.tvErrorMessage.isVisible = true
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
                Toast.makeText(context, "Open Terms", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Open Policy", Toast.LENGTH_SHORT).show()
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
        _binding = null
    }
}