package com.intern002.locketapp.ui.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.intern002.locketapp.R
import com.intern002.locketapp.databinding.FragmentProfileBinding

data class DayData(val day: Int, val imageUrl: String? = null, val isPlaceholder: Boolean = false)

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mock data for October
        val octoberDays = createMockDataForOctober()
        populateGrid(binding.gridOctober, octoberDays)

        // Mock data for November
        val novemberDays = createMockDataForNovember()
        populateGrid(binding.gridNovember, novemberDays)
    }

    private fun populateGrid(grid: GridLayout, days: List<DayData>) {
        grid.post { // Use post to ensure the grid has been laid out and has a width
            val screenWidth = grid.width
            val columnWidth = screenWidth / grid.columnCount

            for (dayData in days) {
                val cellView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.layout_day_cell, grid, false)

                val dayText = cellView.findViewById<TextView>(R.id.tv_day_number)
                val dayImage = cellView.findViewById<ImageView>(R.id.iv_day_image)

                if (dayData.isPlaceholder) {
                    cellView.visibility = View.INVISIBLE // Keep the space, but make it invisible
                } else {
                    dayText.text = String.format("%02d", dayData.day)

                    if (dayData.imageUrl != null) {
                        // Here you would load the image with Glide/Picasso
                        // For now, we'll just show the sample avatar if an image is supposed to be there
                        dayImage.setImageResource(R.drawable.avt_sample)
                        dayImage.alpha = 1f
                    } else {
                        // No image, make it just a gray box with a number
                        dayImage.alpha = 0.2f // Make it more transparent
                        dayImage.setImageResource(0) // Remove any image resource
                    }
                }

                val params = GridLayout.LayoutParams().apply {
                    width = columnWidth
                    height = columnWidth
                    setMargins(0, 0, 0, 0)
                }
                cellView.layoutParams = params
                grid.addView(cellView)
            }
        }
    }

    private fun createMockDataForOctober(): List<DayData> {
        // October 2025 starts on a Wednesday (position 3)
        val days = mutableListOf<DayData>()
        // Add placeholders for empty days at the start of the month
        for (i in 0 until 3) { days.add(DayData(0, isPlaceholder = true)) }

        days.addAll(listOf(
            DayData(1), DayData(2), DayData(3), DayData(4), DayData(5), DayData(6), DayData(7),
            DayData(8), DayData(9), DayData(10), DayData(11), DayData(12, "url"), DayData(13, "url"), DayData(14, "url"),
            DayData(15, "url"), DayData(16), DayData(17, "url"), DayData(18, "url"), DayData(19), DayData(20, "url"), DayData(21),
            DayData(22), DayData(23, "url"), DayData(24), DayData(25, "url"), DayData(26, "url"), DayData(27), DayData(28),
            DayData(29), DayData(30), DayData(31, "url")
        ))
        return days
    }

    private fun createMockDataForNovember(): List<DayData> {
        // November 2025 starts on a Saturday (position 6)
        val days = mutableListOf<DayData>()
        for (i in 0 until 6) { days.add(DayData(0, isPlaceholder = true)) }

        days.addAll(listOf(
            DayData(1), DayData(2), DayData(3), DayData(4), DayData(5), DayData(6), DayData(7),
            DayData(8), DayData(9), DayData(10), DayData(11), DayData(12), DayData(13), DayData(14),
            DayData(15), DayData(16), DayData(17, "url"), DayData(18), DayData(19), DayData(20), DayData(21),
            DayData(22), DayData(23), DayData(24), DayData(25), DayData(26), DayData(27), DayData(28),
            DayData(29), DayData(30)
        ))
        return days
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
