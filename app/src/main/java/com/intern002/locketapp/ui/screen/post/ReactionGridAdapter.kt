package com.intern002.locketapp.ui.screen.post

import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intern002.locketapp.data.remote.model.reaction.ReactionTypeResponse

class ReactionGridAdapter(
    private val list: List<ReactionTypeResponse>,
    private val onClick: (ReactionTypeResponse) -> Unit
) : RecyclerView.Adapter<ReactionGridAdapter.ViewHolder>() {

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150)
            gravity = Gravity.CENTER
            textSize = 32f
            setTextColor(android.graphics.Color.WHITE)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.textView.text = item.emoji

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = list.size
}