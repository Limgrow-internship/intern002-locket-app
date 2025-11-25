package com.intern002.locketapp.ui.screen.auth.signup

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
import com.intern002.locketapp.databinding.FragmentSignupEmailBinding
import com.intern002.locketapp.ui.viewmodel.EmailValidationState
import com.intern002.locketapp.ui.viewmodel.SignupEmailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupEmailFragment: Fragment() {
    private var _binding: FragmentSignupEmailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignupEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupEmailBinding.inflate(inflater,container,false)
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
                val isValid = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                binding.buttonContinue.isEnabled = isValid
                binding.buttonContinue.alpha = if (isValid) 1.0f else 0.5f

                if (!isValid && email.isNotEmpty()) {
                    binding.tvErrorMessage.isVisible = true
                } else {
                    binding.tvErrorMessage.isVisible = false
                }
                viewModel.resetState()
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
            viewModel.validateEmail(email)
        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validationState.collectLatest { state ->
                    binding.progressBar.isVisible = state is EmailValidationState.Loading
                    binding.buttonContinue.isEnabled = state !is EmailValidationState.Loading

                    when (state) {
                        is EmailValidationState.Valid -> {
                            val action = SignupEmailFragmentDirections.actionSignupEmailFragmentToSignupPasswordFragment(state.email)
                            findNavController().navigate(action)
                        }
                        is EmailValidationState.Invalid -> {
                            binding.tvErrorMessage.text = state.message
                            binding.tvErrorMessage.isVisible = true
                        }
                        is EmailValidationState.Exists -> {
                            binding.tvErrorMessage.text = state.message
                            binding.tvErrorMessage.isVisible = true
                        }
                        else -> {
                            binding.tvErrorMessage.isVisible = false
                        }
                    }
                }
            }
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
