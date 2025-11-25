package com.intern002.locketapp.ui.screen.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentMainContainerBinding

class MainContainerFragment : Fragment(R.layout.fragment_main_container) {

    private var _binding: FragmentMainContainerBinding? = null
    private val binding get() = _binding!!

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
        binding.layoutControls.setOnClickListener {
            Toast.makeText(context, "Mở Dashboard", Toast.LENGTH_SHORT).show()
        }

        binding.layoutControls.setOnClickListener {
            Toast.makeText(context, "Share Feed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}