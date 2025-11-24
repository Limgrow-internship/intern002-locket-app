package com.intern002.locketapp.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intern002.locketapp.R

data class AddWidgetStep(val imageResId: Int, val description: String)

class AddWidgetAdapter(private val context: Context, private val steps: List<AddWidgetStep>) :
    RecyclerView.Adapter<AddWidgetAdapter.AddWidgetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddWidgetViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_add_widget_step, parent, false)
        return AddWidgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddWidgetViewHolder, position: Int) {
        val step = steps[position]
        holder.stepImage.setImageResource(step.imageResId)
        holder.stepDescription.text = step.description
    }

    override fun getItemCount(): Int {
        return steps.size
    }

    class AddWidgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stepImage: ImageView = itemView.findViewById(R.id.iv_step_image)
        val stepDescription: TextView = itemView.findViewById(R.id.tv_description)
    }
}
