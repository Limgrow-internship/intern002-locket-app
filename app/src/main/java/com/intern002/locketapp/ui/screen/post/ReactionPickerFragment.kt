package com.intern002.locketapp.ui.screen.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.reaction.ReactionTypeResponse
import com.intern002.locketapp.databinding.BottomSheetReactionPickerBinding

class ReactionPickerFragment(
    private val allReactions: List<ReactionTypeResponse>,
    private val onReactionSelected: (ReactionTypeResponse) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetReactionPickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetReactionPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ReactionGridAdapter(allReactions) { selectedReaction ->
            onReactionSelected(selectedReaction)
            dismiss()
        }
        binding.rvAllReactions.adapter = adapter
    }

    override fun getTheme(): Int = R.style.Theme_KeepinWidget_BottomSheetDialog
    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}