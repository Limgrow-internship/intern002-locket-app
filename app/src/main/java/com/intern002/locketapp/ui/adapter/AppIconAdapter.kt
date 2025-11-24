package com.intern002.locketapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.intern002.locketapp.R
import com.intern002.locketapp.data.model.AppIcon

class AppIconAdapter(private val icons: List<AppIcon>, private val onItemClick: (AppIcon) -> Unit) :
    RecyclerView.Adapter<AppIconAdapter.AppIconViewHolder>() {

    inner class AppIconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImage: ImageView = itemView.findViewById(R.id.iv_icon_image)
        private val radioButton: RadioButton = itemView.findViewById(R.id.radio_button)

        fun bind(appIcon: AppIcon) {
            iconImage.setImageResource(appIcon.iconRes)
            radioButton.isChecked = appIcon.isSelected

            itemView.setOnClickListener {
                onItemClick(appIcon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppIconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_icon, parent, false)
        return AppIconViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppIconViewHolder, position: Int) {
        holder.bind(icons[position])
    }

    override fun getItemCount(): Int = icons.size
}
