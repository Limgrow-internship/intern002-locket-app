package com.intern002.locketapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.CalendarDay

class CalendarAdapter(private val days: List<CalendarDay>) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayImage: ImageView = itemView.findViewById(R.id.iv_day_image)
        private val dayNumber: TextView = itemView.findViewById(R.id.tv_day_number)

        fun bind(day: CalendarDay) {
            if (day.isPlaceholder) {
                itemView.visibility = View.INVISIBLE
            } else {
                itemView.visibility = View.VISIBLE
                dayNumber.text = String.format("%02d", day.dayOfMonth)

                if (day.imageRes != null) {
                    dayImage.setImageResource(day.imageRes)
                    dayImage.visibility = View.VISIBLE
                } else {
                    dayImage.visibility = View.INVISIBLE
                }

                if (day.showPlusIcon) {
                    dayImage.setImageResource(R.drawable.ic_add)
                    dayImage.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int = days.size
}
