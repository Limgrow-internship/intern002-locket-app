package com.intern002.locketapp.ui.screen.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.intern002.locketapp.ui.screen.home.HomeFragment
import com.intern002.locketapp.ui.screen.post.PostListFragment

class MainPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> PostListFragment()
            else -> HomeFragment()
        }
    }
}