package com.intern002.locketapp.ui.screen.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.Reactor
import com.intern002.locketapp.databinding.FragmentPostListBinding
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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentUserId.collectLatest { myId ->
                    if (myId != null) {
                        setupAdapter(myId)
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
                // Khi lướt xong và dừng lại (IDLE)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 👇 CHỈ CẦN GỌI DÒNG NÀY THÔI
                    // (Nó sẽ tự tìm view đang snap và update UI)
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
            Toast.makeText(context, "Chưa có ai thả tim cả huhu 😢", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}