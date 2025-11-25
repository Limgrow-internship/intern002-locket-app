package com.intern002.locketapp.ui.screen.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.BottomSheetShareBinding
import com.intern002.locketapp.databinding.ItemShareIconBinding

class ShareBottomSheetFragment(
    private val onDeleteClick: () -> Unit,
    private val onSaveClick: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetShareBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupShareIcons()

        binding.buttonClose.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener {
            onSaveClick()
            dismiss()
        }
        binding.btnDelete.setOnClickListener {
            onDeleteClick()
            dismiss()
        }
    }

    private fun setupShareIcons() {
        val bindSystem = ItemShareIconBinding.bind(binding.itemShareSystem.root)
        bindSystem.tvName.text = "Share"
        bindSystem.imgIcon.setImageResource(R.drawable.ic_share) // Icon mũi tên cong
        bindSystem.root.setOnClickListener {
            shareText("Check out this photo on HushCam!")
        }

        // 2. Messenger
        val bindMess = ItemShareIconBinding.bind(binding.itemShareMessenger.root)
        bindMess.tvName.text = "Messenger"
        bindMess.imgIcon.setImageResource(R.drawable.ic_messenger) // Anh tự thêm icon
        bindMess.root.setOnClickListener {
            shareToPackage("com.facebook.orca")
        }

        // 3. Instagram
        val bindInsta = ItemShareIconBinding.bind(binding.itemShareInstagram.root)
        bindInsta.tvName.text = "Instagram"
        bindInsta.imgIcon.setImageResource(R.drawable.ic_instagram)
        bindInsta.root.setOnClickListener {
            shareToPackage("com.instagram.android")
        }

        // 4. Twitter
        val bindTwitter = ItemShareIconBinding.bind(binding.itemShareTwitter.root)
        bindTwitter.tvName.text = "Twitter"
        bindTwitter.imgIcon.setImageResource(R.drawable.ic_twitter)
        bindTwitter.root.setOnClickListener {
            shareToPackage("com.twitter.android")
        }
    }

    private fun shareText(content: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    // Hàm share đích danh app (nếu có cài)
    private fun shareToPackage(packageName: String) {
        val intent = requireContext().packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(context, "App not installed!", Toast.LENGTH_SHORT).show()
        }
    }

    // Fix nền trong suốt để thấy bo góc
    override fun getTheme(): Int = R.style.Theme_KeepinWidget_BottomSheetDialog

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}