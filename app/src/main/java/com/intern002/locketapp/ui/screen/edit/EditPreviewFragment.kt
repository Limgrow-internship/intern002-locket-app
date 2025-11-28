package com.intern002.locketapp.ui.screen.edit

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentEditPreviewBinding
import com.intern002.locketapp.utils.MediaSaver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditPreviewFragment : Fragment(R.layout.fragment_edit_preview) {
    private var _binding: FragmentEditPreviewBinding? = null
    private val binding get() = _binding!!

    private val args: EditPreviewFragmentArgs by navArgs()

    private val viewModel: EditPreviewViewModel by viewModels()

    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaUri = args.mediaUri.toUri()
        val isVideo = args.isVideo

        if (isVideo) {
            setupVideo(mediaUri)
        } else {
            setupImage(mediaUri)
        }

        setupButtons(mediaUri, isVideo)
        setupFriendsList()
        setupKeyboardHandling()
        setupEditTextAction()

        binding.imagePreview.setOnClickListener {
            val imm =
                requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

        observeViewModel()

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.sendState.collect { state ->
                    when (state) {
                        is SendState.Loading -> {
                            binding.buttonSend.isEnabled = false
                            binding.buttonSend.alpha = 0.5f
                            Toast.makeText(context, "Đang gửi ảnh...", Toast.LENGTH_SHORT).show()
                        }

                        is SendState.Success -> {
                            binding.buttonSend.isEnabled = true
                            binding.buttonSend.alpha = 1f
                            Toast.makeText(context, "Gửi thành công!", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }

                        is SendState.Error -> {
                            binding.buttonSend.isEnabled = true
                            binding.buttonSend.alpha = 1f
                            Toast.makeText(context, "Lỗi: ${state.message}", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {} // Idle
                    }
                }
            }
        }
    }

    private fun setupFriendsList() {
        val mockData = arrayListOf(
            FriendItem(0, "All", isSelected = true, isAllButton = true),
            FriendItem(1, "Minh", avatarUrl = "https://i.pravatar.cc/150?img=1"),
            FriendItem(2, "Trâm", avatarUrl = "https://i.pravatar.cc/150?img=5"),
            FriendItem(3, "Long", avatarUrl = "https://i.pravatar.cc/150?img=8"),
            FriendItem(4, "Vy", avatarUrl = "https://i.pravatar.cc/150?img=10"),
            FriendItem(5, "Hùng", avatarUrl = "https://i.pravatar.cc/150?img=12")
        )

        val adapter = FriendsSelectAdapter(mockData)
        binding.recyclerFriends.adapter = adapter

    }

    private fun setupImage(uri: Uri) {
        binding.imagePreview.visibility = View.VISIBLE
        binding.videoPreview.visibility = View.GONE

        Glide.with(this).load(uri).into(binding.imagePreview)
    }

    private fun setupVideo(uri: Uri) {
        binding.imagePreview.visibility = View.GONE
        binding.videoPreview.visibility = View.VISIBLE
        binding.videoPreview.setVideoURI(uri)

        binding.videoPreview.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }
    }

    private fun setupButtons(uri: Uri, isVideo: Boolean) {

        binding.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonSend.setOnClickListener {
            val caption = binding.editTextCaption.text.toString()

            val adapter = binding.recyclerFriends.adapter
            if (adapter == null) {
                return@setOnClickListener
            }

            if (adapter !is FriendsSelectAdapter) {
                return@setOnClickListener
            }

            val selectedFriends = adapter.getSelectedFriends()

            if (selectedFriends.isEmpty()) {
                Toast.makeText(context, "Chọn ít nhất 1 người bạn!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val friendIds = selectedFriends.map { it.id.toString() }
            viewModel.sendPost(uri, isVideo, caption, friendIds)
        }

        binding.buttonDownload.setOnClickListener {
            lifecycleScope.launch {
                Toast.makeText(context, "Saving...", Toast.LENGTH_SHORT).show()

                MediaSaver.saveMediaToGallery(requireContext(), uri, isVideo)
            }
        }
        binding.buttonEditTools.setOnClickListener {
            showCaptionBottomSheet()
        }
    }

    private fun setupKeyboardHandling() {
        val rootView = binding.root

        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (_binding == null) return@OnGlobalLayoutListener

            val r = android.graphics.Rect()
            rootView.getWindowVisibleDisplayFrame(r)

            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                binding.layoutEditControls.visibility = View.GONE
                binding.recyclerFriends.visibility = View.GONE
                binding.textHeaderSend.visibility = View.GONE
                binding.buttonDownload.visibility = View.GONE

            } else {
                binding.layoutEditControls.visibility = View.VISIBLE
                binding.recyclerFriends.visibility = View.VISIBLE
                binding.textHeaderSend.visibility = View.VISIBLE
                binding.buttonDownload.visibility = View.VISIBLE

                binding.editTextCaption.clearFocus()
            }
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }


    private fun setupEditTextAction() {
        binding.editTextCaption.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                val imm =
                    requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun showCaptionBottomSheet() {
        val bottomSheet = CaptionBottomSheetFragment { selectedText ->

            if (selectedText.isNotEmpty()) {
                binding.editTextCaption.setText(selectedText)

                binding.editTextCaption.setSelection(selectedText.length)
            } else {
                binding.editTextCaption.setText("")
                binding.editTextCaption.requestFocus()
            }
        }

        bottomSheet.show(parentFragmentManager, "CaptionBottomSheet")
    }

    override fun onDestroyView() {
        if (globalLayoutListener != null) {
            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }
        super.onDestroyView()
        _binding = null
    }
}