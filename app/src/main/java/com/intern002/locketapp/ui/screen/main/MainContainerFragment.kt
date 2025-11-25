package com.intern002.locketapp.ui.screen.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentMainContainerBinding
import com.intern002.locketapp.ui.screen.post.PostListFragment

class MainContainerFragment : Fragment(R.layout.fragment_main_container) {

    private var _binding: FragmentMainContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 👂 LẮNG NGHE KẾT QUẢ TỪ GRID (Khi user chọn ảnh xong quay về)
        childFragmentManager.setFragmentResultListener(
            "request_jump_to_post",
            this
        ) { key, bundle ->
            val index = bundle.getInt("post_index")

            binding.viewPagerMain.setCurrentItem(1, false)

            val feedFragment = childFragmentManager.findFragmentByTag("f1") as? PostListFragment
            feedFragment?.scrollToPosition(index)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainContainerBinding.bind(view)

        // 1. Setup ViewPager
        val adapter = MainPagerAdapter(this)
        binding.viewPagerMain.adapter = adapter
        binding.viewPagerMain.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
        binding.viewPagerMain.setCurrentItem(0, false)

        setupScrollSync()
        setupFeedControls()
    }

    private fun setupScrollSync() {
        binding.viewPagerMain.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)


                if (position == 0) {

                    binding.layoutControls.alpha = positionOffset

                    binding.layoutControls.visibility =
                        if (positionOffset > 0.1) View.VISIBLE else View.GONE

                } else if (position == 1) {
                    binding.layoutControls.alpha = 1f
                    binding.layoutControls.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupFeedControls() {
        binding.buttonDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_mainContainerFragment_to_gridPostFragment)
        }

        // Nút Share
        binding.buttonShare.setOnClickListener {
            Toast.makeText(context, "Share Feed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}