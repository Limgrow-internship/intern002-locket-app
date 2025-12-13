package com.intern002.locketapp.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentAddWidgetBinding

class AddWidgetFragment : Fragment() {

    private var _binding: FragmentAddWidgetBinding? = null
    private val binding get() = _binding!!
    private val dots = mutableListOf<ImageView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWidgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupBackButton()
    }

    private fun setupViewPager() {
        val steps = listOf(
            AddWidgetStep(
                R.drawable.ic_add_widget_step_1,
                "Press and hold an empty space on your Home Screen"
            ),
            AddWidgetStep(
                R.drawable.ic_add_widget_step_2,
                "Tap the Widget button to add a new widget"
            ),
            AddWidgetStep(
                R.drawable.ic_add_widget_step_3,
                "Search and add the widget"
            )
        )

        val adapter = AddWidgetAdapter(steps)
        binding.viewPager.adapter = adapter

        setupDotsIndicator(steps.size)
        setCurrentDot(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentDot(position)
            }
        })
    }

    private fun setupDotsIndicator(count: Int) {
        dots.clear()
        binding.dotsIndicator.removeAllViews()

        for (i in 0 until count) {
            val dot = ImageView(requireContext())
            dot.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.dot_inactive)
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)

            binding.dotsIndicator.addView(dot, params)
            dots.add(dot)
        }
    }

    private fun setCurrentDot(position: Int) {
        dots.forEachIndexed { index, dot ->
            val drawableId = if (index == position) {
                R.drawable.dot_active
            } else {
                R.drawable.dot_inactive
            }
            dot.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), drawableId)
            )
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}