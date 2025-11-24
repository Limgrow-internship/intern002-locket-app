package com.intern002.locketapp.ui.screen.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.intern002.locketapp.databinding.FragmentMainContainerBinding

class MainContainerFragment : Fragment() {

    private var _binding: FragmentMainContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewPager
        val adapter = MainPagerAdapter(this)
        binding.viewPagerMain.adapter = adapter

        binding.viewPagerMain.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

        binding.viewPagerMain.setCurrentItem(0, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}