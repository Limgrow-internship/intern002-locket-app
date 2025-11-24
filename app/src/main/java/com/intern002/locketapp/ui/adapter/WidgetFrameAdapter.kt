package com.intern002.locketapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.WidgetFrame

class WidgetFrameAdapter(private val frames: List<WidgetFrame>, private val onFrameSelected: (WidgetFrame) -> Unit) :
    RecyclerView.Adapter<WidgetFrameAdapter.FrameViewHolder>() {

    inner class FrameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val frameBackground: View = itemView.findViewById(R.id.frame_background)
        private val lockIcon: ImageView = itemView.findViewById(R.id.iv_lock_icon)
        private val frameText: TextView = itemView.findViewById(R.id.tv_frame_text)
        private val radioButton: RadioButton = itemView.findViewById(R.id.radio_button)

        fun bind(frame: WidgetFrame) {
            frameBackground.setBackgroundResource(frame.backgroundRes)

            if (frame.displayText != null) {
                frameText.visibility = View.VISIBLE
                lockIcon.visibility = View.GONE
                frameText.text = frame.displayText
            } else {
                frameText.visibility = View.GONE
                lockIcon.visibility = if (frame.isLocked) View.VISIBLE else View.GONE
            }

            radioButton.isChecked = frame.isSelected

            itemView.setOnClickListener {
                onFrameSelected(frame)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_widget_frame, parent, false)
        return FrameViewHolder(view)
    }

    override fun onBindViewHolder(holder: FrameViewHolder, position: Int) {
        holder.bind(frames[position])
    }

    override fun getItemCount(): Int = frames.size
}
