package com.intern002.locketapp.ui.screen.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.intern002.locketapp.R
import com.intern002.locketapp.data.datasource.MockData
import com.intern002.locketapp.data.remote.model.Post
import com.intern002.locketapp.data.remote.model.Reactor
import com.intern002.locketapp.databinding.FragmentPostListBinding

class PostListFragment : Fragment(), PostItemCallBack {

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

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

        val postList = MockData.posts

        // 1. SETUP ADAPTER (Truyền "this" vào làm callback)
        val adapter = PostAdapter(postList, "me", this)
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewPosts.adapter = adapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerViewPosts)

        setupScrollListener(adapter, snapHelper, postList)
        setupScrollConflict()
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

    private fun setupScrollListener(
        adapter: PostAdapter,
        snapHelper: PagerSnapHelper,
        postList: List<Post>
    ) {
        binding.recyclerViewPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // Chỉ kiểm tra khi RecyclerView đã dừng hẳn
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView = snapHelper.findSnapView(recyclerView.layoutManager)
                    val position = snapView?.let { recyclerView.layoutManager?.getPosition(it) }

                    if (position != null && position >= 0) {
                        val currentPost = postList[position]

                        adapter.updateVisibleItemType(currentPost)
                    }
                }
            }
        })
    }

    override fun onPostTypeChanged(isMine: Boolean) {
        val replyBar = binding.layoutReactionBar

        if (isMine) {
            replyBar.isVisible = false
        } else {
            replyBar.isVisible = true
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

    // Hàm này để MainContainer gọi khi chọn ảnh từ Grid (Chức năng Jump to Post)
    fun scrollToPosition(index: Int) {
        binding.recyclerViewPosts.postDelayed({
            binding.recyclerViewPosts.scrollToPosition(index)
        }, 100)
    }

    override fun onPause() {
        super.onPause()
        // KHI RỜI KHỎI MÀN HÌNH FEED (Về Camera hoặc tắt app)
        // -> Reset cuộn về vị trí đầu tiên (0) ngay lập tức
        binding.recyclerViewPosts.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}