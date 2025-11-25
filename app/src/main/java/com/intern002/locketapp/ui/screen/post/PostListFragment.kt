package com.intern002.locketapp.ui.screen.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.intern002.locketapp.R
import com.intern002.locketapp.data.datasource.MockData
import com.intern002.locketapp.databinding.FragmentPostListBinding

class PostListFragment : Fragment() {

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

        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(context)
        val adapter = PostAdapter(MockData.posts)
        binding.recyclerViewPosts.adapter = adapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerViewPosts)

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