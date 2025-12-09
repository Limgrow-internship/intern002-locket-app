package com.intern002.locketapp.ui.screen.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.Reactor
import com.intern002.locketapp.data.remote.model.reaction.ReactionTypeResponse
import com.intern002.locketapp.databinding.FragmentPostListBinding
import com.intern002.locketapp.ui.screen.main.MainContainerFragmentDirections
import com.intern002.locketapp.ui.screen.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostListFragment : Fragment(), PostItemCallBack {

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostListViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var pendingScrollPosition: Int? = null
    private var currentPostId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(context)

        setupViewModelObservers()
        setupClickListeners()
    }

    private fun setupViewModelObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userProfile.collectLatest { userProfile ->
                        if (userProfile != null) {
                            if (userProfile.avatarUrl.isNullOrEmpty()) {
                                binding.avatar.isVisible = false
                                binding.textAvatarInitial.isVisible = true
                                binding.textAvatarInitial.text = userProfile.username.first().uppercase()
                            } else {
                                binding.avatar.isVisible = true
                                binding.textAvatarInitial.isVisible = false
                                Glide.with(requireContext())
                                    .load(userProfile.avatarUrl)
                                    .placeholder(R.drawable.avt_sample)
                                    .error(R.drawable.avt_sample)
                                    .into(binding.avatar)
                            }
                        }
                    }
                }

                launch {
                    viewModel.currentUserId.collectLatest { myId ->
                        if (myId != null) {
                            setupAdapter(myId)
                        }
                    }
                }

                launch {
                    viewModel.reactionTypes.collectLatest { types ->
                        if (types.isNotEmpty()) {
                            setupReactionButtons(types)
                        }
                    }
                }
            }
        }

        mainViewModel.scrollRequest.observe(viewLifecycleOwner) { index ->
            if (index != null) {
                pendingScrollPosition = index
                consumePendingScroll()
                mainViewModel.scrollRequest.value = null
            }
        }

        lifecycleScope.launch {
            viewModel.reactionTypes.collectLatest { types ->
                if (types.isNotEmpty()) {
                    setupReactionButtons(types)
                }
            }
        }

        binding.btnMore.setOnClickListener {
            val allReactions = viewModel.reactionTypes.value

            if (allReactions.isNotEmpty()) {
                val pickerSheet = ReactionPickerFragment(allReactions) { selectedReaction ->
                    onReactionClicked(selectedReaction.id)
                    binding.root.postDelayed({ showFlyingEmoji(selectedReaction.emoji) }, 150)
                    binding.root.postDelayed({ showFlyingEmoji(selectedReaction.emoji) }, 300)
                    binding.root.postDelayed({ showFlyingEmoji(selectedReaction.emoji) }, 200)
                    binding.root.postDelayed({ showFlyingEmoji(selectedReaction.emoji) }, 120)
                }
                pickerSheet.show(parentFragmentManager, "ReactionPicker")
            } else {
                Toast.makeText(context, "Đang tải icon...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.avatarContainer.setOnClickListener {
            findNavController().navigate(R.id.action_mainContainerFragment_to_profileFragment)
        }

        binding.buttonChat.setOnClickListener {
            findNavController().navigate(R.id.action_mainContainerFragment_to_chatListFragment)
        }

        binding.btnMore.setOnClickListener {
            val allReactions = viewModel.reactionTypes.value
            if (allReactions.isNotEmpty()) {
                val pickerSheet = ReactionPickerFragment(allReactions) { selectedReaction ->
                    onReactionClicked(selectedReaction.id)
                    binding.root.postDelayed({ showFlyingEmoji(selectedReaction.emoji) }, 150)
                    binding.root.postDelayed({ showFlyingEmoji(selectedReaction.emoji) }, 300)
                    binding.root.postDelayed({ showFlyingEmoji(selectedReaction.emoji) }, 200)
                    binding.root.postDelayed({ showFlyingEmoji(selectedReaction.emoji) }, 120)
                }
                pickerSheet.show(parentFragmentManager, "ReactionPicker")
            } else {
                Toast.makeText(context, "Loading icon...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mainViewModel.refreshTrigger.value == true) {
            viewModel.refreshFeed()
            scrollToPosition(0)
            mainViewModel.refreshTrigger.value = false
        }
    }

    private fun setupAdapter(myUserId: String) {
        if (binding.recyclerViewPosts.adapter == null) {
            val adapter = PostAdapter(emptyList(), myUserId, this)
            binding.recyclerViewPosts.adapter = adapter

            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(binding.recyclerViewPosts)

            setupScrollConflict()
            observePosts(adapter)
            setupPagination()
            setupScrollListener()
        }
    }

    private fun observePosts(adapter: PostAdapter) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.posts.collectLatest { postList ->
                    adapter.updateData(postList)

                    if (postList.isNotEmpty()) {
                        binding.recyclerViewPosts.post {
                            if (pendingScrollPosition != null) {
                                consumePendingScroll()
                            } else {
                                checkCurrentVisibleItem()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupPagination() {
        binding.recyclerViewPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (totalItemCount <= lastVisibleItem + 3) {
                    viewModel.loadPosts(isRefresh = false)
                }
            }
        })
    }

    private fun setupScrollConflict() {
        val parentViewPager = requireActivity().findViewById<ViewPager2>(R.id.view_pager_main)

        binding.recyclerViewPosts.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            var startY = 0f

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startY = e.y
                        parentViewPager?.isUserInputEnabled = false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val isScrollingUp = e.y > startY
                        val isAtTop = !rv.canScrollVertically(-1)

                        if (isScrollingUp && isAtTop) {
                            parentViewPager?.isUserInputEnabled = true
                        } else {
                            parentViewPager?.isUserInputEnabled = false
                        }
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        parentViewPager?.isUserInputEnabled = true
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    private fun setupScrollListener() {
        binding.recyclerViewPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkCurrentVisibleItem()
                }
            }
        })
    }

    fun scrollToPosition(index: Int) {
        binding.recyclerViewPosts.post {
            val layoutManager = binding.recyclerViewPosts.layoutManager as? LinearLayoutManager

            if (layoutManager != null) {
                layoutManager.scrollToPositionWithOffset(index, 0)

                binding.recyclerViewPosts.postDelayed({
                    checkCurrentVisibleItem()
                }, 50)
            }
        }
    }

    override fun onPostTypeChanged(isMine: Boolean) {
        if (isMine) {
            binding.layoutReactionBar.visibility = View.GONE
        } else {
            binding.layoutReactionBar.visibility = View.VISIBLE
        }
    }

    override fun onShowReactions(reactors: List<Reactor>) {
        if (reactors.isNotEmpty()) {
            val bottomSheet = ReactionsBottomSheetFragment(reactors)
            bottomSheet.show(parentFragmentManager, "ReactionsSheet")
        } else {
            Toast.makeText(context, "No one has reacted yet 😢", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCurrentVisibleItem() {
        binding.recyclerViewPosts.post {
            val layoutManager =
                binding.recyclerViewPosts.layoutManager as? LinearLayoutManager ?: return@post
            val adapter = binding.recyclerViewPosts.adapter as? PostAdapter ?: return@post

            val snapHelper = PagerSnapHelper()
            val snapView = snapHelper.findSnapView(layoutManager)
            val position = snapView?.let { layoutManager.getPosition(it) }

            if (position != null && position != RecyclerView.NO_POSITION) {
                val currentList = adapter.getCurrentList()
                if (position in currentList.indices) {
                    val currentPost = currentList[position]
                    currentPostId = currentPost.id
                    adapter.updateVisibleItemType(currentPost)
                }
            }
        }
    }

    private fun consumePendingScroll() {
        val index = pendingScrollPosition ?: return
        val adapter = binding.recyclerViewPosts.adapter ?: return

        if (index >= 0 && index < adapter.itemCount) {
            scrollToPosition(index)
            pendingScrollPosition = null
        }
    }

    private fun setupReactionButtons(types: List<ReactionTypeResponse>) {
        binding.containerReactions.removeAllViews()

        val previewTypes = types.take(3)

        for (type in previewTypes) {
            val reactionBtn = ImageView(context)

            val params = LinearLayout.LayoutParams(
                dpToPx(32),
                dpToPx(32)
            )
            params.marginStart = dpToPx(8)
            reactionBtn.layoutParams = params

            reactionBtn.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
            reactionBtn.setBackgroundResource(R.color.grey_dark)

            val emojiBtn = TextView(context)
            emojiBtn.layoutParams = params
            emojiBtn.text = type.emoji
            emojiBtn.textSize = 20f
            emojiBtn.gravity = android.view.Gravity.CENTER
            emojiBtn.setBackgroundResource(R.color.grey_dark)

            emojiBtn.setOnClickListener {
                onReactionClicked(type.id)
                showFlyingEmoji(type.emoji)

                binding.root.postDelayed({ showFlyingEmoji(type.emoji) }, 150)
                binding.root.postDelayed({ showFlyingEmoji(type.emoji) }, 300)
                binding.root.postDelayed({ showFlyingEmoji(type.emoji) }, 200)
                binding.root.postDelayed({ showFlyingEmoji(type.emoji) }, 120)
            }

            binding.containerReactions.addView(emojiBtn)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun onReactionClicked(reactionId: Int) {
        if (currentPostId != null) {
            viewModel.reactToPost(currentPostId!!, reactionId)
        }
    }


    private fun showFlyingEmoji(emojiChuoi: String) {
        val emojiView = android.widget.TextView(requireContext())
        emojiView.text = emojiChuoi
        emojiView.textSize = 32f
        emojiView.gravity = android.view.Gravity.CENTER

        val params = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT,
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomToBottom =
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
        params.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomMargin = dpToPx(80)
        params.marginEnd = dpToPx(60)

        emojiView.layoutParams = params

        (binding.root as android.view.ViewGroup).addView(emojiView)


        val animatorY =
            android.animation.ObjectAnimator.ofFloat(emojiView, "translationY", 0f, -1200f)
        val animatorAlpha = android.animation.ObjectAnimator.ofFloat(emojiView, "alpha", 1f, 0f)
        val animatorScaleX =
            android.animation.ObjectAnimator.ofFloat(emojiView, "scaleX", 0.8f, 1.5f)
        val animatorScaleY =
            android.animation.ObjectAnimator.ofFloat(emojiView, "scaleY", 0.8f, 1.5f)
        val randomX = java.util.Random().nextFloat() * 300f - 150f
        val animatorX =
            android.animation.ObjectAnimator.ofFloat(emojiView, "translationX", 0f, randomX)
        val randomRotate = java.util.Random().nextFloat() * 90f - 45f
        val animatorRotate =
            android.animation.ObjectAnimator.ofFloat(emojiView, "rotation", 0f, randomRotate)


        val set = android.animation.AnimatorSet()
        set.playTogether(
            animatorY,
            animatorAlpha,
            animatorScaleX,
            animatorScaleY,
            animatorX,
            animatorRotate
        )
        set.duration = 1800
        set.interpolator = android.view.animation.DecelerateInterpolator()

        set.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                (binding.root as android.view.ViewGroup).removeView(emojiView)
            }
        })

        set.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}