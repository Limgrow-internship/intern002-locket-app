package com.intern002.locketapp.ui.screen.language

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern002.locketapp.R
import com.intern002.locketapp.data.remote.model.Language

class LanguageAdapter(
    private var items: List<Language>,
    private val listener: OnLanguageClickListener
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    interface OnLanguageClickListener {
        fun onLanguageClick(language: Language)
    }

    inner class LanguageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val flag: ImageView = view.findViewById(R.id.iv_flag)
        val name: TextView = view.findViewById(R.id.tv_language_name)
        val check: ImageView = view.findViewById(R.id.iv_check)
        val container: ConstraintLayout = view.findViewById(R.id.language_item_container)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previouslySelected = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previouslySelected)
                    notifyItemChanged(selectedPosition)
                    listener.onLanguageClick(items[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val lang = items[position]
        holder.name.text = lang.name
        Glide.with(holder.itemView)
            .load(lang.flagUrl)
            .placeholder(R.drawable.ic_flag_placeholder)
            .error(R.drawable.ic_flag_placeholder)
            .into(holder.flag)

        if (position == selectedPosition) {
            holder.check.visibility = View.VISIBLE
            holder.container.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.selected_language_background))
        } else {
            holder.check.visibility = View.GONE
            holder.container.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Language>) {
        val diffCallback = LanguageDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    class LanguageDiffCallback(
        private val oldList: List<Language>,
        private val newList: List<Language>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}