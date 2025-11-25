package com.intern002.locketapp.ui.screen.auth.login

import android.app.Activity
import android.content.IntentSender
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
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentLoginBinding
import com.intern002.locketapp.ui.viewmodel.login.LoginState
import com.intern002.locketapp.ui.viewmodel.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var firebaseAuth: FirebaseAuth

    private val oneTapSignInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val googleIdToken = credential.googleIdToken
                if (googleIdToken != null) {
                    handleGoogleIdToken(googleIdToken)
                } else {
                    Log.e("LoginFragment", "Google ID Token was null.")
                    viewModel.onGoogleLoginResult(null)
                }
            } catch (e: ApiException) {
                Log.e("LoginFragment", "Google Sign-In failed with ApiException", e)
                viewModel.onGoogleLoginResult(null)
            }
        }
    }

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

        firebaseAuth = FirebaseAuth.getInstance()

        setupGoogleOneTap()
        setupClickListeners()
        setupLoginLink()
        observeLoginState()
    }

    private fun handleGoogleIdToken(googleIdToken: String) {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                firebaseUser?.getIdToken(true)
                    ?.addOnSuccessListener { result ->
                        val firebaseIdToken = result.token
                        if (firebaseIdToken != null) {
                            viewModel.onGoogleLoginResult(firebaseIdToken)
                        } else {
                            Log.e("LoginFragment", "Firebase ID Token was null.")
                            viewModel.onGoogleLoginResult(null)
                        }
                    }
                    ?.addOnFailureListener { e ->
                        Log.e("LoginFragment", "Failed to get Firebase ID Token", e)
                        viewModel.onGoogleLoginResult(null)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("LoginFragment", "Firebase Sign-In failed", e)
                viewModel.onGoogleLoginResult(null)
            }
    }

    private fun setupGoogleOneTap() {
        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    private fun setupClickListeners() {
        binding.buttonGoogle.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        oneTapSignInLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("LoginFragment", "Couldn't start One Tap UI", e)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LoginFragment", "Google One Tap sign-in failed", e)
                }
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

        when (state) {
            is LoginState.Success -> {
                findNavController().navigate(R.id.action_loginFragment_to_mainContainerFragment)
            }
            is LoginState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is LoginState.RegistrationRequired -> {
                val action = LoginFragmentDirections.actionLoginFragmentToUsernameFragment(
                    email = state.email ?: "",
                    idToken = state.idToken,
                    suggestedUsername = state.suggestedUsername,
                    password = null
                )
                findNavController().navigate(action)
            }
            is LoginState.Idle, is LoginState.Loading -> {}
        }
    }

    private fun setupLoginLink() {
        val fullText = getString(R.string.login_prompt_full)
        val loginText = getString(R.string.login_action_text)
        val signupText = getString(R.string.signup_action_text)

        val spannable = SpannableString(fullText)

        // Clickable span for "Log in"
        val loginStart = fullText.indexOf(loginText)
        if (loginStart != -1) {
            val loginEnd = loginStart + loginText.length
            val loginClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    findNavController().navigate(R.id.action_LoginFragment_to_LoginEmailFragment)
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                }
            }
            spannable.setSpan(loginClickableSpan, loginStart, loginEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(StyleSpan(Typeface.BOLD), loginStart, loginEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val highlightColor = ContextCompat.getColor(requireContext(), R.color.white)
            spannable.setSpan(ForegroundColorSpan(highlightColor), loginStart, loginEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val signupStart = fullText.indexOf(signupText)
        if (signupStart != -1) {
            val signupEnd = signupStart + signupText.length
            val signupClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    findNavController().navigate(R.id.action_LoginFragment_to_SignupEmailFragment)
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                }
            }
            spannable.setSpan(signupClickableSpan, signupStart, signupEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(StyleSpan(Typeface.BOLD), signupStart, signupEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val highlightColor = ContextCompat.getColor(requireContext(), R.color.white)
            spannable.setSpan(ForegroundColorSpan(highlightColor), signupStart, signupEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.textLoginLink.text = spannable
        binding.textLoginLink.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
