package com.intern002.locketapp.ui.screen.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.Reactor
import com.intern002.locketapp.databinding.BottomSheetReactionsBinding
import com.intern002.locketapp.ui.adapter.ReactionAdapter

class ReactionsBottomSheetFragment(
    private val reactors: List<Reactor>
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetReactionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetReactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = "Reactions (${reactors.size})"

        val adapter = ReactionAdapter(reactors)
        binding.rvReactions.adapter = adapter

        binding.btnClose.setOnClickListener { dismiss() }
    }

    override fun getTheme(): Int =
        R.style.Theme_KeepinWidget_BottomSheetDialog

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}