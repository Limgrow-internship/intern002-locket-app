package com.intern002.locketapp.ui.screen.language

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.Language
import com.intern002.locketapp.databinding.FragmentLanguageBinding
import com.intern002.locketapp.ui.viewmodel.LanguageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageFragment : Fragment(), LanguageAdapter.OnLanguageClickListener {

    private lateinit var binding: FragmentLanguageBinding
    private val viewModel: LanguageViewModel by viewModels()
    private lateinit var adapter: LanguageAdapter

    companion object {
        const val LANGUAGE_PREFS = "LanguagePrefs"
        const val SELECTED_LANGUAGE = "SelectedLanguage"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchListener()
        observeViewModel()

        restoreSavedLanguage()

        binding.btnGetStarted.setOnClickListener {
            val selectedCode = viewModel.selectedLanguageCode.value

            if (selectedCode != null) {
                val localeList = LocaleListCompat.forLanguageTags(selectedCode)
                AppCompatDelegate.setApplicationLocales(localeList)
                saveLanguage(selectedCode)
            }

            findNavController().navigate(R.id.action_languageFragment_to_nativeAdFragment)
        }
    }

    private fun saveLanguage(languageCode: String) {
        val prefs = requireActivity().getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
    }

    private fun setupRecyclerView() {
        adapter = LanguageAdapter(emptyList(), this)
        binding.rvLanguages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLanguages.adapter = adapter
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchQueryChanged(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun restoreSavedLanguage() {
        val prefs = requireActivity().getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE)
        val savedLanguageCode = prefs.getString(SELECTED_LANGUAGE, null)

        if (!savedLanguageCode.isNullOrBlank()) {
            viewModel.setSelectedLanguageCode(savedLanguageCode)

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.languages.collect { languages ->
                    val savedLanguage = languages.find { it.code == savedLanguageCode }
                    if (savedLanguage != null) {
                        updateSelectedLanguageUI(savedLanguage)
                    }
                }
            }
        }
    }

    private fun updateSelectedLanguageUI(language: Language) {
        binding.tvSelectedLanguage.text = language.name
        binding.tvSelectedLanguage.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.white)
        )

        Glide.with(requireContext())
            .load(language.flagUrl)
            .error(R.drawable.ic_flag_placeholder)
            .into(binding.ivSelectedFlag)

        binding.ivSelectedFlag.isVisible = true
        binding.ivCheck.isVisible = true
        binding.btnGetStarted.isEnabled = true
        binding.btnGetStarted.alpha = 1.0f
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.isLoading.collectLatest { isLoading ->
                    binding.progressBar.isVisible = isLoading
                    binding.rvLanguages.isVisible = !isLoading
                }
            }

            launch {
                viewModel.error.collectLatest { error ->
                    error?.let { }
                }
            }

            launch {
                viewModel.languages.collectLatest { langs ->
                    adapter.updateData(langs)
                    val query = viewModel.searchQuery.value
                    val hasResults = langs.isNotEmpty()
                    val isSearching = query.isNotBlank()
                    binding.rvLanguages.isVisible = hasResults
                    binding.tvNoResults.isVisible = !hasResults && isSearching

                    if (!hasResults && isSearching) {
                        binding.tvNoResults.text = getString(R.string.no_results_for, query)
                    } else if (!hasResults && !isSearching) {
                        binding.tvNoResults.isVisible = true
                        binding.tvNoResults.text = "No languages available"
                    }
                }
            }
        }
    }

    override fun onLanguageClick(language: Language) {
        binding.tvSelectedLanguage.text = language.name
        binding.tvSelectedLanguage.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        Glide.with(requireContext())
            .load(language.flagUrl)
            .error(R.drawable.ic_flag_placeholder)
            .into(binding.ivSelectedFlag)

        binding.ivSelectedFlag.isVisible = true
        binding.ivCheck.isVisible = true

        viewModel.setSelectedLanguageCode(language.code)

        binding.btnGetStarted.isEnabled = true
        binding.btnGetStarted.alpha = 1.0f
    }
}
